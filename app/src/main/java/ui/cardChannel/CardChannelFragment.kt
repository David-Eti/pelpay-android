package ui.cardChannel

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import enums.PaymentChannel
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.CardRequest
import ui.customWebView.CustomWebViewFragment
import ui.library.LoadingProgressDialog
import ui.otpView.OtpFragment
import java.security.InvalidParameterException

class CardChannelFragment : BottomSheetDialogFragment() {

    lateinit var cardNumberInputLayout: TextInputLayout
    lateinit var expiryInputLayout: TextInputLayout
    lateinit var cvvInputLayout: TextInputLayout
    lateinit var cardPinLayout: TextInputLayout

    lateinit var cardNumber: TextInputEditText
    lateinit var expiry: TextInputEditText
    lateinit var cvv: TextInputEditText
    lateinit var cardPin: TextInputEditText
    lateinit var payButton: AppCompatButton

    private lateinit var cardType: ImageView

    private var isCardNumberError: Boolean = true
    private var isCardCvvError: Boolean = true
    private var isCardExpiryError: Boolean = true
    private var isCardPinError: Boolean = true

    private var canContinue = false
    private val cardRequest = CardRequest()

    companion object {
        fun newInstance() = CardChannelFragment()
    }

    private lateinit var viewModel: CardChannelViewModel

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
        val view = inflater.inflate(R.layout.card_channel_fragment, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.cardToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        cardNumber = view.findViewById(R.id.card_number)
        expiry = view.findViewById(R.id.expiry)
        cvv = view.findViewById(R.id.cvv)
        cardPin = view.findViewById(R.id.cardPin)

        cardNumberInputLayout = view.findViewById(R.id.cardNumberInputLayout)
        expiryInputLayout = view.findViewById(R.id.expiryInputLayout)
        cvvInputLayout = view.findViewById(R.id.cvvInputLayout)
        cardPinLayout = view.findViewById(R.id.cardPinInputLayout)

        payButton = view.findViewById(R.id.payButton)

        cardType = view.findViewById(R.id.card_type)

        payButton.text = String.format("Pay ₦%s", PelpaySdk.transaction?.amount)



        return view
    }

    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CardChannelViewModel::class.java)

        val progress = LoadingProgressDialog(requireContext())

        cardNumber.addTextChangedListener(afterTextChanged = {
            try {
                cardRequest.cardNumber = it.toString()
                when (cardRequest.cardType?.issuerName) {
                    "MASTER" -> cardType.setImageResource(R.drawable.master_card)
                    "VISA" -> cardType.setImageResource(R.drawable.visa_card)
                    "VERVE" -> cardType.setImageResource(R.drawable.verve_card)
                }

                isCardNumberError = false
                cardNumberInputLayout.error = null

                expiry.requestFocus()
            } catch (e: Exception) {
                if (it.toString().length > 15) cardNumberInputLayout.error = "Invalid Card Number"
                cardType.setImageResource(0)
                isCardNumberError = true
            }

        })

        cvv.addTextChangedListener(afterTextChanged = {
            try {
                cardRequest.cvv = it.toString()
                isCardCvvError = false
                cvvInputLayout.error = null
                cardPin.requestFocus()

            } catch (error: java.lang.Exception) {
                cvvInputLayout.error = "Invalid Card CVV"
                isCardCvvError = true
            }
        })

        expiry.addTextChangedListener(afterTextChanged = {
            try {
                val exp = it.toString().split("/")
                cardRequest.expiredMonth = exp[0]
                cardRequest.expiredYear = exp[1]
                cvv.requestFocus()
                isCardExpiryError = false
                expiryInputLayout.error = null
            } catch (ex: InvalidParameterException) {
                expiryInputLayout.error = ex.message
                isCardExpiryError = true
            } catch (ex: java.lang.Exception) {
                isCardExpiryError = true
            }
        })

        cardPin.addTextChangedListener(afterTextChanged = {
            try {
                cardRequest.cardPin = it.toString()
                isCardPinError = false
                cardPinLayout.error = null
            } catch (ex: InvalidParameterException) {
                cardPinLayout.error = ex.message
                isCardPinError = true
            } catch (ex: java.lang.Exception) {
                isCardPinError = true
            }
        })


        payButton.setOnClickListener {
            viewModel.processCardRequest(cardRequest)
        }

        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    progress.show()
                } else {
                    progress.dismiss()
                }
            }
        })

        viewModel.processCardResponse.observe(this, { cardResponse ->
            when (cardResponse.responseData?.transactionStatus?.lowercase()) {
                "pending" -> {

                }
                "processing" -> {

                }
                "successful" -> {

                }
                "secure3d" -> {
                    val customWebViewFragment = CustomWebViewFragment(cardResponse)
                    customWebViewFragment.show(parentFragmentManager, customWebViewFragment.tag)
                }
                "otp" -> {
                    val otpFragment = OtpFragment(paymentChannel = PaymentChannel.Card, paymentReference = cardResponse.responseData.paymentReference!!) //from card payment channel
                    otpFragment.show(parentFragmentManager, otpFragment.tag)
                }
                "authsetup" -> {
                    val customWebViewFragment = CustomWebViewFragment(cardResponse)
                    customWebViewFragment.show(parentFragmentManager, customWebViewFragment.tag)
                }
                "failed" -> {

                }
            }

        })

    }

}