package com.sumup.taptopay.sampleapp

import com.sumup.taptopay.payment.domain.model.api.CheckoutConfig

data class PaymentOptions(
    val skipSuccessScreen: Boolean = false,
    val timeoutCardWaitInput: String = "",
) {
    fun timeoutCardWaitSeconds(): Int? {
        val parsed = timeoutCardWaitInput.toIntOrNull() ?: return null
        return parsed.coerceIn(
            CheckoutConfig.MIN_TIMEOUT_CARD_WAIT_SECONDS,
            CheckoutConfig.MAX_TIMEOUT_CARD_WAIT_SECONDS,
        )
    }

    fun isTimeoutCardWaitInvalid(): Boolean {
        if (timeoutCardWaitInput.isEmpty()) return false
        val parsed = timeoutCardWaitInput.toIntOrNull() ?: return true
        return parsed < CheckoutConfig.MIN_TIMEOUT_CARD_WAIT_SECONDS ||
            parsed > CheckoutConfig.MAX_TIMEOUT_CARD_WAIT_SECONDS
    }
}
