
package models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
        @SerialName("access_token")
        val accessToken: String? = null,

        @SerialName("expires_in")
        val expiresIn: Long? = null
)