package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemManageAddressBinding
import com.joy.fattyfood.domain.model.CustomerAddressVO
import com.joy.fattyfood.viewholder.AddressViewHolder
import com.joy.fattyfood.viewholder.BaseViewHolder

class AddressAdapter(context: Context, var callbacks: (CustomerAddressVO,str : String,pos : Int) -> Unit) : BaseAdapter<AddressViewHolder, CustomerAddressVO>(context){
    private lateinit var binding: ItemManageAddressBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CustomerAddressVO> {

        binding = ItemManageAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding,callbacks)
    }
}
