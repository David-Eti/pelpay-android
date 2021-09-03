package ui.bankTransferChannel

import android.app.Dialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
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
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import enums.Environment
import kotlinx.serialization.ExperimentalSerializationApi
import models.responses.SupportedBank
import ui.customWebView.CustomWebViewFragment
import ui.library.LoadingProgressDialog

class BankTransferChannelFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: BankTransferChannelViewModel
    private lateinit var bankTextFieldLayout: TextInputLayout
    private lateinit var payButton: AppCompatButton

    private var banks: ArrayList<SupportedBank> = ArrayList()
    private var adapter: ArrayAdapter<SupportedBank>? = null
    private var selectedBank: SupportedBank? = null


    companion object {
        fun newInstance() = BankTransferChannelFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bank_transfer_channel_fragment, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.bankChannelToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        bankTextFieldLayout = view.findViewById(R.id.bankTextFieldLayout)

        payButton = view.findViewById(R.id.payButton)

        payButton.setBackgroundColor(PelpaySdk.primaryColor)
        payButton.setTextColor(PelpaySdk.primaryTextColor)

        payButton.text = String.format("Pay ₦%s", PelpaySdk.transaction?.amount)

        payButton.isEnabled = false

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


    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val progress = LoadingProgressDialog(requireContext())

        viewModel = ViewModelProvider(this).get(BankTransferChannelViewModel::class.java)
        viewModel.loadSupportedBanks()
        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    progress.show("Getting supported banks...")
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
            val selectedBank: SupportedBank? = adapter?.getItem(position)
            payButton.isEnabled = true

            this.selectedBank  = selectedBank
            Log.d("BankTransferChannelFrag", "selectedBank: $selectedBank id: ${selectedBank?.code}")

//            bankRequest.bankCode = selectedBank?.id
        }

        payButton.setOnClickListener {
            val bankTransferDetailsFragment = BankTransferDetailsFragment(this.selectedBank!!)
            bankTransferDetailsFragment.show(parentFragmentManager, bankTransferDetailsFragment.tag)
        }



    }

}