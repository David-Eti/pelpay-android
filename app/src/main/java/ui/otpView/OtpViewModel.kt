package ui.otpView

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.PelpaySdk
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.CompleteBankPaymentRequest
import models.requests.CompleteCardRequest
import models.responses.CompleteBankPaymentResponse
import models.responses.CompleteCardResponse
import models.responses.ProcessCardResponse
import networking.NetworkServiceImpl
import utilities.ResultWrapper
import java.net.URLEncoder

class OtpViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()

    var completeCardResponse = MutableLiveData<CompleteCardResponse>()
    var completeBankPaymentResponse = MutableLiveData<CompleteBankPaymentResponse>()


    @ExperimentalSerializationApi
    fun completeCardTransaction(otp: String, paymentReference: String) {
        isLoading.value = true
        viewModelScope.launch {
            when (val cardCompleteResponse = NetworkServiceImpl().getNetworkService().validateCardPayment(CompleteCardRequest(
                    paymentreference = paymentReference, value = otp
            ), token = PelpaySdk.accessToken!!)) {
                is ResultWrapper.NetworkError -> {
                    isLoading.value = false
                    Log.e("OtpViewModel", "NetworkError:  $cardCompleteResponse")
                }
                is ResultWrapper.GenericError -> {
                    isLoading.value = false
                    Log.e("OtpViewModel", "GenericError:  $cardCompleteResponse")
                }
                is ResultWrapper.Success -> showCardSuccess(cardCompleteResponse.value)
            }
        }

    }

    private fun showCardSuccess(completeCardResponse: CompleteCardResponse) {
        isLoading.value = false
        this.completeCardResponse.value = completeCardResponse
    }

    @ExperimentalSerializationApi
    fun completeBankTransaction(otp: String, paymentReference: String) {
        isLoading.value = true
        viewModelScope.launch {
            when (val bankCompleteResponse = NetworkServiceImpl().getNetworkService().completeBankPayment(paymentReference = paymentReference, CompleteBankPaymentRequest(
                    value = otp
            ), token = PelpaySdk.accessToken!!)) {
                is ResultWrapper.NetworkError -> {
                    isLoading.value = false
                    Log.e("OtpViewModel", "NetworkError:  $bankCompleteResponse")
                }
                is ResultWrapper.GenericError -> {
                    isLoading.value = false
                    Log.e("OtpViewModel", "GenericError:  $bankCompleteResponse")
                }
                is ResultWrapper.Success -> showBankSuccess(bankCompleteResponse.value)
            }
        }
    }

    private fun showBankSuccess(bankPaymentResponse: CompleteBankPaymentResponse) {
        this.completeBankPaymentResponse.value = bankPaymentResponse
    }
}