package ui.verify

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.PelpaySdk
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.responses.GetTransactionDetailsResponse
import models.responses.SupportedBank
import models.responses.VerifiedTransactionDetails
import networking.NetworkServiceImpl
import utilities.ResultWrapper

class VerifyTransactionViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var verifiedTransactionDetails = MutableLiveData<VerifiedTransactionDetails>()
    var isSuccessFul  = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String?>()

    @ExperimentalSerializationApi
    fun getTransactionDetails() {
        isLoading.value = true
        viewModelScope.launch {
            when (val verifiedDetailsResponse = NetworkServiceImpl().getNetworkService().getTransactionByAdvice(adviceReference = PelpaySdk.advice?.adviceReference!!, token = PelpaySdk.accessToken!!)){
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(verifiedDetailsResponse)
                is ResultWrapper.Success -> showSuccess(verifiedDetailsResponse.value)
                else -> {
                    isLoading.value = false
                    isSuccessFul.value = false
                    errorMessage.value = "A unknown Error occurred. $verifiedDetailsResponse"
                    Log.e("VerifyTransactionVM", "showGenericError:  $verifiedDetailsResponse")
                }
            }
        }
    }

    private fun showSuccess(transactionDetailsResponse: GetTransactionDetailsResponse) {
        isLoading.value = false
        this.verifiedTransactionDetails.value = transactionDetailsResponse.responseData
        if (this.verifiedTransactionDetails.value?.transactionStatus?.lowercase()?.contains("success") == true){
            isSuccessFul.value = true
        }
        else {
            isSuccessFul.value = false
            errorMessage.value = "${this.verifiedTransactionDetails.value?.transactionStatus}: ${this.verifiedTransactionDetails.value?.message}"
        }
    }

    private fun showGenericError(verifiedDetailsResponse: ResultWrapper.GenericError) {
        isLoading.value = false
        isSuccessFul.value = false
        errorMessage.value = "${verifiedDetailsResponse.error?.message}"
    }

    private fun showNetworkError() {
        isLoading.value = false
        isSuccessFul.value = false
        errorMessage.value = "A network Error occurred. Try again later."
    }

}