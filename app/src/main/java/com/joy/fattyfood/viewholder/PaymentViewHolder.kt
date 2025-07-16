package com.joy.fattyfood.viewholder

import android.view.View
import com.joy.fattyfood.databinding.ItemPaymentBinding
import com.joy.fattyfood.domain.model.PaymentMethodVO

class PaymentViewHolder(private var bind : ItemPaymentBinding) : BaseViewHolder<PaymentMethodVO>(bind.root) {

    override fun setData(data: PaymentMethodVO, position: Int) {
        bind.root
    }

    override fun onClick(v: View?) {

    }
}