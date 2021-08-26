// To parse the JSON, install Klaxon and do:
//
//   val completeCardRequest = CompleteCardRequest.fromJson(jsonString)

package models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CompleteCardRequest (
        val paymentreference: String? = null,
        val value: String? = null
)
