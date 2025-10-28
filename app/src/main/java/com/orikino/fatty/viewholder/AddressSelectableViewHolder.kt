package com.orikino.fatty.viewholder

import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemManageAddressBinding
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder.RecyclerItemSelectedListener
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show

class AddressSelectableViewHolder(
    var binding : ItemManageAddressBinding,
    var callbacks: (CustomerAddressVO,str : String,pos : Int) -> Unit,
    recyclerItemSelectedListener: RecyclerItemSelectedListener) : BaseSelectableViewHolder<CustomerAddressVO>(binding.root, recyclerItemSelectedListener)  {

    private fun checkAddress(isChecked : Boolean) {
        if (isChecked) {
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.fattyPrimary)
            binding.imvCheck.show()
        } else {
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.natural300)
            binding.imvCheck.gone()
        }

    }

    override fun setData(
        mData: CustomerAddressVO,
        isSelected: Boolean,
        isEnable: Boolean
    ) {
        binding.imvSelect.show()
        if (isSelected){
            binding.imvLocation.imageTintList = ContextCompat.getColorStateList(binding.root.context, R.color.fattyPrimary)
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.fattyPrimary)
            binding.imvSelect.setImageResource(R.drawable.radio_check)
        }else{
            binding.imvLocation.imageTintList = ContextCompat.getColorStateList(binding.root.context, R.color.icon_tint)
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.natural300)
            binding.imvSelect.setImageResource(R.drawable.uncheck_circle_18dp)
        }
        when(mData.address_type) {
            "Home" -> {
                binding.imvLocation.setImageResource(R.drawable.ic_type_home)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.txt_home)
            }
            "အိမ်" -> {
                binding.imvLocation.setImageResource(R.drawable.ic_type_home)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.txt_home)
            }
            "家" -> {
                binding.imvLocation.setImageResource(R.drawable.ic_type_home)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.txt_home)
            }
            "Work" -> {
                binding.imvLocation.setImageResource(R.drawable.ic_type_work)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.work)
            }
            "အလုပ်" -> {
                binding.imvLocation.setImageResource(R.drawable.ic_type_work)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.work)
            }
            "工作单位" -> {
                binding.imvLocation.setImageResource(R.drawable.ic_type_work)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.work)
            }
            else ->{
                binding.imvLocation.setImageResource(R.drawable.ic_type_other)
                //binding.tvAddressType.text = ContextCompat.getString(binding.root.context, R.string.other)
            }
        }
        val address = if (mData.building_system.isNullOrEmpty()){
            mData.current_address
        }else{
            mData.current_address.plus("\n").plus(mData.building_system)
        }
        binding.tvAddress.text = address
        if (mData.secondary_phone == null){
            binding.tvPhone.text = mData.customer_phone
        }else{
            binding.tvPhone.text = "${mData.customer_phone }, ${mData.secondary_phone}"
        }

        mData.customer_phone.let { binding.tvPhone.text }


        if (mData.is_default) {
            //binding.imvRadioStatus.setImageResource(R.drawable.radio_check)
            binding.tvDefault.show()
        } else {
            //binding.imvRadioStatus.setImageResource(R.drawable.uncheck_circle_18dp)
            binding.tvDefault.gone()
        }

        if (mData.onTapPositon) {
            checkAddress(mData.onTapPositon)
        }

//        binding.llSetDefault.setOnClickListener {
//            callbacks.invoke(mData,"default",position)
//        }

//        binding.imvDeleteAddress.setOnClickListener {
//            callbacks.invoke(mData,"delete",position)
//        }

        binding.btnUpdate.setOnClickListener {
            callbacks.invoke(mData,"update",position)
        }
    }
}