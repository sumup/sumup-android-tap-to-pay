package com.sumup.taptopay.sampleapp.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sumup.taptopay.payment.domain.model.api.CheckoutConfig

private val PayButtonBottomInset = 80.dp

@Composable
@Suppress("LongParameterList")
fun MainPaymentScreen(
    amount: String,
    skipSuccessScreen: Boolean,
    timeoutCardWaitInput: String,
    timeoutCardWaitInvalid: Boolean,
    eventLog: @Composable () -> Unit,
    onAmountChanged: (String) -> Unit,
    onSkipSuccessScreenChanged: (Boolean) -> Unit,
    onTimeoutCardWaitChanged: (String) -> Unit,
    onStartPayment: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(bottom = PayButtonBottomInset)
                .verticalScroll(rememberScrollState())
        ) {
            TextButton(onClick = { onLogout() }) { Text("Logout") }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { onAmountChanged(it) },
                textStyle = MaterialTheme.typography.displayLarge,
                visualTransformation = CurrencyAmountInputVisualTransformation(
                    fixedCursorAtTheEnd = true
                ),
                suffix = { Text("€", style = MaterialTheme.typography.displayLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip success screen", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = skipSuccessScreen,
                    onCheckedChange = onSkipSuccessScreenChanged,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = timeoutCardWaitInput,
                label = { Text("Timeout card wait (seconds)") },
                placeholder = { Text("Default: ${CheckoutConfig.DEFAULT_TIMEOUT_CARD_WAIT_SECONDS}s") },
                isError = timeoutCardWaitInvalid,
                supportingText = {
                    Text(
                        "Range: ${CheckoutConfig.MIN_TIMEOUT_CARD_WAIT_SECONDS}-" +
                            "${CheckoutConfig.MAX_TIMEOUT_CARD_WAIT_SECONDS}s. Leave empty for default."
                    )
                },
                onValueChange = { value ->
                    if (value.isEmpty() || value.all { it.isDigit() }) {
                        onTimeoutCardWaitChanged(value)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            eventLog()
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = onStartPayment,
        ) {
            Text("Make Payment")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainPaymentScreen(
        amount = "12345",
        skipSuccessScreen = false,
        timeoutCardWaitInput = "",
        timeoutCardWaitInvalid = false,
        eventLog = { Text("Event log...") },
        onAmountChanged = {},
        onSkipSuccessScreenChanged = {},
        onTimeoutCardWaitChanged = {},
        onStartPayment = {},
        onLogout = {},
    )
}
