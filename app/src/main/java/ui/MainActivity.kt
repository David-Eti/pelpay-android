package ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import application.PelpaySdk
import application.PelpaySdkCallback
import com.example.pelpaysamplebuildandroid.R
import enums.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.Customer
import models.requests.Transaction
import java.util.*

class MainActivity : AppCompatActivity() {
    @ExperimentalSerializationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val scope = CoroutineScope(Job() + Dispatchers.Main)

        val launchButton = findViewById<AppCompatButton>(R.id.launchButton)
        launchButton.setOnClickListener {
            scope.launch {
                initialiseSdk()
            }
        }
    }

    @ExperimentalSerializationApi
    private suspend fun initialiseSdk() {

        PelpaySdk.setTransaction(Transaction(
                amount = 50,
                currency = "NGN",
                merchantRef = UUID.randomUUID().toString(),
                narration = "Sample pay from Android",
                splitCode = "",
                integrationKey = "a6ccab0e-157d-4fb7-b15d-ddb7cd149153",
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
        )).setMerchantLogo(ContextCompat.getDrawable(this, R.drawable.ic_pelpay_logo_full)!!).setBrandPrimaryColor(Color.parseColor("#009F49")).setHidePelpayLogo(false).initialise(environment = Environment.Staging, clientId = "Ken0000004",
                clientSecret = "d36eb5dd-a89f-411a-b024-4cdc11673c11", context = this).withCallBack(callback = object : PelpaySdkCallback() {
            override fun onSuccess(adviceReference: String?) {

                Log.e("PELPAYSDK", "onSuccess: $adviceReference")
            }

            override fun onError(errorMessage: String?) {
                Log.e("PELPAYSDK", "onError: $errorMessage")
            }
        })
    }
}