package ui.bankTransferChannel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.PelpaySdk
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.responses.SupportedBank
import models.responses.SupportedBankResponse
import networking.NetworkServiceImpl
import utilities.ResultWrapper

class BankTransferChannelViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var banksResponse = MutableLiveData<ArrayList<SupportedBank>>()

    @ExperimentalSerializationApi
    fun loadSupportedBanks() {
        isLoading.value = true

        viewModelScope.launch {

            when (val supportedBankResponse = NetworkServiceImpl().getNetworkService().getSupportedBanksForTransfer(PelpaySdk.accessToken!!)){
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(supportedBankResponse)
                is ResultWrapper.Success -> showSuccess(supportedBankResponse.value)
                else -> {
                    isLoading.value = false
                    Log.e("BankTransferChannelVM", "showGenericError:  $banksResponse")
                }
            }
        }
    }

    private fun showSuccess(supportedBankResponse: SupportedBankResponse) {
        isLoading.value = false
        this.banksResponse.value  = supportedBankResponse.responseData
    }

    private fun showGenericError(supportedBankResponse: ResultWrapper.GenericError) {
        isLoading.value = false
        Log.d("BankTransferChannelVM", "GenericError:  $supportedBankResponse")
    }

    private fun showNetworkError() {
        isLoading.value = false
        Log.d("BankTransferChannelVM", "NetworkError")
    }
}