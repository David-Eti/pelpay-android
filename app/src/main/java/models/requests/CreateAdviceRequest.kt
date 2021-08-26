package models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAdviceRequest (
        val amount: Long? = null,
        val currency: String? = null,
        val merchantRef: String? = null,
        val narration: String? = null,
        @SerialName("callBackUrl")
        val callBackURL: String? = null,
        val splitCode: String? = null,
        val customer: Customer? = null,
        val integrationKey: String? = null
)
@Serializable
data class Customer (
        @SerialName("customerId")
        val customerID: String? = null,
        val customerLastName: String? = null,
        val customerFirstName: String? = null,
        val customerEmail: String? = null,
        val customerPhoneNumber: String? = null,
        val customerAddress: String? = null,
        val customerCity: String? = null,
        val customerStateCode: String? = null,
        val customerPostalCode: String? = null,
        val customerCountryCode: String? = null
)
public typealias Transaction = CreateAdviceRequest
