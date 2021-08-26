package models.responses


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json                 = Json(JsonConfiguration.Stable)
// val completeCardResponse = json.parse(CompleteCardResponse.serializer(), jsonString)


import kotlinx.serialization.*

@Serializable
data class CompleteCardResponse (
        val requestSuccessful: Boolean? = null,
        val responseData: CompleteResponseData? = null,
        val message: String? = null,
        val responseCode: String? = null
)

@Serializable
data class CompleteResponseData (
        val merchantCode: String? = null,
        val paymentReference: String? = null,
        val merchantReference: String? = null,
        val amount: Double? = null,

        @SerialName("callBackUrl")
        val callBackURL: String? = null,

        val transactionStatus: String? = null,
        val currencyCode: String? = null,
        val accountNumber: String? = null,
        val accountNumberMasked: String? = null,
        val narration: String? = null,
        val env: String? = null,
        val message: String? = null,
        val formData: CompleteResponseDataFormData? = null,
        val sessionMerchantID: String? = null,

        @SerialName("orgId")
        val orgID: String? = null,

        @SerialName("returnUrl")
        val returnURL: String? = null
)

@Serializable
data class CompleteResponseDataFormData (
        val url: String? = null,
        val formData: CompleteResponseJWTFormData? = null,
        val formString: String? = null
)

@Serializable
data class CompleteResponseJWTFormData (
        @SerialName("JWT")
        val jwt: String? = null,

        @SerialName("PaymentReference")
        val paymentReference: String? = null
)
