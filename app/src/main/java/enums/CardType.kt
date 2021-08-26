package enums


/**
 * Created by Ehigiator David on 20/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */

enum class CardType  constructor(private val regex: String, val issuerName: String) {

    VISA("^4[0-9]{12}(?:[0-9]{3})?$", "VISA"),
    MASTERCARD("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$", "MASTER"),
    AMEX("^3[47][0-9]{13}$", "AMEX"),
    DINERS("^3(?:0[0-5]|[68][0-9])[0-9]{11}$", "DINERS"),
    DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$", "DISCOVER"),
    VERVE("^((506(0|1))|(507(8|9))|(6500))[0-9]{12,15}$","VERVE"),
    JCB("^(?:2131|1800|35\\d{3})\\d{11}$", "JCB");

    fun matches(card: String): Boolean {
        return card.matches(this.regex.toRegex())
    }

    companion object {
        /**
         * get an enum from a card number
         * @param card
         * @return
         */
        fun getCardByName (card: String): CardType? {
            for (cc in values()) {
                if (cc.matches(card)) {
                    return cc
                }
            }
            return null
        }

        /**
         * get an enum from an issuerName
         * @param issuerName
         * @return
         */
        fun getCardByIssuerName(issuerName: String): CardType? {
            for (cc in values()) {
                if (cc.issuerName == issuerName) {
                    return cc
                }
            }
            return null
        }
    }
}