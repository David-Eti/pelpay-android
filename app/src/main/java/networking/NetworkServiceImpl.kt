package networking

import android.annotation.SuppressLint
import application.PelpaySdk
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import constants.URLs
import enums.Environment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import models.requests.*
import models.responses.*
import networking.services.NetworkService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import utilities.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * Created by Ehigiator David on 12/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
class NetworkServiceImpl(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private lateinit var networkService: NetworkService

    @ExperimentalSerializationApi
    fun getNetworkService(): NetworkServiceImpl {
        val contentType = "application/json".toMediaType()
        // Initialize ApiService if not initialized yet
        if (!::networkService.isInitialized) {
            val retrofit = Retrofit.Builder()
                    .baseUrl(
                            when (PelpaySdk.environment) {
                                Environment.Staging -> URLs.STAGING
                                Environment.Production -> URLs.PRODUCTION
                            })
                    .client(unSafeOkHttpClient().build())
                    .addConverterFactory(Json { ignoreUnknownKeys = true; prettyPrint = true; isLenient = true}.asConverterFactory(contentType))
                    .build()

            networkService = retrofit.create(NetworkService::class.java)
        }

        return this
    }

    private fun unSafeOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS)


        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(sslSocketFactory, trustAllCerts.first() as X509TrustManager)
                okHttpClient.hostnameVerifier(HostnameVerifier { _, _ -> true })

            }

            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }

    suspend fun login(loginRequest: LoginRequest): ResultWrapper<LoginResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.login(loginRequest) }
    }

    suspend fun createAdvice(createAdviceRequest: CreateAdviceRequest, token: String): ResultWrapper<CreateAdviceResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.createAdvice(createAdviceRequest = createAdviceRequest, token = "Bearer $token") }
    }

    suspend fun getSupportedBanksForTransfer(token: String): ResultWrapper<SupportedBankResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.getSupportedBanksForTransfer(token = "Bearer $token") }
    }

    suspend fun getBanks(token: String): ResultWrapper<ArrayList<BanksResponse>> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.getBanks(token = "Bearer $token") }
    }

    suspend fun processBankTransfer(adviceReference: String, bankTransferDetailRequest: BankTransferDetailRequest, token: String): ResultWrapper<BankTransferDetailResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.processBankTransfer(adviceReference = adviceReference, bankTransferDetailRequest = bankTransferDetailRequest, token = "Bearer $token") }
    }

    suspend fun getTransactionByAdvice(adviceReference: String, token: String): ResultWrapper<GetTransactionDetailsResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.getTransactionByAdvice(adviceReference, token = "Bearer $token") }
    }

    suspend fun processCardPayment(adviceReference: String, cardRequest: CardRequest, token: String): ResultWrapper<ProcessCardResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.processCardPayment(adviceReference, cardRequest, token = "Bearer $token") }
    }

    suspend fun validateCardPayment( completeCardRequest: CompleteCardRequest, token: String): ResultWrapper<CompleteCardResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.validateCardPayment(completeCardRequest, token = "Bearer $token") }
    }

    suspend fun processBankPayment(adviceReference: String, processBankPaymentRequest: ProcessBankPaymentRequest, token: String): ResultWrapper<ProcessBankPaymentResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.processBankPayment(adviceReference, processBankPaymentRequest, token = "Bearer $token") }
    }

    suspend fun completeBankPayment(paymentReference: String, completeBankPaymentRequest: CompleteBankPaymentRequest, token: String): ResultWrapper<CompleteBankPaymentResponse> {
        return NetworkHelper().safeApiCall(dispatcher) { networkService.completeBankPayment(paymentReference, completeBankPaymentRequest, token = "Bearer $token") }
    }

}