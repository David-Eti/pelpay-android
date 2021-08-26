package models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */

@Serializable
data class CreateAdviceResponse (
        val requestSuccessful: Boolean? = null,
        val responseData: AdviceData? = null,
        val message: String? = null,
        val responseCode: String? = null
)
@Serializable
data class AdviceData (
        val currency: String? = null,
        val adviceReference: String? = null,
        val merchantRef: String? = null,
        val amount: Double? = null,
        val narration: String? = null,

        @SerialName("customerId")
        val customerID: String? = null,

        val charge: Double? = null,
        val status: String? = null,
        val customerFullName: String? = null,
        val merchantName: String? = null,

        @SerialName("paymentUrl")
        val paymentURL: String? = null,

        val channel: List<String>? = null
)
