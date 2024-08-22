
# TapToPay SDK

This repository provides step-by-step documentation for SumUp's TapToPay SDK, which enables users to use any Android phone as a contactless reader. This is an early version (0.2.0-DEV) of the SDK, which contains a set of limitations that will be addressed in future versions. The SDK currently works only in DEV mode and is intended for testing purposes.

## Limitations

1. **Environment Restriction**: The SDK works only in the SumUp `Staging` environment. For security reasons, this set is configured for the Staging environment only. The real transactions are not processed in the Staging environment.

2. **Market Restriction**: The SDK uses hardcoded payment configurations for the German (DE) market. Due to this, it is only possible to use German test user. 

3. **Feature Limitations**: The TapToPay API doesn't currently support all the features that exist in the MerchantApp, such as tips and installments.

## Integration of the TapToPay SDK

You can use the sample app provided in this repository as a reference.

### Setup

For external integrators who want to test the SDK, SumUp provides:
 - API key for the Staging environment, which is used for the authentication process
 - Test credentials, which are used to get access to the SDK repository

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
            url = uri("https://tap-to-pay-sdk.fleet.live.sumup.net/")  
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
implementation("com.sumup.tap-to-pay:utopia-sdk:0.2.0-DEV")
```  

### Authentication

UTOPIA SDK uses the transparent authentication approach. It means that the SDK is not responsible for the authentication process. The authentication process is handled by consuming app. The SDK provides the `init` [method](#1-initialization) with the `AuthTokenProvider` interface as a parameter. The `AuthTokenProvider` interface is responsible for providing the access token to the SDK.

```kotlin
interface AuthTokenProvider {
  fun getAccessToken(): String
}
```

There are several ways for a consumer app to provide the access token to the SDK.

1. Using the OAuth2 [flow](https://developer.sumup.com/online-payments/introduction/authorization#o-auth-2-0). The consumer app can implement the OAuth2 flow to get the access token and provide it to the SDK. The SDK provides the `AuthTokenProvider` interface that should be implemented by the consumer app. The implementation of the `getAccessToken` method should return the access token. This way is preferable and recommended because it provides a more secure way to authenticate the user.

2. Using API key. This is possible to generate the API keys in the SumUp Dashboard for [Live environment](https://developer.sumup.com/online-payments/introduction/authorization#api-keys) and provide them to the SDK.
> [!CAUTION]
> Currently, the SDK works only in the Staging environment. SumUp provides the API key for the Staging environment for testing purposes.

> [!IMPORTANT]
>  The API keys should be stored securely and should not be hardcoded in the app. The API keys should be stored in the secure storage and should be provided to the SDK when needed. Do not share your secret API keys in publicly accessible places such as GitHub repositories, client-side code, etc.

### API

The `TapToPay` interface provides methods to interact with the Tap to Pay feature. To receive the implementation of the `TapToPay` interface, call:

```kotlin
val tapToPay = TapToPayApiProvider.provide()
```

The `TapToPay` interface has the following methods:

#### 1. Initialization

```kotlin
suspend fun init(authTokenProvider: AuthTokenProvider): Result<Unit>
```

The `init` method initializes the session, and register the Terminal if it is needed. The `AuthTokenProvider` interface is responsible for providing the access token to the SDK (see [here](#Authentication)).

The `init` function returns a `Result` object that can be either a `Result.Success` if the initialization was successful.
The function can also return `Result.Failure` with one exception from the list of exceptions mentioned [here](#Exceptions).

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
      val serverTransactionId: String
  )
  ```

- `TransactionFailed` - in case of a failed transaction for several reasons, like backend error, card reader error, and so on.
- `TransactionCanceled` - in case of a canceled transaction by the user.
- `PaymentClosed(val shouldDisplayReceipt: Boolean)` - after a successful transaction, users see the successful screen with two buttons: Send receipt and Done. Once the user clicks on any button, the screen closes and fires the `PaymentClosed` event.

The function can also return `Result.Failure` with one exception from the list of exceptions mentioned [here](#Exceptions).

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
fun startPayment() {
    tapToPay.startPayment(
        CheckoutData(
            totalAmount = 1234, // 12.34 EUR
            clientUniqueTransactionId = "123",
            customItems = null,
            priceItems = null
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

The `tearDown` function logs out the user, cleans up keys and other sensitive data, and closes the session. It returns a `Result` object that can be either a `Result.Success` if the teardown was successful or a `Result.Failure` if there was an error during the teardown.

#### Exceptions

The SDK can throw the following exceptions:
- `TapToPayException` - in case of an error on the SDK side. This a general exception that can be thrown in case of any error on the SDK side.
- `AppUpdateException` - in case of an outdated version of the app. The consumer app should ask the user to update the app.
- `UnauthorisedException` - in case of an unauthorized user. In this case the consumer app should refresh the token or log out the user and ask for the login again.
  The list of exceptions can be extended in the future.

## Known Issues
1. We use [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) to store some data. There is a known issue with the `EncryptedSharedPreferences` library that causes the `InvalidProtocolBufferException` exception during the first initialization. This issue is related to the `EncryptedSharedPreferences` library and is not directly related to the SDK. The issue is tracked [here](https://issuetracker.google.com/issues/164901843).
   There is a workaround for this issue. You need to clean up the app data and restart the app.
