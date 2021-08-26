package models.responses


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
import kotlinx.serialization.*

@Serializable
data class ProcessCardResponse(
        val requestSuccessful: Boolean? = null,
        val responseData: ProcessCardData? = null,
        val message: String? = null,
        val responseCode: String? = null
)

@Serializable
data class ProcessCardData(
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
        val formData: ProcessCardFormData? = null,
        val sessionMerchantID: String? = null,

        @SerialName("orgId")
        val orgID: String? = null,

        @SerialName("returnUrl")
        val returnURL: String? = null
)

@Serializable
data class ProcessCardFormData(
        val url: String? = null,
        val formData: ProcessCardFormDataForm? = null,
        val formString: String? = null
)

@Serializable
data class ProcessCardFormDataForm(
        @SerialName("JWT")
        val jwt: String? = null
)