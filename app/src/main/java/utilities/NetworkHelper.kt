package utilities

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.responses.APIErrorResponse
import models.responses.APIErrorResponseAlt
import okio.IOException
import retrofit2.HttpException


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
class NetworkHelper {

    suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Log.e("safeApiCall", "safeApiCall: ${throwable.localizedMessage}")
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        if (errorResponse != null) {
                            ResultWrapper.GenericError(code, errorResponse)
                        } else {
                            val errorAltResponse = convertAltErrorBody(throwable)
                            ResultWrapper.AlternateGenericError(code, errorAltResponse)
                        }
                    }

                    else -> {
                        ResultWrapper.OtherError(null, throwable.localizedMessage)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): APIErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                APIErrorResponse.fromJson(it.readUtf8())
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun convertAltErrorBody(throwable: HttpException): APIErrorResponseAlt? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                APIErrorResponseAlt.fromJson(it.readUtf8())
            }
        } catch (exception: Exception) {
            null
        }
    }

}