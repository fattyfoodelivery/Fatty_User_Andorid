package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemManageAddressBinding
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.viewholder.AddressViewHolder
import com.orikino.fatty.viewholder.BaseViewHolder

class AddressAdapter(context: Context, var callbacks: (CustomerAddressVO,str : String,pos : Int) -> Unit) : BaseAdapter<AddressViewHolder, CustomerAddressVO>(context){
    private lateinit var binding: ItemManageAddressBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CustomerAddressVO> {

        binding = ItemManageAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding,callbacks)
    }
}
