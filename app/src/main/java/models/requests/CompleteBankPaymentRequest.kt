
package models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CompleteBankPaymentRequest (
        val value: String? = null
) {

}
