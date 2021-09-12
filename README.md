# Pelpay Android SDK
Welcome to Pelpay's Android SDK. This library will help you accept card and alternative payments in your Android app.
**The Pelpay Android SDK permits a deployment target of Android version 21 or higher**.
## Supported Payment Methods
- Credit Card
- Bank Payment
- Bank Transfer
## Installation
### Install and Configure the SDK
1. Add it in your root build.gradle at the end of repositories:

```
	allprojects {
	   repositories {
		   ...
		   maven { url 'https://jitpack.io' }
	   }
	}
```
2. Add the dependency
```

	dependencies {
	   implementation 'xxx'
	}
  
```
### Configure your Pelpay integration
**Step 1**: Configure Client ID, Client Secret & Integration Key
After installation of the Pelpay SDK, configure it with your Client ID, Client Secret & Integration Key gotten from your merchant dashboard, for both test and production

#### Simple Integration generation (Kotlin)
```kotlin
  PelpaySdk.setTransaction(Transaction(
                amount = 50,
                currency = "NGN",
                merchantRef = "UNIQUE_GENERATED_VALUE",
                narration = "Sample pay from Android",
                splitCode = "",
                integrationKey = "INTEGRATION_KEY_FROM_MERCHANT_DASHBOARD",
                customer = Customer(
                        customerID = "xxx",
                        customerLastName = "olajuwon",
                        customerFirstName = "adeoye",
                        customerEmail = "olajuwon@yopmail.com",
                        customerPhoneNumber = "07039544295",
                        customerAddress = "16 Egbeda Road",
                        customerCity = "Lagos",
                        customerStateCode = "LA",
                        customerPostalCode = "12345",
                        customerCountryCode = "NG"
                )
        )).setBrandPrimaryColor(Color.parseColor("#009F49")).setHidePelpayLogo(false).initialise(environment = Environment.Staging, clientId = "CLIENT_ID_FROM_MERCHANT_DASHBOARD",
                clientSecret = "CLIENT_SECRET_FROM_MERCHANT_DASHBOARD", context = this).withCallBack(callback = object : PelpaySdkCallback() {
            override fun onSuccess(adviceReference: String?) {

                Log.e("PELPAYSDK", "onSuccess: $adviceReference")
            }

            override fun onError(errorMessage: String?) {
                Log.e("PELPAYSDK", "onError: $errorMessage")
            }
        })
```
