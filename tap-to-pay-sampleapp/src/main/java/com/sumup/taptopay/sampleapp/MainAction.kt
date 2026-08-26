package com.sumup.taptopay.sampleapp

internal sealed interface MainAction {
    data class StartPayment(val amount: Long) : MainAction
    data object Teardown : MainAction
    data class SkipSuccessScreen(val skip: Boolean) : MainAction
    data class UpdateTimeoutCardWait(val input: String) : MainAction
}
