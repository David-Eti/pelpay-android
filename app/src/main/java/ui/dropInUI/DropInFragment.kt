package ui.dropInUI

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import enums.PaymentChannel
import models.ui.PaymentChannelView
import ui.bankChannel.BankChannelFragment
import ui.bankTransferChannel.BankTransferChannelFragment
import ui.cardChannel.CardChannelFragment


// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    DropInFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class DropInFragment : BottomSheetDialogFragment(), PaymentSelectionListener {


    private lateinit var itemAdapter: PaymentSelectionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // init adapter
        itemAdapter = PaymentSelectionAdapter(getPaymentChannelViews())
        itemAdapter.listener = this
        // init recyclerview
        val view = inflater.inflate(R.layout.fragment_drop_in_list_dialog, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.selectionToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        })
        val recyclerView: RecyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = itemAdapter



        return view
    }


    companion object {

        fun newInstance(itemCount: Int): DropInFragment =
                DropInFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }
    }

    private fun getPaymentChannelViews(): ArrayList<PaymentChannelView> {
        val channels: ArrayList<PaymentChannelView> = ArrayList()
        if (PelpaySdk.isCardChannelEnabled) {
            val cardChannelUI = PaymentChannelView(title = "Pay with Card", image = R.drawable.ic_card_channel, channel = PaymentChannel.Card)
            channels.add(cardChannelUI)
        }
        if (PelpaySdk.isBankChannelEnabled) {
            val bankChannelUI = PaymentChannelView(title = "Pay with Bank", image = R.drawable.ic_bank_channel, channel = PaymentChannel.Bank)
            channels.add(bankChannelUI)
        }
        if (PelpaySdk.isBankTransferChannelEnabled) {
            val bankTransferChannelUI = PaymentChannelView(title = "Pay with Bank Transfer", image = R.drawable.ic_transfer_channel, channel = PaymentChannel.BankTransfer)
            channels.add(bankTransferChannelUI)
        }

        return channels
    }

    override fun onSelect(channel: PaymentChannel) {
        when (channel) {
            PaymentChannel.Card -> {
                val cardChannelFragment = CardChannelFragment.newInstance()
                cardChannelFragment.show(parentFragmentManager, cardChannelFragment.tag)
            }
            PaymentChannel.Bank  -> {
                val bankChannelFragment = BankChannelFragment.newInstance()
                bankChannelFragment.show(parentFragmentManager, bankChannelFragment.tag)

            }
            PaymentChannel.BankTransfer  -> {
                val bankTransferChannelFragment = BankTransferChannelFragment.newInstance()
                bankTransferChannelFragment.show(parentFragmentManager, bankTransferChannelFragment.tag)
            }
        }
    }
}