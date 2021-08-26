package ui.library.cardvalidation

import enums.CardType


/**
 * Created by Ehigiator David on 20/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
internal class CardResult {

    var isValid = false
    var cardType: CardType? = null
    var cardNo: String? = null

    constructor(cardNo: String, status : Boolean) {
        this.cardNo = cardNo
        this.isValid = status
    }

    constructor(cardNo: String, cardType: CardType) {
        this.cardNo = cardNo
        this.isValid = true
        this.cardType = cardType
    }
}