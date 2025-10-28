package com.orikino.fatty.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemManageAddressBinding
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show

class AddressViewHolder(var binding : ItemManageAddressBinding,var callbacks: (CustomerAddressVO,str : String,pos : Int) -> Unit) : BaseViewHolder<CustomerAddressVO>(binding.root)  {

    private var isChecked = false
    override fun setData(data: CustomerAddressVO, position: Int) {
        when(data.address_type) {
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
        if (data.building_system.isNullOrEmpty()){
            binding.tvBuildingSystem.gone()
        }else{
            binding.tvBuildingSystem.show()
        }
        binding.tvAddress.text = data.current_address
        binding.tvBuildingSystem.text = data.building_system
        if (data.secondary_phone == null){
            binding.tvPhone.text = data.customer_phone
        }else{
            binding.tvPhone.text = "${data.customer_phone }, ${data.secondary_phone}"
        }

        data.customer_phone.let { binding.tvPhone.text }


        if (data.is_default) {
            binding.imvLocation.imageTintList = ContextCompat.getColorStateList(binding.root.context, R.color.fattyPrimary)
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.fattyPrimary)
            //binding.imvRadioStatus.setImageResource(R.drawable.radio_check)
            binding.tvDefault.show()
        } else {
            binding.imvLocation.imageTintList = ContextCompat.getColorStateList(binding.root.context, R.color.icon_tint)
            binding.llManageAddress.strokeColor = ContextCompat.getColor(binding.root.context, R.color.natural300)
            //binding.imvRadioStatus.setImageResource(R.drawable.uncheck_circle_18dp)
            binding.tvDefault.gone()
        }

        if (data.onTapPositon) {
            checkAddress(data.onTapPositon)
        }

        binding.root.setOnClickListener {

            callbacks.invoke(data, "root", position)

        }

//        binding.llSetDefault.setOnClickListener {
//            callbacks.invoke(data,"default",position)
//        }

//        binding.imvDeleteAddress.setOnClickListener {
//            callbacks.invoke(data,"delete",position)
//        }

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