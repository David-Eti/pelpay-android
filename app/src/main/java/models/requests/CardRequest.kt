// To parse the JSON, install Klaxon and do:
//
//   val cardRequest = CardRequest.fromJson(jsonString)

package models.requests

import enums.CardType
import kotlinx.serialization.Serializable
import ui.library.cardvalidation.CardValidator
import java.security.InvalidParameterException

@Serializable
data class CardRequest(
        var cardType: CardType? = null
) {

    var cardNumber: String? = null
        set(value) {

            if (value.isNullOrEmpty()) throw InvalidParameterException("Invalid Card Number Found")
            val result = CardValidator.isValid(value)
            if (!result.isValid) throw InvalidParameterException("Invalid Card Number Found")

//            var last4Digits = ""

            this.cardType = result.cardType
            field = result.cardNo
//        last4Digits = field?.substring(field?.length!! - 4, (field?.length!!))!!
        }


    var cvv: String? = null
        set(value) {
            if (value != null) {
                if (value.length > 3 || value.length < 3) throw InvalidParameterException("Invalid Card Cvv Found")
            } else throw InvalidParameterException("Invalid Card Cvv Found")

            field = value
        }

    var expiredMonth: String? = null
        set(value) {
            if (value != null) {
                if (value.toInt() > 12 || value.toInt() < 1) throw InvalidParameterException("Invalid Card Expiry Month")
            }else throw InvalidParameterException("Invalid Card Expiry Month")
            field = value
        }


    var expiredYear: String? = null
        set(value) {
            if (value != null) {
                if (value.length > 4 || value.length < 4) throw InvalidParameterException("Invalid Card Expiry Year")
            } else throw InvalidParameterException("Invalid Card Expiry Year")
            field = value
        }

    var cardPin: String? = null
        set(value) {
            if (value != null) {
                if (value.length > 4 || value.length < 4) throw InvalidParameterException("Invalid Card Pin")
            } else throw InvalidParameterException("Invalid Card Pin")
            field = value
        }

}
