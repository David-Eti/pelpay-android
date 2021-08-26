package utilities


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
abstract class PelpayResultCallback<T> {
    abstract fun onSuccess(result : T)
    abstract fun onError(error : Failure)
}