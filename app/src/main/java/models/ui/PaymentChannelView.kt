package models.ui

import enums.PaymentChannel


/**
 * Created by Ehigiator David on 19/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
class PaymentChannelView(val title: String? = null, val image: Int?, val channel: PaymentChannel = PaymentChannel.Card)