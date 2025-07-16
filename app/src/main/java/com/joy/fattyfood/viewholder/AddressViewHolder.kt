package com.joy.fattyfood.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemManageAddressBinding
import com.joy.fattyfood.domain.model.CustomerAddressVO
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show

class AddressViewHolder(var binding : ItemManageAddressBinding,var callbacks: (CustomerAddressVO,str : String,pos : Int) -> Unit) : BaseViewHolder<CustomerAddressVO>(binding.root)  {

    private var isChecked = false
    override fun setData(data: CustomerAddressVO, position: Int) {


        binding.tvAddressType.text = data.address_type
        binding.tvAddress.text = data.current_address
        data.customer_phone.let { binding.tvPhone.text }


        if (data.is_default) {
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.fattyPrimary)
            binding.imvRadioStatus.setImageResource(R.drawable.radio_check)
        } else {
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.natural300)
            binding.imvRadioStatus.setImageResource(R.drawable.uncheck_circle_18dp)

        }

        if (data.onTapPositon) {
            checkAddress(data.onTapPositon)
        }

        binding.root.setOnClickListener {

            callbacks.invoke(data, "root", position)
            
        }

        binding.llSetDefault.setOnClickListener {
            callbacks.invoke(data,"default",position)
        }

        binding.imvDeleteAddress.setOnClickListener {
            callbacks.invoke(data,"delete",position)
        }

        binding.btnUpdate.setOnClickListener {
            callbacks.invoke(data,"update",position)
        }



    }

    private fun checkAddress(isChecked : Boolean) {
        if (isChecked) {
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.fattyPrimary)
            binding.imvCheck.show()
        } else {
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.natural300)
            binding.imvCheck.gone()
        }

    }
    override fun onClick(v: View?) {

    }
}