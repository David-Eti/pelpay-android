package utilities

import models.responses.APIErrorResponse
import models.responses.APIErrorResponseAlt


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class OtherError(val code: Int? = null, val error: String? = null) : ResultWrapper<Nothing>()
    data class GenericError(val code: Int? = null, val error: APIErrorResponse? = null) : ResultWrapper<Nothing>()
    data class AlternateGenericError(val code: Int? = null, val error: APIErrorResponseAlt? = null) : ResultWrapper<Nothing>()
    object NetworkError : ResultWrapper<Nothing>()
}