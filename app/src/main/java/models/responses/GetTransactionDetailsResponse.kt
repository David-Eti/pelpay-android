package models.responses


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json                          = Json(JsonConfiguration.Stable)
// val getTransactionDetailsResponse = json.parse(GetTransactionDetailsResponse.serializer(), jsonString)


import kotlinx.serialization.*

@Serializable
data class GetTransactionDetailsResponse (
        val requestSuccessful: Boolean? = null,
        val responseData: VerifiedTransactionDetails? = null,
        val message: String? = null,
        val responseCode: String? = null
)

@Serializable
data class VerifiedTransactionDetails (
        val merchantCode: String? = null,
        val paymentReference: String? = null,
        val merchantReference: String? = null,
        val amount: Double? = null,

        @SerialName("callBackUrl")
        val callBackURL: String? = null,

        val processorCode: String? = null,
        val transactionStatus: String? = null,
        val currencyCode: String? = null,
        val narration: String? = null,
        val env: String? = null,
        val message: String? = null,

        @SerialName("returnUrl")
        val returnURL: String? = null
)
