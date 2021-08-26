package ui.bankTransferChannel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.PelpaySdk
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.BankTransferDetailRequest
import models.responses.BankTransferDetail
import models.responses.BankTransferDetailResponse
import models.responses.SupportedBank
import networking.NetworkServiceImpl
import utilities.ResultWrapper

class BankTransferDetailsViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()

    var bankDetail = MutableLiveData<BankTransferDetail>()

    @ExperimentalSerializationApi
    fun loadBankDetails(bankCode: String) {
        viewModelScope.launch {
            isLoading.value = true

            when (val bankTransferDetailsResponse = NetworkServiceImpl().getNetworkService().processBankTransfer(adviceReference = PelpaySdk.advice?.adviceReference!!, BankTransferDetailRequest(bankCode = bankCode), token = PelpaySdk.accessToken!!)) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(bankTransferDetailsResponse)
                is ResultWrapper.Success -> showSuccess(bankTransferDetailsResponse.value)
                else -> {
                    isLoading.value = false
                    Log.e("BankTransferDetailsVM", "showGenericError:  $bankTransferDetailsResponse")
                }
            }

        }
    }

    private fun showSuccess(detailResponse: BankTransferDetailResponse) {
        isLoading.value = false
        this.bankDetail.value = detailResponse.responseData
    }

    private fun showGenericError(detailsResponse: ResultWrapper.GenericError) {
        isLoading.value = false
        Log.d("BankTransferDetailsVM", "GenericError:  $detailsResponse")
    }

    private fun showNetworkError() {
        isLoading.value = false
        Log.d("BankTransferDetailsVM", "NetworkError")
    }
}