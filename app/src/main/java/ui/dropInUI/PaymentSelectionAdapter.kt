package ui.dropInUI

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pelpaysamplebuildandroid.R
import enums.PaymentChannel
import models.ui.PaymentChannelView


/**
 * Created by Ehigiator David on 20/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
class PaymentSelectionAdapter(private val itemList: List<PaymentChannelView>) : RecyclerView.Adapter<PaymentSelectionAdapter.ViewHolder>() {
    private var context: Context? = null
    var listener: PaymentSelectionListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentSelectionAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_drop_in_list_dialog_item, parent,
                false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PaymentSelectionAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val paymentChannel = itemList[bindingAdapterPosition]
            val title = paymentChannel.title
            val image = paymentChannel.image
            itemView.findViewById<TextView>(R.id.text).text = title
            itemView.setBackgroundColor(Color.parseColor("#F7F7F7"));

            if (image != null) {
                itemView.findViewById<ImageView>(R.id.channel_image).setImageResource(image)
            }
//            // adding click or tap handler for our image layout
            itemView.setOnClickListener {
                listener?.onSelect(paymentChannel.channel)
            }
        }
    }
}

interface PaymentSelectionListener {
    fun onSelect(channel: PaymentChannel)
}