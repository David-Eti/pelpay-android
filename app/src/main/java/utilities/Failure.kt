package utilities


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
sealed class Failure {

    /**
     * Extend this class in order to provide your own
     * custom failure.
     */
    open class CustomFailure() : Failure()

    data class UnexpectedFailure(
            val message: String?
    ) : Failure()

}