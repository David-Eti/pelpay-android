package ui.bankChannel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.PelpaySdk
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.ProcessBankPaymentRequest
import models.responses.BanksResponse
import models.responses.ProcessBankPaymentResponse
import models.responses.ProcessCardResponse
import networking.NetworkServiceImpl
import utilities.ResultWrapper

class BankChannelViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var banksResponse = MutableLiveData<ArrayList<BanksResponse>>()
    var processBankResponse = MutableLiveData<ProcessBankPaymentResponse?>()

    @ExperimentalSerializationApi
    fun loadBanks() {
        isLoading.value = true
        viewModelScope.launch {
            when (val banksResponse = NetworkServiceImpl().getNetworkService().getBanks(PelpaySdk.accessToken!!)) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(banksResponse)
                is ResultWrapper.Success -> showSuccess(banksResponse.value)
                else -> {
                    isLoading.value = false
                    Log.e("BankChannelViewModel", "showGenericError:  $banksResponse")
                }
            }

        }
    }

    @ExperimentalSerializationApi
    fun processBankRequest(processBankPaymentRequest: ProcessBankPaymentRequest) {
        isLoading.value = true
        viewModelScope.launch {
            when (val bankPaymentResponse = NetworkServiceImpl().getNetworkService().processBankPayment(adviceReference = PelpaySdk.advice!!.adviceReference!!, processBankPaymentRequest = processBankPaymentRequest, token = PelpaySdk.accessToken!!)) {
                is ResultWrapper.NetworkError -> {
                    isLoading.value = false
                    Log.d("BankChannelViewModel", "NetworkError:  $banksResponse")

                }
                is ResultWrapper.GenericError -> {
                    isLoading.value = false
                    Log.d("BankChannelViewModel", "GenericError:  $banksResponse")

                }
                is ResultWrapper.Success -> {
                    isLoading.value = false
                    processBankResponse.value = bankPaymentResponse.value
                }
            }
        }
    }

    private fun showSuccess(banksResponse: ArrayList<BanksResponse>) {
        isLoading.value = false
        this.banksResponse.value = banksResponse
        Log.d("BankChannelViewModel", "showSuccess:  $banksResponse")

    }

    private fun showGenericError(response: Any) {
        TODO("Not yet implemented")
    }

    private fun showNetworkError() {
        TODO("Not yet implemented")
    }
}