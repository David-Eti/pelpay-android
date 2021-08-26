package models.responses

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class APIErrorResponse(
        val requestSuccessful: Boolean? = null,
        val message: String? = null,
        val responseCode: String? = null
) {
    companion object {
        public fun fromJson(json: String) = Json.decodeFromString<APIErrorResponse>(json)
    }
}


