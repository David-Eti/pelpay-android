package models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest (
        @SerialName("clientId")
        val clientID: String? = null,
        val clientSecret: String? = null
)
