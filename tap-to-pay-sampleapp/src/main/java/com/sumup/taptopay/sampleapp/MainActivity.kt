package com.sumup.taptopay.sampleapp

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sumup.taptopay.TapToPayApiProvider
import com.sumup.taptopay.sampleapp.screen.MainPaymentScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels(
        factoryProducer = {
            MainViewModelFactory(
                tapToPay = TapToPayApiProvider.provide(applicationContext),
            )
        }
    )

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            MaterialTheme{
                val state: MainViewState by viewModel.uiState.collectAsState()
                val scrollBehavior =
                    TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                val totalAmount = remember { mutableLongStateOf(100L) }

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    "Demo App",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                ) { paddings ->
                    Box(
                        modifier = Modifier
                            .padding(paddings)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val s = state) {
                            MainViewState.Loading -> CircularProgressIndicator()
                            is MainViewState.Error -> MainState(
                                totalAmount = totalAmount,
                                eventLog = {
                                    Text(
                                        "Error: ${s.message}",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                            .verticalScroll(rememberScrollState())
                                    )
                                },
                            )
                            is MainViewState.Processing -> MainState(
                                totalAmount = totalAmount,
                                eventLog = {
                                    Text(
                                        "Processing payment...\n ${s.message}",
                                        modifier = Modifier.padding(16.dp)
                                            .verticalScroll(rememberScrollState())
                                    )
                                },
                            )
                            MainViewState.Ready -> MainState(
                                totalAmount = totalAmount,
                                eventLog = { Text("Ready to process payments") },
                            )
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun MainState(
        totalAmount: MutableLongState,
        eventLog: @Composable () -> Unit = { Text("No events yet") },
    ) {
        MainPaymentScreen(
            amount = totalAmount.longValue.toString(),
            eventLog = eventLog,
            onAmountChanged = { amount ->
                totalAmount.longValue = if (amount.isEmpty()) {
                    0L
                } else {
                    amount.toLong()
                }
            },
            onStartPayment = {
                viewModel.dispatch(
                    MainAction.StartPayment(totalAmount.longValue)
                )
            },
            onLogout = {
                viewModel.dispatch(MainAction.Teardown)
            }
        )
    }
}
