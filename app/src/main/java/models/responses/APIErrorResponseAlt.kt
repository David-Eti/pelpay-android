package models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class APIErrorResponseAlt (
        val requestSuccessful: Boolean? = null,
        @SerialName("Message")
        val message: String? = null,

) {
        companion object {
                public fun fromJson(json: String) = Json.decodeFromString<APIErrorResponseAlt>(json)
        }
}