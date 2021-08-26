package ui.cardChannel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.PelpaySdk
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.CardRequest
import models.responses.ProcessCardResponse
import networking.NetworkServiceImpl
import utilities.ResultWrapper

class CardChannelViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var processCardResponse = MutableLiveData<ProcessCardResponse>()

    @ExperimentalSerializationApi
    fun processCardRequest(cardRequest: CardRequest) {
        isLoading.value = true
        viewModelScope.launch {
            when (val cardResponse = NetworkServiceImpl().getNetworkService().processCardPayment(adviceReference = PelpaySdk.advice!!.adviceReference!!, cardRequest, token = PelpaySdk.accessToken!!)) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(cardResponse)
                is ResultWrapper.Success -> showSuccess(cardResponse.value)
                else -> {
                    isLoading.value = false
                    Log.e("CardChannelViewModel", "showGenericError:  $cardResponse")
                }
            }
        }
    }

    private fun showSuccess(cardResponse: ProcessCardResponse) {
        isLoading.value = false
        processCardResponse.value = cardResponse

    }

    private fun showGenericError(cardResponse: ResultWrapper<ProcessCardResponse>) {
        isLoading.value = false
        Log.e("CardChannelViewModel", "showGenericError:  $cardResponse")
    }

    private fun showNetworkError() {
        isLoading.value = false
        TODO("Not yet implemented")
    }
}