// To parse the JSON, install Klaxon and do:
//
//   val completeBankPaymentResponse = CompleteBankPaymentResponse.fromJson(jsonString)

package models.responses

import kotlinx.serialization.Serializable

@Serializable
data class CompleteBankPaymentResponse (
        val requestSuccessful: Boolean,
        val responseData: CompleteBankResponseData,
        val message: String,
        val responseCode: String
)
@Serializable
data class CompleteBankResponseData (
        val merchantCode: String,
        val paymentReference: String,
        val merchantReference: String,
        val amount: Double,
        val transactionStatus: String,
        val currencyCode: String,
        val bankCode: String,
        val narration: String,
        val env: String,
        val accountNumber: String,
        val bankName: String
)
