package application


/**
 * Created by Ehigiator David on 02/09/2021.
 * Pelpay
 * david3ti@gmail.com
 */
 abstract class PelpaySdkCallback {

    abstract  fun onSuccess(adviceReference : String?)

    abstract  fun onError(errorMessage: String?)

}