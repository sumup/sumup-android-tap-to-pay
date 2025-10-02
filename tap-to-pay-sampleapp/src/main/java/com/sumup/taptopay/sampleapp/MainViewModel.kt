package com.sumup.taptopay.sampleapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sumup.taptopay.TapToPay
import com.sumup.taptopay.auth.AuthTokenProvider
import com.sumup.taptopay.payment.domain.model.api.CheckoutData
import com.sumup.taptopay.payment.domain.model.api.PaymentEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

internal class MainViewModel(
    private val tapToPay: TapToPay
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainViewState> = MutableStateFlow(MainViewState.Loading)
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    init {
        initTapToPay()
    }

    fun dispatch(action: MainAction) {
        when (action) {
            is MainAction.StartPayment -> startPayment(action.amount)
            is MainAction.Teardown -> teardown()
        }
    }

    private fun teardown() {
        viewModelScope.launch {
            tapToPay.tearDown()
                .onSuccess {
                    _uiState.emit(MainViewState.Loading)
                    initTapToPay()
                }
        }
    }

    private fun startPayment(amount: Long) {
        viewModelScope.launch {
            _uiState.emit(MainViewState.Processing(""))


            tapToPay.startPayment(
                CheckoutData(
                    totalAmount = amount,
                    clientUniqueTransactionId = UUID.randomUUID().toString(),
                    tipsAmount = null,
                    vatAmount = null,
                    customItems = null,
                    products = null,
                    priceItems = null,
                    processCardAs = null,
                    affiliateData = null
                )
            ).catch {
                _uiState.emit(
                    MainViewState.Error(
                        message = it.message ?: "Unknown error"
                    )
                )
                Log.e("MainViewModel", "Payment error: $it")
            }.collectLatest { paymentEvent ->

                _uiState.update { previousState ->
                    MainViewState.Processing(
                        buildString {
                            if (previousState is MainViewState.Processing) {
                                append(previousState.message)
                            }
                            append("\n\n")
                            append("Payment Event: $paymentEvent")
                        }
                    )
                }
                Log.d("MainViewModel", "Payment event: $paymentEvent")
            }
        }
    }

    private fun initTapToPay() {
        viewModelScope.launch {
            /*
                NOTE:
                Before testing, make sure of the following:
                1. The app is not debuggable (isDebuggable = false in build.gradle).
                2. You have USB debugging disabled on your device. Even if you install the app through a cable, disable the USB debugging after installation.
                3. You have Developer Mode disabled on your device. Even if you install the app through a cable, disable the Developer Mode after installation.

                On some devices (e.g. Samsung), you still have to disable USB debugging before disabling the Developer Mode.
                It is still possible to have a USB debugging enabled and Developer Mode disabled, but it depends on the device manufacturer.
             */
            tapToPay.init(
                object : AuthTokenProvider {
                    override fun getAccessToken(): String = "An access token or API token"
                }
            ).onSuccess {
                Log.d("MainViewModel", "Tap to Pay initialized successfully")
                _uiState.emit(MainViewState.Ready)
            }.onFailure {
                Log.e("MainViewModel", "Tap to Pay init error: $it")
                _uiState.emit(MainViewState.Error(
                    message = it.message ?: "Unknown error while initializing Tap to Pay"
                ))
            }
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
