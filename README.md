
# TapToPay SDK

This repository provides step-by-step documentation for SumUp's TapToPay SDK, which enables users to use any Android phone as a contactless reader. This is an early version (v0.1.0-DEV) of the SDK, which contains a set of limitations that will be addressed in future versions. The SDK currently works only in DEV mode and is intended for testing purposes.

## Limitations

1. **Environment Restriction**: The SDK works only in the SumUp `Staging` environment. For security reasons, this set is configured for the Staging environment only. The real transactions are not processed in the Staging environment.

2. **Market Restriction**: The SDK uses hardcoded payment configurations for the German (DE) market. Due to this, it is only possible to use German test user. 

3. **Feature Limitations**: The TapToPay API doesn't currently support all the features that exist in the MerchantApp, such as tips and installments.

## Integration of the TapToPay SDK

You can use the sample app provided in this repository as a reference.

### Prerequisites
- Kotlin version: 1.9.22 or later
- `minSDK`: 30 or later
- `targetSDK`/`compileSDK`: 34 or later
- Android Gradle Plugin: 7.3.0 or later
- Java 17 or later

### Dependency

Add the following to the Gradle dependencies:

```kotlin  
allprojects {  
    repositories {  
        maven {  
            url = uri("https://maven.sumup.com/releases")  
        }  
        maven {  
            url = uri("https://tap-to-pay-sdk.fleet.dev.sumup.net/")  
            credentials {  
                username = "your_username"  // The credentials are provided by SumUp
                password = "your_password"  
            }  
        }        
        google()  
        mavenCentral()  
    }  
}
```  

Add the dependency to a module `build.gradle`:

```groovy  
implementation("com.sumup.tap-to-pay:utopia-sdk:0.1.0-DEV")
```  

### Authentication

1. The Tap to Pay feature requires a SumUp merchant account. To authenticate the user, the SDK uses the Single Sign-On (SSO) approach.
2. If you don't have a SumUp merchant account, you can create one [here](https://sumup.com/).
3. The authentication approach is going to be changed in the future versions of the SDK.

### API

The `TapToPay` interface provides methods to interact with the Tap to Pay feature. To receive the implementation of the `TapToPay` interface, call:

```kotlin  
val tapToPay = TapToPayApiProvider.provide()  
```  

The `TapToPay` interface has the following methods:

#### 1. Initialization

```kotlin  
suspend fun init(): Result<Unit>  
```  

The `init` method initializes the session and logs in a registered SumUp merchant account if needed. Please note that only SSO login is allowed.

The `init` function returns a `Result` object that can be either a `Result.Success` if the initialization was successful or a `Result.Failure` with a `TapToPayException` in case the SDK has already been initialized or if the initialization has finished with any error. In the future, the list of exceptions will be determined and extended.

#### 2. Start Payment

```kotlin  
suspend fun startPayment(checkoutData: CheckoutData): Flow<Result<PaymentEvent>>  
```  

The `startPayment` method initiates the payment process. It returns a `Flow` of `Result` objects that can be either a `Result.Success` with the `PaymentEvent` or a `Result.Failure` with an error message.

The list of possible events:

- `TransactionDone(val output: PaymentOutput)` - in case of a completed transaction where `PaymentOutput` param is:
  
```kotlin
  data class PaymentOutput(  
    val txCode: String,  
    val scheme: Scheme?,  
    val serverTransactionId: String  
) {  
    enum class Scheme(val value: String) {  
        VISA("VISA"),  
        MASTERCARD("MASTERCARD"),  
    }  
}
```  

- `TransactionFailed` - in case of a failed transaction for several reasons, like backend error, card reader error, and so on.  
- `TransactionCanceled` - in case of a canceled transaction by the user.  
- `PaymentClosed(val shouldDisplayReceipt: Boolean)` - after a successful transaction, users see the successful screen with two buttons: Send receipt and Done. Once the user clicks on any button, the screen closes and fires the `PaymentClosed` event.  
  
The function can also return `Result.Failure` with a `TapToPayException` in case of unpredictable errors on the SDK side. In the future, the list of exceptions will be determined and extended.  
  
##### Parameters  
  
`checkoutData` - The checkout data object.  
  
```kotlin  
data class CheckoutData(  
    val totalAmount: Long,  
    val clientUniqueTransactionId: String,  
    val customItems: ArrayList<CustomItem>?,  
    val priceItems: ArrayList<PriceItem>?,  
) : Serializable
```  

Where:
- `totalAmount` - The amount expressed in the minor unit of the currency. For example, an amount of $`12.34` corresponds to a value of `1234`, $`11.00` corresponds to a value of  `1100`.
- `clientUniqueTransactionId` - Currently, this can be any random string.
- `customItems` - The list of custom items.
- `priceItems` - The list of price items.

The required minimum to make the transaction looks like:

```kotlin  
tapToPay.startPayment(  
    CheckoutData(  
        totalAmount = currentState.paymentData.amount,  
        clientUniqueTransactionId = "123",  
        customItems = null,  
        priceItems = null  
    )  
).collectLatest {  
    Log.d("MainViewModel", "Payment event: $it")  
}
```  

#### 3. Tear Down

```kotlin  
suspend fun tearDown(): Result<Unit>  
```  

The `tearDown` function logs out the user, cleans up keys and other sensitive data, and closes the session. It returns a `Result` object that can be either a `Result.Success` if the teardown was successful or a `Result.Failure` if there was an error during the teardown.

## Known Issues
1. We use [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) to store some data. There is a known issue with the `EncryptedSharedPreferences` library that causes the `InvalidProtocolBufferException` exception during the first initialization. This issue is related to the `EncryptedSharedPreferences` library and is not directly related to the SDK. The issue is tracked [here](https://issuetracker.google.com/issues/164901843).  
   There is a workaround for this issue. You can need to clean up the app data and restart the app.
2. The authentication doesn't work reliably if you use SumUp Auth SDK in the same app. We are working on a solution to this issue.
