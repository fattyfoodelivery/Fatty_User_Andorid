package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemManageAddressBinding
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.ui.views.base.BaseSelectableRecyclerAdapter
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.viewholder.AddressSelectableViewHolder
import com.orikino.fatty.viewholder.AddressViewHolder

class AddressSelectableAdapter(context: Context, var callbacks: (CustomerAddressVO,str : String,pos : Int) -> Unit,private val recyclerItemSelectedListener: BaseSelectableViewHolder.RecyclerItemSelectedListener) : BaseSelectableRecyclerAdapter<CustomerAddressVO,BaseSelectableViewHolder<CustomerAddressVO>>(context){
    private lateinit var binding: ItemManageAddressBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSelectableViewHolder<CustomerAddressVO> {

        binding = ItemManageAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressSelectableViewHolder(binding,callbacks, recyclerItemSelectedListener)
    }
}