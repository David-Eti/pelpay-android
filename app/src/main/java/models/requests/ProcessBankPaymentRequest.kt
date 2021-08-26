
package models.requests

import kotlinx.serialization.Serializable

@Serializable
data class ProcessBankPaymentRequest (
        var bankCode: String? = null,
        var accountNumber: String? = null,
        var nameOnAccount: String? = null
)