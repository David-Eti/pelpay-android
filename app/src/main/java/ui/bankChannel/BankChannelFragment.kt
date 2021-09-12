package ui.bankChannel

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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
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
import enums.Environment
import enums.PaymentChannel
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.CardRequest
import models.requests.ProcessBankPaymentRequest
import models.responses.BanksResponse
import ui.customWebView.CustomWebViewFragment
import ui.library.LoadingProgressDialog
import ui.otpView.OtpFragment

class BankChannelFragment : BottomSheetDialogFragment() {

    lateinit var accountNumberInputLayout: TextInputLayout
    lateinit var accountNameInputLayout: TextInputLayout

    lateinit var accountNumberEditText: TextInputEditText
    lateinit var accountNameEditText: TextInputEditText
    lateinit var payButton: AppCompatButton

    lateinit var bankTextFieldLayout: TextInputLayout
    var banks: ArrayList<BanksResponse> = ArrayList()
    var adapter: ArrayAdapter<BanksResponse>? = null
    private val bankRequest = ProcessBankPaymentRequest()

    companion object {
        fun newInstance() = BankChannelFragment()
    }


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

    private lateinit var viewModel: BankChannelViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.bank_channel_fragment, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.bankToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        bankTextFieldLayout = view.findViewById(R.id.bankTextFieldLayout)
        accountNumberInputLayout = view.findViewById(R.id.accountNumberInputLayout)
        accountNameInputLayout = view.findViewById(R.id.accountNameInputLayout)
        accountNumberEditText = view.findViewById(R.id.accountNumberEditText)
        accountNameEditText = view.findViewById(R.id.accountNameEditText)

        payButton = view.findViewById(R.id.payButton)

        payButton.setBackgroundColor(PelpaySdk.primaryColor)
        payButton.setTextColor(PelpaySdk.primaryTextColor)
        payButton.text = String.format("Pay ₦%s", PelpaySdk.transaction?.amount)


        if(PelpaySdk.hidePelpayLogo){
            val securedLogo: ImageView = view.findViewById(R.id.secured_logo)
            securedLogo.visibility = View.INVISIBLE
        }
        if(PelpaySdk.environment == Environment.Production){
            val testLayout: LinearLayout = view.findViewById(R.id.test_layout)
            testLayout.visibility = View.GONE
        }

        return view
    }

    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val progress = LoadingProgressDialog(requireContext())


        viewModel = ViewModelProvider(this).get(BankChannelViewModel::class.java)

        viewModel.loadBanks()

        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    progress.show()
                } else {
                    progress.dismiss()
                }
            }
        })


        viewModel.banksResponse.observe(this, { banksResponse ->

            banks = banksResponse
            adapter = ArrayAdapter(requireContext(), R.layout.bank_list_item, banks)
            (bankTextFieldLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        })

        (bankTextFieldLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            val selectedBank: BanksResponse? = adapter?.getItem(position)

            Log.d("BankFragment", "selectedBank: $selectedBank id: ${selectedBank?.id}")

            bankRequest.bankCode = selectedBank?.id
        }
        accountNumberEditText.addTextChangedListener(afterTextChanged = {
            try {
                if (it.toString().length > 10 || it.toString().length < 10){
                    accountNumberInputLayout.error = "Please provide a valid account"
                }
                else {
                    bankRequest.accountNumber = it.toString()
                    accountNumberInputLayout.error = null

                    accountNameEditText.requestFocus()
                }

            } catch (e: Exception) {
                accountNumberInputLayout.error = "Invalid Bank Account"
            }

        })

        accountNameEditText.addTextChangedListener(afterTextChanged = {
            try {
                if (it.toString().length < 3){
                    accountNameInputLayout.error = "Please provide a valid Account name"
                }
                else {
                    bankRequest.nameOnAccount = it.toString()
                    accountNameInputLayout.error = null

                    accountNameEditText.requestFocus()
                }

            } catch (e: Exception) {
                accountNameInputLayout.error = "Invalid Account name"
            }

        })
        payButton.setOnClickListener {
            Log.d("BankFragment", "onActivityCreated: $bankRequest")
            viewModel.processBankRequest(bankRequest)
        }


        viewModel.processBankResponse.observe(this, { processBankResponse ->
            when (processBankResponse?.responseData?.status?.lowercase()) {
                "pending" -> {

                }
                "processing" -> {

                }
                "successful" -> {

                }
                "secure3d" -> {
//                    val customWebViewFragment = CustomWebViewFragment(cardResponse)
//                    customWebViewFragment.show(parentFragmentManager, customWebViewFragment.tag)
                }
                "otp" -> {
                    val otpFragment = OtpFragment(paymentChannel = PaymentChannel.Bank, paymentReference = processBankResponse.responseData.paymentReference!!,message = processBankResponse.responseData.message) //from bank payment channel
                    otpFragment.show(parentFragmentManager, otpFragment.tag)
                }
                "authsetup" -> {
//                    val customWebViewFragment = CustomWebViewFragment(cardResponse)
//                    customWebViewFragment.show(parentFragmentManager, customWebViewFragment.tag)
                }
                "failed" -> {

                }
            }

        })

    }

}