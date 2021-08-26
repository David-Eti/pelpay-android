package models.responses


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json                       = Json(JsonConfiguration.Stable)
// val bankTransferDetailResponse = json.parse(BankTransferDetailResponse.serializer(), jsonString)


import kotlinx.serialization.*

@Serializable
data class BankTransferDetailResponse (
        val requestSuccessful: Boolean? = null,
        val responseData: BankTransferDetail? = null,
        val message: String? = null,
        val responseCode: String? = null
)

@Serializable
data class BankTransferDetail (
        val bankName: String? = null,
        val bankCode: String? = null,
        val bankAccount: String? = null,
        val paymentReference: String? = null,
        val status: String? = null,
        val message: String? = null,

        @SerialName("callBackUrl")
        val callBackURL: String? = null
)
