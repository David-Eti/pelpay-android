
package models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BankTransferDetailRequest (
        @SerialName("bankCode")
        val bankCode: String? = null
)
