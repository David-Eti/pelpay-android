// To parse the JSON, install Klaxon and do:
//
//   val banksResponse = BanksResponse.fromJson(jsonString)

package models.responses

import kotlinx.serialization.Serializable

//class BanksResponse(elements: Collection<Bank>) : ArrayList<Bank>(elements)

@Serializable
data class BanksResponse (
        val bankName: String? = null,
        val id: String? = null,
        val isActive: Boolean? = null,
        val createdDate: String? = null,
        val createdBy: String? = null,
        val modifiedDate: String? = null,
        val modifiedBy: String? = null


){
    override fun toString(): String {
        return bankName!!
    }
}
