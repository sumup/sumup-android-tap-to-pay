package com.sumup.taptopay.sampleapp

internal sealed interface MainAction {
    class UpdateAmount(val amount: Long) :MainAction
    data object StartPayment : MainAction
    data object Teardown : MainAction
}
