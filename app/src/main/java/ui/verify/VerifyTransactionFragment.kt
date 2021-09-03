package ui.verify

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.serialization.ExperimentalSerializationApi
import ui.library.LoadingProgressDialog

class VerifyTransactionFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = VerifyTransactionFragment()
    }

    private lateinit var transactionIconImageView: ImageView
    private lateinit var payButton: AppCompatButton
    private lateinit var retryButton: AppCompatButton
    private lateinit var transactionTitleTextView: TextView
    private lateinit var transactionSubtitleTextView: TextView

    private lateinit var viewModel: VerifyTransactionViewModel

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
        val view = inflater.inflate(R.layout.verify_transaction_fragment, container, false)

        payButton = view.findViewById(R.id.payButton)
        transactionIconImageView = view.findViewById(R.id.transactionIconImageView)
        retryButton = view.findViewById(R.id.retryButton)
        transactionTitleTextView = view.findViewById(R.id.transactionTitleTextView)
        transactionSubtitleTextView = view.findViewById(R.id.transactionSubtitleTextView)

        payButton.setBackgroundColor(PelpaySdk.primaryColor)
        payButton.setTextColor(PelpaySdk.primaryTextColor)
        payButton.text = "Done"

        if(PelpaySdk.hidePelpayLogo){
            val securedLogo: ImageView = view.findViewById(R.id.secured_logo)
            securedLogo.visibility = View.INVISIBLE
        }

        return view
    }

    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VerifyTransactionViewModel::class.java)
        val progress = LoadingProgressDialog(requireContext())

        viewModel.getTransactionDetails()

        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading != null) {
                if (isLoading) {
                    progress.show("Please wait...")
                } else {
                    progress.dismiss()
                }
            }
        })

        payButton.setOnClickListener {
            parentFragmentManager.fragments.let {
                if (it.isNotEmpty()) {
                    if (viewModel.isSuccessFul.value == true) {
                        PelpaySdk.callback?.onSuccess(PelpaySdk.advice?.adviceReference)
                    } else {
                        PelpaySdk.callback?.onError(viewModel.errorMessage.value)
                    }

                    parentFragmentManager.beginTransaction().apply {
                        for (fragment in it) {
                            remove(fragment)
                        }
                        commit()
                    }
                }
            }
        }
        retryButton.setOnClickListener {
            viewModel.getTransactionDetails()
        }

        viewModel.verifiedTransactionDetails.observe(this, { verifiedTransaction ->
            transactionTitleTextView.text = verifiedTransaction.transactionStatus

            if (verifiedTransaction.transactionStatus?.lowercase()?.contains("success") == true) {
                transactionIconImageView.setImageResource(R.drawable.ic_success_icon)
                transactionSubtitleTextView.text = verifiedTransaction.message
                retryButton.visibility = View.GONE
            } else {
                transactionIconImageView.setImageResource(R.drawable.ic_error_icon)
                "You attempted to make a payment of ${PelpaySdk.advice?.currency} ${PelpaySdk.advice?.amount}. You can also click retry.".also { transactionSubtitleTextView.text = it }
                retryButton.visibility = View.VISIBLE
            }

        })
    }

}