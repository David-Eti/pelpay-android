package utilities


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
fun <R> R.toSuccess(): Either.Success<R> {
    return Either.Success(this)
}

fun <L> L.toError(): Either.Error<L> {
    return Either.Error(this)
}