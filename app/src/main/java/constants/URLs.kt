package constants


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
object URLs {

    const val STAGING = "https://api.pelpay.ng"
    const val PRODUCTION = "https://api.pelpay.africa"


    //ACCOUNT ENDPOINTS
    const val ACCOUNT_POST = "api/Account/Login"

    //PAYMENT ENDPOINTS
    const val PAYMENT_GET_ADVICE = "Payment/advice"
    const val PAYMENT_POST_ADVICE = "Payment/advice"
    const val PAYMENT_POST_PROCESS_CARD = "Payment/process/card"
    const val PAYMENT_POST_COMPLETE_CARD = "Payment/complete/card"
    const val PAYMENT_GET_SUPPORTED_BANKS_TRANSFER = "Payment/banktransfer/supportedbank"
    const val PAYMENT_PROCESS_BANK_TRANSFER = "Payment/process/banktransfer"
    const val PAYMENT_PROCESS_BANK_PAYMENT = "Payment/process/bankaccount"
    const val PAYMENT_COMPLETE_BANK_PAYMENT = "Payment/complete/bankaccount"

    //BANK ENDPOINTS
    const val GET_BANKS = "api/Banks"

    //TRANSACTION ENDPOINTS
    const val TRANSACTION_GET_BY_ADVICE = "api/Transaction/byadvicereference"

}