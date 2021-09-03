package application

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import enums.Environment
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.CreateAdviceRequest
import models.requests.LoginRequest
import models.requests.Transaction
import models.responses.AdviceData
import networking.NetworkServiceImpl
import ui.dropInUI.DropInFragment
import ui.library.LoadingProgressDialog
import utilities.ResultWrapper


/**
 * Created by Ehigiator David on 15/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
object PelpaySdk {
    internal var environment: Environment = Environment.Staging
    internal var clientId = ""
    internal var clientSecret = ""
    internal var accessToken: String? = null
    internal var merchantLogo: Drawable? = null
    internal var primaryColor = Color.parseColor("#009F49")
    internal var primaryTextColor = Color.parseColor("#ffffff")

    private var supportFragment: Fragment? = null
    internal var isCardChannelEnabled = true
    internal var isBankChannelEnabled = true
    internal var isBankTransferChannelEnabled = true
    internal var advice: AdviceData? = null
    internal var transaction: Transaction? = null
    internal var callback: PelpaySdkCallback? = null
    internal var hidePelpayLogo = false

//    fun PelpaySdk(fragment: Fragment?) {
//        supportFragment = fragment
//    }

    fun setTransaction(transaction: Transaction): PelpaySdk {
        this.transaction = transaction
        return this
    }

    fun setBrandPrimaryColor(color: Int): PelpaySdk {
        this.primaryColor = color
        return this
    }

    fun setBrandPrimaryTextColor(color: Int): PelpaySdk {
        this.primaryTextColor = color
        return this
    }

    fun setHidePelpayLogo(isHidden: Boolean): PelpaySdk {
        this.hidePelpayLogo = isHidden
        return this
    }


    fun withCallBack(callback: PelpaySdkCallback): PelpaySdk {
        this.callback = callback
        return this
    }

    @ExperimentalSerializationApi
    suspend fun initialise(environment: Environment = Environment.Staging, clientId: String, clientSecret: String, context: FragmentActivity): PelpaySdk {
        this.environment = environment
        this.clientId = clientId
        this.clientSecret = clientSecret
        val progress = LoadingProgressDialog(context)
        progress.show("Processing...please wait")
        when (val loginResponse = NetworkServiceImpl().getNetworkService().login(LoginRequest(clientId, clientSecret))) {
            is ResultWrapper.Success -> {
                accessToken = loginResponse.value.accessToken
                when (val adviceResponse =
                        NetworkServiceImpl().getNetworkService().createAdvice(transaction!!, token = accessToken!!)) {
                    is ResultWrapper.Success -> {
                        progress.dismiss()
                        advice = adviceResponse.value.responseData
                        displayDropInUI(context)
                    }
                    is ResultWrapper.GenericError -> {
                        progress.dismiss()
                        adviceResponse.error?.message?.let { Log.e("ERR", it) }
                    }
                    is ResultWrapper.AlternateGenericError -> {
                        progress.dismiss()
                        adviceResponse.error?.message?.let { Log.e("ERR", it) }
                    }

                }

            }
            is ResultWrapper.GenericError -> {
                progress.dismiss()
                loginResponse.error?.message?.let { Log.e("ERR", it) }
            }
            is ResultWrapper.AlternateGenericError -> {
                progress.dismiss()
                loginResponse.error?.message?.let { Log.e("ERR", it) }
            }
            else -> {
                progress.dismiss()
                Log.e("ERR", "initialise: Failed")
            }
        }
        return this
    }

    private fun displayDropInUI(context: FragmentActivity) {
        val dropInFragment = DropInFragment.newInstance(3)
        dropInFragment.show(context.supportFragmentManager, dropInFragment.tag)
    }
}