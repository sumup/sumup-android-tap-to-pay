package com.sumup.taptopay.sampleapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sumup.taptopay.sampleapp.MainViewState

@Composable
@Suppress("LongParameterList")
fun MainPaymentScreen(
    amount: String,
    eventLog: @Composable () -> Unit,
    onAmountChanged: (String) -> Unit,
    onStartPayment: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
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

            eventLog()
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .imePadding(),
            onClick = {
                onStartPayment()
            },
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
        eventLog = { Text("Event log...") },
        onAmountChanged = {},
        onStartPayment = {},
        onLogout = {},
    )
}
