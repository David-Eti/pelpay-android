package models.responses


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
import kotlinx.serialization.*

@Serializable
data class ProcessBankPaymentResponse(
        val requestSuccessful: Boolean? = null,
        val responseData: ProcessBankData? = null,
        val message: String? = null,
        val responseCode: String? = null
)

@Serializable
data class ProcessBankData(
        val bankCode: String? = null,
        val bankAccount: String? = null,
        val paymentReference: String? = null,
        val status: String? = null,
        val message: String? = null,

        @SerialName("callBackUrl")
        val callBackURL: String? = null
)
