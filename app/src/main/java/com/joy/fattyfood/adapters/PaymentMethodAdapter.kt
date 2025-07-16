package com.joy.fattyfood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemPaymentBinding
import com.joy.fattyfood.domain.model.PaymentMethodVO
import com.joy.fattyfood.utils.delegate.ItemIdDelegate
import com.joy.fattyfood.viewholder.BaseViewHolder

class PaymentMethodAdapter(private var context : Context,val delegate: ItemIdDelegate,val callback : (PaymentMethodVO) -> Unit) : BaseAdapter<PaymentMethodAdapter.PV, PaymentMethodVO>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PaymentMethodVO> {
        return PV(ItemPaymentBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class PV(private var binding : ItemPaymentBinding) : BaseViewHolder<PaymentMethodVO>(binding.root) {

        override fun setData(data: PaymentMethodVO, position: Int) {

            /*if (data.payment_method_id == 2) this.imv.load(R.drawable.kbz)
            else this.imv.load(R.drawable.ic_payments)
            if (data.payment_method_id == 1) this.tv_payment_name.text =
                resources.getString(R.string.payment_methods)
            else this.tv_payment_name.text = data.payment_method_name
            this.radio_payment_method.isChecked = lastSelected == pos

            this.ll_payment_method.setOnClickListener {
                lastSelected = pos
                viewModel.paymentMethodID = data.payment_method_id
                this@CartActivity.rvPaymentMethod.adapter?.notifyDataSetChanged()
            }
            this.radio_payment_method.setOnClickListener {
                lastSelected = pos
                viewModel.paymentMethodID = data.payment_method_id
                this@CartActivity.rvPaymentMethod.adapter?.notifyDataSetChanged()
            }*/
            /*if (data.payment_method_id == 2) binding.imvPayment.load(R.drawable.kbz_payment)
            else binding.imvPayment.load(R.drawable.ic_cash)
            binding.tvPaymentName.text = data.payment_method_name

            if (data.payment_method_id == 2) {
                binding.imvPayment.load(R.drawable.kbz_payment)
                binding.imvStatus.setImageResource(R.drawable.radio_check)
                binding.llPaymentMethod.setBackgroundResource(R.drawable.bg_selected)
            } else {
                binding.imvPayment.load(R.drawable.ic_cash)
                binding.imvStatus.setImageResource(R.drawable.radio_check)
                binding.llPaymentMethod.setBackgroundResource(R.drawable.bg_unselectd)

            }*/



        }

        override fun onClick(v: View?) {

        }

    }
}