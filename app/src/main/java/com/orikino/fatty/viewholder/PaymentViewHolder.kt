package com.orikino.fatty.viewholder

import android.view.View
import com.orikino.fatty.databinding.ItemPaymentBinding
import com.orikino.fatty.domain.model.PaymentMethodVO

class PaymentViewHolder(private var bind : ItemPaymentBinding) : BaseViewHolder<PaymentMethodVO>(bind.root) {

    override fun setData(data: PaymentMethodVO, position: Int) {
        bind.root
    }

    override fun onClick(v: View?) {

    }
}