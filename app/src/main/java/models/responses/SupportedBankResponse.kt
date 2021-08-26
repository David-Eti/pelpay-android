// To parse the JSON, install Klaxon and do:
//
//   val supportedBankResponse = SupportedBankResponse.fromJson(jsonString)

package models.responses

import kotlinx.serialization.Serializable

@Serializable
data class SupportedBankResponse (
        val requestSuccessful: Boolean? = null,
        val responseData: ArrayList<SupportedBank>? = null,
        val message: String? = null,
        val responseCode: String? = null
)

@Serializable
data class SupportedBank (
        val code: String? = null,
        val bankName: String? = null
){
    override fun toString(): String {
        return bankName!!
    }
}
