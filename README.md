
# Tap to Pay on Android SDK

Tap to Pay on Android SDK enables your mobile app to accept card-present contactless payments with a smartphone only, without the need for any additional hardware.
This repository provides a step-by-step guide on how you can integrate this functionality into your Android mobile app.

- Transactions are performed in the production environment.
- Used for live, customer-facing operations.
- The SDK is not debuggable. Additionally, **Attestation & Monitoring** is enabled. If a device doesn’t meet our threat policy (for example, if debug mode is enabled or the device is rooted) then payment operations will not be available.
- For testing purposes, use dedicated developer credentials to prevent actual card charges. You can request access by contacting integration@sumup.com.

# Table of Contents

1. [Overview](#tap-to-pay-on-android-sdk)
2. [Integration](#integration)  
   2.1. [Setup](#setup)  
   2.2. [Prerequisites](#prerequisites)  
   2.3. [Dependency](#dependency)  
3. [Authentication](#authentication)  
4. [API](#api)  
   4.1. [Initialization](#1-initialization)  
   4.2. [Start Payment](#2-start-payment)  
   4.3. [Tear Down](#3-tear-down)  
   4.4. [Exceptions](#exceptions)

## Integration

You can use the sample app provided in this repository as a reference.

### Setup

To integrate the Tap to Pay on Android SDK you will need:
- maven repository credentials (to access the SDK repository, can be requested by sending an email to integration@sumup.com).
- a Secret API key, in case you don't intend to use the OAuth2 authentication process. You can generate a Secret API key in the [SumUp Dashboard](https://developer.sumup.com/online-payments/introduction/authorization#api-keys)

### Prerequisites
- Kotlin version: 1.9.22 or later
- `minSDK`: 30 or later
- `targetSDK`/`compileSDK`: 34 or later
- Android Gradle Plugin: 7.3.0 or later
- Java 17 or later

### Hardware requirements
- NFC-enabled Android device
- Android 11 or later

### Dependency

Add the following to the Gradle dependencies:

```kotlin  
allprojects {
    repositories {
        maven {
            url = uri("https://maven.sumup.com/releases")
        }
        maven {
            url = uri("https://tap-to-pay-sdk.fleet.live.sumup.net/")
            credentials {
                username = "your_username"  // The maven credentials are provided by SumUp
                password = "your_password"
            }
        }
        google()
        mavenCentral()
    }
}
```  

Add the dependency to a module `build.gradle`:

```kotlin 
implementation("com.sumup.tap-to-pay:utopia-sdk:0.15.0")
```  

### Authentication

Tap to Pay on Android SDK uses the transparent authentication approach. 
It means that the SDK is not responsible for the authentication process. 
The authentication process is handled by the consuming app. 
The SDK provides the `init` [method](#1-initialization) with the `AuthTokenProvider` interface as a parameter. 
The `AuthTokenProvider` interface is responsible for providing the access token to the SDK.

```kotlin
interface AuthTokenProvider {
    fun getAccessToken(): String
}
```

There are several ways for a consumer app to provide the access token to the SDK.

1. Using the OAuth2 [flow](https://developer.sumup.com/online-payments/introduction/authorization#o-auth-2-0):
The consumer app can implement the OAuth2 flow to get the access token and provide it to the SDK. The SDK provides the `AuthTokenProvider` interface that should be implemented by the consumer app. The implementation of the `getAccessToken` method should return the access token. This way is preferable and recommended because it provides a more secure way to authenticate the user.

2. Using Secret API key:
It is possible to use a Secret API key as an auth token. It can be generated in the [SumUp Dashboard](https://developer.sumup.com/online-payments/introduction/authorization#api-keys) and provided to the SDK through `AuthTokenProvider.getAccessToken()` method.

> ⚠️ **Important:**
>  The API keys should be stored securely and should not be hardcoded in the app. Instead, they should be stored in the secure storage and provided to the SDK when needed. Do not share your secret API keys in publicly accessible places such as GitHub repositories, client-side code, etc.

### API

The `TapToPay` interface provides methods to interact with the SDK. To get an implementation of the `TapToPay` interface, call:

```kotlin
val tapToPay = TapToPayApiProvider.provide(applicationContext)
```
where `applicationContext` is the context of a consumer application.

The `TapToPay` interface has the following methods:

#### 1. Initialization

```kotlin
suspend fun init(authTokenProvider: AuthTokenProvider): Result<Unit>
```

The `init` method initializes the session. The `AuthTokenProvider` interface is responsible for providing the access token to the SDK (see [here](#Authentication)).
Please, note that the `init` method should be called only once during the app lifecycle. The `init` method should be called as soon as possible after the app starts.

The `init` function returns a `Result` object that can be either a `Result.Success` if the initialization was successful.
The function can also return `Result.Failure` with one exception from the list of exceptions mentioned [here](#Exceptions).

#### 2. Start Payment

```kotlin
suspend fun startPayment(checkoutData: CheckoutData): Flow<Result<PaymentEvent>>
```

The `startPayment` method initiates the payment process. It returns a `Flow` of `Result` objects that can be either a `Result.Success` with the `PaymentEvent` or a `Result.Failure` with an error message.

The list of possible events:

- `CardRequested` - the SDK is trying to detect a card, waiting for the cardholder to tap/present their card.
- `CardPresented` - a card is detected.
- `CVMRequested` - a CVM (Cardholder Verification Method) is requested. This event is fired when the card is detected and the SDK is waiting for the cardholder to enter the PIN.
- `CVMPresented` - a CVM was entered by the cardholder. This event is fired upon completion of the CVM regardless if it was successful or not.
- `TransactionDone(val paymentOutput: PaymentOutput)` - transaction was completed. `PaymentOutput` param is:
  ```kotlin
  data class PaymentOutput(
      val txCode: String,
      val serverTransactionId: String
  )
  ```
- `TransactionFailed(val paymentOutput: PaymentOutput?)` - transaction failed. It might happen due to many reasons, like backend error, card reader error, and so on.
- `TransactionCanceled(val paymentOutput: PaymentOutput?)` - transaction was cancelled by the user.
- `TransactionResultUnknown(val paymentOutput: PaymentOutput?)` - transaction result is unknown. This might happen on remote calls, when there is no response due to timeout.
- `PaymentFlowClosedSuccessfully(val paymentOutput: PaymentOutput?, val shouldDisplayReceipt: Boolean)` - after a successful transaction, users see the successful screen with two buttons: **Send receipt** and **Done**. Once the user clicks on any button, the screen closes and fires the `PaymentClosed` event.

The function can also return `Result.Failure` with one exception from the list of exceptions mentioned [here](#Exceptions).

##### Parameters

`checkoutData` - The checkout data object.

```kotlin
data class CheckoutData(
    val totalAmount: Long,
    val tipsAmount: Long?,
    val vatAmount: Long?,
    val clientUniqueTransactionId: String,
    val customItems: List<CustomItem>?,
    val priceItems: List<PriceItem>?,
    val processCardAs: ProcessCardAs?,
) : Serializable
```

Where:
- `totalAmount` - The amount expressed in the minor unit of the currency. Total amount includes tip amount and VAT amount.
- `tipsAmount` - The amount of tips expressed in the minor unit of the currency. Please, note that the tip amount is included in the total amount. Ignored if null.
- `vatAmount` - The amount of VAT expressed in the minor unit of the currency. Please, note that the VAT amount is included in the total amount. Ignored if null.
- `clientUniqueTransactionId` - This should be a unique identifier for the transaction. A random UUID is can be used.
- `customItems` - The list of custom items. Set null if not used.
- `priceItems` - The list of price items. Set null if not used.
- `processCardAs` - The type of the card processing. The default value is `null`. The possible values are `ProcessCardAs.Credit(val instalments: Int)` and `ProcessCardAs.Debit`, where `instalments` is the number of instalments. This parameter is optional and only applicable to some markets, such as Brazil, where the card type selection and instalments are supported.

**Note:** The amounts shall be provided in minor unit of the currency according to the list below.    
Currencies with exponent 2 : `AUD, BGN, BRL, CHF, CLP, COP, CZK, DKK, EUR, GBP, HRK, HUF, NOK, PEN, PLN, RON, SEK, USD`.

For example, an amount of `$12.34` corresponds to a value of `1234`, `$11.00` corresponds to a value of `1100`.

**Note 2:** Some currencies (Hungarian Forint `HUF`, Chilean Peso `CLP` and Colombian Peso `COP`) are displayed to the merchant and cardholder without minor unit of the currency but still require it.

For these specific currencies, the amount shall still be multiplied by 100 (exponent 2).
For example, `Ft100` should be provided as `10000`.


The required minimum to make the transaction looks like:

```kotlin
fun startPayment() {
    tapToPay.startPayment(
        CheckoutData(
            totalAmount = 1234, // 12.34 EUR
            clientUniqueTransactionId = "123",
            tipsAmount = null,
            vatAmount = null,
            customItems = null,
            priceItems = null,
            processCardAs = null,
        )
    ).collectLatest {
        Log.d("Payment event: $it")
    }
}
```

#### 3. Tear Down

```kotlin
suspend fun tearDown(): Result<Unit>
```

The `tearDown` function logs out the user, cleans up keys and other sensitive data, and closes the session. 
The `tearDown` method should be called when the app is closed or when the user logs out.
It returns a `Result` object that can be either a `Result.Success` if the teardown was successful or a `Result.Failure` if there was an error during the teardown.

#### Exceptions

The SDK may return a `Result.Failure` containing an exception when one of its methods is called. 
Every exception belongs to one of the base types. 
The base types are listed below, and each of these is further divided into more specific exception types.

- `CommonException` - These exceptions cover scenarios such as initialization issues, registration problems, authentication failures, and required updates, providing a consistent and predictable way to handle errors across the system.
- `NetworkException` - These exceptions represent network-related and communication errors encountered during SDK operation. They include issues such as interrupted connections, authentication problems, and server/client-side failures.
- `PaymentException` - These exceptions represent errors related to the payment transaction flow, covering everything from preprocessing to final charge attempts. They include issues such as invalid payment actions, timeouts, incorrect amounts, unsupported card technologies, and unexpected states during card reading.
- `PaymentPreparationException` - These exceptions relate to the preparation and availability of the payment process. They indicate failures such as the unavailability of the payment function, issues during kernel setup, missing security-related data, and general checkout failures. These errors typically occur before or at the start of a transaction and prevent it from proceeding.
- `TapToPayException.Unknown` - The Unknown exception represents an internal error that cannot be exposed externally. It acts as a fallback for unexpected or unclassified issues that occur within the SDK, ensuring sensitive or implementation-specific details are not leaked.

You can find full list of errors with codes [here](docs/ERRORS.md)
