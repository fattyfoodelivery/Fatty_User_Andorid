package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.LayoutItemAvailableTimeBinding
import com.joy.fattyfood.domain.model.AvailableTimeVO
import com.joy.fattyfood.viewholder.BaseViewHolder

class RestAviableTimeAdapterder(
    val context: Context,
    val callback : (AvailableTimeVO, String, Int) -> Unit
): BaseAdapter<RestAviableTimeAdapterder.AvailableTimeViewHolder,AvailableTimeVO>(context) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<AvailableTimeVO> {
        return AvailableTimeViewHolder(LayoutItemAvailableTimeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class AvailableTimeViewHolder(val binding : LayoutItemAvailableTimeBinding) : BaseViewHolder<AvailableTimeVO>(binding.root) {
        override fun setData(data: AvailableTimeVO, position: Int) {
            println("availabletime $data")
            binding.tvDay.text = data.day
            binding.tvTimeOpenClose.text =  ContextCompat.getString(context,R.string.opening_time).plus(" : ").plus(data.opening_time)
                .plus("  -  ").plus(ContextCompat.getString(context,R.string.closing_time).plus(data.closing_time))
        }

        override fun onClick(v: View?) = Unit

    }



}