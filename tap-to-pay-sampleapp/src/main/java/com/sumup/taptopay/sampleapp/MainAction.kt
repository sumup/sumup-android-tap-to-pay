package com.sumup.taptopay.sampleapp

internal sealed interface MainAction {
    data class StartPayment(val amount: Long) : MainAction
    data object Teardown : MainAction
}
