package com.sumup.taptopay.sampleapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sumup.taptopay.TapToPay
import com.sumup.taptopay.auth.AuthTokenProvider
import com.sumup.taptopay.payment.domain.model.CheckoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val tapToPay: TapToPay
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainViewState> = MutableStateFlow(MainViewState())
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    private inline val currentState: MainViewState
        get() = _uiState.value

    init {
        initTapToPay()
    }

    fun dispatch(action: MainAction) {
        when (action) {
            is MainAction.StartPayment -> startPayment()
            is MainAction.UpdateAmount -> updateAmount(action.amount)
            is MainAction.Teardown -> teardown()
        }
    }

    private fun teardown() {
        viewModelScope.launch {
            tapToPay.tearDown()
                .onSuccess {
                    _uiState.emit(currentState.copy(isLoading = true))
                    initTapToPay()
                }
        }
    }

    private fun updateAmount(amount: Long) {
        viewModelScope.launch {
            _uiState.emit(
                currentState.copy(
                    isLoading = false,
                    paymentData = currentState.paymentData.copy(amount = amount)
                )
            )
        }
    }

    private fun startPayment() {
        viewModelScope.launch {
            tapToPay.startPayment(
                CheckoutData(
                    totalAmount = currentState.paymentData.amount,
                    clientUniqueTransactionId = "123",
                    customItems = null,
                    priceItems = null
                )
            ).collectLatest {
                Log.d("MainViewModel", "Payment event: $it")
            }
        }
    }

    private fun initTapToPay() {
        viewModelScope.launch {
            tapToPay.init(
                object : AuthTokenProvider {
                    override fun getAccessToken(): String = "An access token or API token"
                }
            )
            _uiState.emit(
                currentState.copy(
                    isLoading = false,
                )
            )
        }
    }
}

internal class MainViewModelFactory(
    private val tapToPay: TapToPay,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            tapToPay = tapToPay,
        ) as T
    }
}
