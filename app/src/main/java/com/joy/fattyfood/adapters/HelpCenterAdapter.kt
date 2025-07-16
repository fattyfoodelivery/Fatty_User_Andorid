package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemHelpCenterBinding
import com.joy.fattyfood.domain.model.HelpCenterVO
import com.joy.fattyfood.viewholder.BaseViewHolder

class HelpCenterAdapter(private val context : Context,private val callbacks : (String) -> Unit) : BaseAdapter<HelpCenterViewHolder,HelpCenterVO>(context) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<HelpCenterVO> {
        return HelpCenterViewHolder(ItemHelpCenterBinding.inflate(LayoutInflater.from(parent.context),parent,false),callbacks)
    }
}

class HelpCenterViewHolder(val binding : ItemHelpCenterBinding,val callbacks: (String) -> Unit) : BaseViewHolder<HelpCenterVO>(binding.root) {
    override fun setData(data: HelpCenterVO, position: Int) {
        binding.tvPh.text = data.phone
        binding.tvType.text = data.type
        binding.btnCall.setOnClickListener {
            callbacks.invoke(data.phone)
        }
    }

    override fun onClick(v: View?) {

    }

}

