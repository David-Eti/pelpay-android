package networking.services

import constants.URLs
import models.requests.*
import models.responses.*
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by Ehigiator David on 12/08/2021.
 */
interface NetworkService {

    @POST(URLs.ACCOUNT_POST)
   suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST(URLs.PAYMENT_POST_ADVICE)
    suspend  fun createAdvice(@Body createAdviceRequest: CreateAdviceRequest, @Header("Authorization") token: String): CreateAdviceResponse

    @GET(URLs.PAYMENT_GET_SUPPORTED_BANKS_TRANSFER)
    suspend fun getSupportedBanksForTransfer(@Header("Authorization") token: String): SupportedBankResponse

    @GET(URLs.GET_BANKS)
    suspend fun getBanks(@Header("Authorization") token: String): ArrayList<BanksResponse>

    @POST("${URLs.PAYMENT_PROCESS_BANK_TRANSFER}/{adviceReference}")
    suspend fun processBankTransfer(@Path("adviceReference") adviceReference: String, @Body bankTransferDetailRequest: BankTransferDetailRequest, @Header("Authorization") token: String): BankTransferDetailResponse

    @GET("${URLs.TRANSACTION_GET_BY_ADVICE}/{adviceReference}")
    suspend fun getTransactionByAdvice(@Path("adviceReference") adviceReference: String, @Header("Authorization") token: String): GetTransactionDetailsResponse

    @POST("${URLs.PAYMENT_POST_PROCESS_CARD}/{adviceReference}")
    suspend fun processCardPayment(@Path("adviceReference") adviceReference: String, @Body cardRequest: CardRequest, @Header("Authorization") token: String): ProcessCardResponse

    @POST(URLs.PAYMENT_POST_COMPLETE_CARD)
    suspend fun validateCardPayment( @Body completeCardRequest: CompleteCardRequest, @Header("Authorization") token: String): CompleteCardResponse

    @POST("${URLs.PAYMENT_PROCESS_BANK_PAYMENT}/{adviceReference}")
    suspend fun processBankPayment(@Path("adviceReference") adviceReference: String, @Body processBankPaymentRequest: ProcessBankPaymentRequest, @Header("Authorization") token: String): ProcessBankPaymentResponse

    @POST("${URLs.PAYMENT_COMPLETE_BANK_PAYMENT}/{paymentReference}")
    suspend fun completeBankPayment(@Path("paymentReference") paymentReference: String, @Body completeBankPaymentRequest: CompleteBankPaymentRequest, @Header("Authorization") token: String): CompleteBankPaymentResponse

}