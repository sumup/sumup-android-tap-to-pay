package com.sumup.taptopay.sampleapp

private const val DEFAULT_AMOUNT = 1599L

internal data class MainViewState(
    val isLoading: Boolean = true,
    val paymentData: Payment = Payment(),
    val error: Error? = null,
) {
    internal data class Error(val errorMessage: String)
    internal data class Payment(
        val amount: Long = DEFAULT_AMOUNT,
    )
}
