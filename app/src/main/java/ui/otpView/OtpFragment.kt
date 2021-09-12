package ui.otpView

import android.app.Dialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import enums.PaymentChannel
import kotlinx.serialization.ExperimentalSerializationApi
import models.responses.ProcessCardResponse
import ui.callbackWeb.CallBackWebFragment
import ui.customWebView.CustomWebViewFragment
import ui.dropInUI.ARG_ITEM_COUNT
import ui.dropInUI.DropInFragment
import ui.library.LoadingProgressDialog
import ui.verify.VerifyTransactionFragment
import java.lang.ref.PhantomReference
import java.security.InvalidParameterException


class OtpFragment constructor(var paymentReference: String, var message: String?, var paymentChannel: PaymentChannel) : BottomSheetDialogFragment() {


    private lateinit var viewModel: OtpViewModel
    lateinit var otpInputLayout: TextInputLayout
    lateinit var otpEditText: TextInputEditText
    lateinit var payButton: AppCompatButton
    lateinit var otpMessageTextView: TextView


    private var otp: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.otp_fragment, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.otpToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        })

        otpInputLayout = view.findViewById(R.id.otpInputLayout)
        otpEditText = view.findViewById(R.id.otpEditText)
        otpMessageTextView = view.findViewById(R.id.otpMessageTextView)
        payButton = view.findViewById(R.id.payButton)

        payButton.setBackgroundColor(PelpaySdk.primaryColor)
        payButton.setTextColor(PelpaySdk.primaryTextColor)
        payButton.text = String.format("Complete Payment ₦%s", PelpaySdk.transaction?.amount)
        otpMessageTextView.text  = message
        payButton.isEnabled = false

        if(PelpaySdk.hidePelpayLogo){
            val securedLogo: ImageView = view.findViewById(R.id.secured_logo)
            securedLogo.visibility = View.INVISIBLE
        }


        return view
    }


    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OtpViewModel::class.java)
        val progress = LoadingProgressDialog(requireContext())

        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    progress.show()
                } else {
                    progress.dismiss()
                }
            }
        })

        otpEditText.addTextChangedListener(afterTextChanged = {
            try {
                if (it.toString().length < 3) {
                    otpInputLayout.error = "Please provide a valid account"
                    payButton.isEnabled = false
                } else {
                    payButton.isEnabled = true
                    this.otp = it.toString()
                    otpInputLayout.error = null
                }

            } catch (e: Exception) {
                otpInputLayout.error = "Invalid Input"
                payButton.isEnabled = false
            }

        })

        payButton.setOnClickListener {
            if (paymentChannel == PaymentChannel.Card) {
                viewModel.completeCardTransaction(otp = this.otp!!, paymentReference = paymentReference)
            } else if (paymentChannel == PaymentChannel.Bank) {
                viewModel.completeBankTransaction(otp = this.otp!!, paymentReference = paymentReference)
            }

        }

        viewModel.completeCardResponse.observe(this, { completedCardResponse ->
            Log.d("OTPFragment", "completedCardResponse: $completedCardResponse")
            when (completedCardResponse.responseData?.transactionStatus?.lowercase()) {
                "callbacklisten" -> {
                    val callBackWebFragment = CallBackWebFragment(callBackUrl = completedCardResponse.responseData.returnURL)
                    callBackWebFragment.show(parentFragmentManager, callBackWebFragment.tag)
                }
                "successful" -> {
                    val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
                    verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
                }
                else -> {
                    val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
                    verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
                }

            }
        })

        viewModel.completeBankPaymentResponse.observe(this, { completedBankResponse ->
            Log.d("OTPFragment", "completedBankResponse: $completedBankResponse")
            when (completedBankResponse.responseData.transactionStatus.lowercase()) {
                "callbacklisten" -> {
                    val callBackWebFragment = CallBackWebFragment(callBackUrl = completedBankResponse.responseData.returnUrl)
                    callBackWebFragment.show(parentFragmentManager, callBackWebFragment.tag)
                }
                "successful" -> {
                    val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
                    verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
                }
                else -> {
                    val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
                    verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
                }

            }

        })

    }

}