package com.orikino.fatty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orikino.fatty.databinding.LayoutItemOrderInfoBinding
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultFoodName
import com.orikino.fatty.utils.helper.toThousandSeparator
import com.orikino.fatty.viewholder.BaseViewHolder

class OrderInfoAdapter(var context: Context,var callback : (FoodVO,str : String,pos : Int) -> Unit) : BaseAdapter<OrderInfoAdapter.OrderInfoViewHolder,FoodVO>(context) {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FoodVO> {
        return OrderInfoViewHolder(LayoutItemOrderInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class OrderInfoViewHolder(var binding : LayoutItemOrderInfoBinding) : BaseViewHolder<FoodVO>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun setData(data: FoodVO , position: Int) {


            //if (data.order_id != 0) binding.tvFoodNameQty.setTextColor(Color.parseColor("#a9a9a9"))
            if (data.is_cancel != 0) binding.tvAmount.setTextColor(Color.parseColor("#a9a9a9"))
            //if (data.is_cancel != 0) binding.tvAmount.setTextColor(Color.parseColor("#a9a9a9"))
            if (data.is_cancel != 0) binding.tvFoodAddon.setTextColor(Color.parseColor("#a9a9a9"))
            if (data.is_cancel != 0) binding.tvFoodCancel.show()

            binding.tvAmount.text =
                "${data.food_price?.toDouble()?.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            binding.tvFoodNote.text = data.food_note
            binding.tvFoodNameQty.text = "x ${data.food_qty}   ${data.toDefaultFoodName()}"



        }

        override fun onClick(v: View?)  = Unit

    }


}