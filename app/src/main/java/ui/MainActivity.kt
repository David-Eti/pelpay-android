package ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import models.requests.Customer
import models.requests.Transaction
import java.util.*

class MainActivity : AppCompatActivity() {
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

    private suspend fun initialiseSdk() {
        val uniqueID = UUID.randomUUID().toString()

        PelpaySdk.setTransaction(Transaction(
                amount = 50,
                currency = "NGN",
                merchantRef = uniqueID,
                narration = "Sample pay from Android",
                callBackURL = "https://www.localhost.com",
                splitCode = "",
                integrationKey = "a6ccab0e-157d-4fb7-b15d-ddb7cd149153",
                customer = Customer(
                        customerID = "xxx",
                        customerLastName = "olajuwon",
                        customerFirstName = "adeoye",
                        customerEmail = "olajuwon@yopmail.com",
                        customerPhoneNumber = "07039544295",
                        customerAddress = "",
                        customerCity = "Lagos",
                        customerStateCode = "LA",
                        customerPostalCode = "12345",
                        customerCountryCode = "NG"
                )
        )).initialise(clientId = "Ken0000004",
                clientSecret = "d36eb5dd-a89f-411a-b024-4cdc11673c11", context = this)
    }
}