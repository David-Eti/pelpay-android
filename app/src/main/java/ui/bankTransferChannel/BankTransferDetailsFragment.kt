package ui.bankTransferChannel

import android.app.Dialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.serialization.ExperimentalSerializationApi
import models.responses.ProcessCardResponse
import models.responses.SupportedBank
import ui.library.LoadingProgressDialog
import ui.verify.VerifyTransactionFragment

class BankTransferDetailsFragment constructor(var selectedBank: SupportedBank)  : BottomSheetDialogFragment() {

    private lateinit var bankNameTextView: TextView

    private lateinit var bankAccountNumberTextView: TextView


    private lateinit var payButton: AppCompatButton



    private lateinit var viewModel: BankTransferDetailsViewModel






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
        val view = inflater.inflate(R.layout.bank_transfer_details_fragment, container, false)


        val toolbar: Toolbar = view.findViewById(R.id.bankChannelDetailsToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        bankNameTextView = view.findViewById(R.id.bankNameTextView)
        bankAccountNumberTextView = view.findViewById(R.id.bankAccountNumberTextView)

        payButton = view.findViewById(R.id.payButton)

        payButton.setBackgroundColor(PelpaySdk.primaryColor)
        payButton.setTextColor(PelpaySdk.primaryTextColor)

        payButton.text = String.format("I have transferred ₦%s", PelpaySdk.transaction?.amount)

        if(PelpaySdk.hidePelpayLogo){
            val securedLogo: ImageView = view.findViewById(R.id.secured_logo)
            securedLogo.visibility = View.INVISIBLE
        }


        return view
    }

    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BankTransferDetailsViewModel::class.java)

        val progress = LoadingProgressDialog(requireContext())

        viewModel.loadBankDetails(selectedBank.code!!)

        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    progress.show("Preparing account for transfer...")
                } else {
                    progress.dismiss()
                }
            }
        })

        viewModel.bankDetail.observe(this, {bankDetails ->

            bankNameTextView.text = selectedBank.bankName
            bankAccountNumberTextView.text = bankDetails.bankAccount

        })

        payButton.setOnClickListener {
            val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
            verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
        }

    }

}