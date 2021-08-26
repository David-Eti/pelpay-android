package utilities


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
sealed class Either<out L, out R> {

    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Error<out L>(val error: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Success<out R>(val success: R) : Either<Nothing, R>()

    val isSuccess get() = this is Success<R>
    val isError get() = this is Error<L>

    fun either(fnL: (L) -> Unit, fnR: (R) -> Unit): Any =
            when (this) {
                is Error -> fnL(error)
                is Success -> fnR(success)
            }

    /**
     * This method will send the Failure if is [Error].
     * But, if is [Success] then it will invoke the [transform]
     * suspend lambda and return the desired transformed object [T].
     *
     * @return [Either.Success<T>] or the expected [Either.Error]
     * @see [https://github.com/arrow-kt/arrow-core/blob/master/arrow-core-data/src/main/kotlin/arrow/core/Either.kt]
     */
    suspend inline fun <T> coMapSuccess(
            crossinline transform: suspend (R) -> T
    ): Either<L, T> {
        return when (this) {
            is Success -> transform(this.success).toSuccess()
            is Error -> this
        }
    }

    /**
     * This method will send the Failure if is [Error].
     * But, if is [Success] then it will invoke the [transform]
     * block and return the transformation result.
     *
     * @return [Either.Success<T>] or the expected [Either.Error]
     * @see [https://github.com/arrow-kt/arrow-core/blob/master/arrow-core-data/src/main/kotlin/arrow/core/Either.kt]
     */
    inline fun <T> mapSuccess(
            crossinline transform: (R) -> T
    ): Either<L, T> {
        return when (this) {
            is Success -> transform(this.success).toSuccess()
            is Error -> this
        }
    }

    fun getSuccessOrNull(): R? = if (this is Success<R>) {
        this.success
    } else {
        null
    }

    fun getFailureOrNull(): L? = if (this is Error<L>) {
        this.error
    } else {
        null
    }
}