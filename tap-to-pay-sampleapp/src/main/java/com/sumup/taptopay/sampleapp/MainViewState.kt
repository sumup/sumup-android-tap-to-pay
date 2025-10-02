package com.sumup.taptopay.sampleapp

private const val DEFAULT_AMOUNT = 1599L

sealed interface MainViewState {
    data object Loading: MainViewState
    data object Ready : MainViewState
    data class Processing(val message: String) : MainViewState
    data class Error(val message: String) : MainViewState
}