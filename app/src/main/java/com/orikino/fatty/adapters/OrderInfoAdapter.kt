package com.orikino.fatty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orikino.fatty.R
import com.orikino.fatty.databinding.LayoutItemOrderInfoBinding
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultFoodName
import com.orikino.fatty.utils.helper.toThousandSeparator
import com.orikino.fatty.viewholder.BaseViewHolder
import androidx.core.graphics.toColorInt

class OrderInfoAdapter(var context: Context,var callback : (FoodVO,str : String,pos : Int) -> Unit) : BaseAdapter<OrderInfoAdapter.OrderInfoViewHolder,FoodVO>(context) {

    private var currency : String = "MMK"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FoodVO> {
        return OrderInfoViewHolder(LayoutItemOrderInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    inner class OrderInfoViewHolder(var binding : LayoutItemOrderInfoBinding) : BaseViewHolder<FoodVO>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun setData(data: FoodVO , position: Int) {
            if (data.is_cancel != 0) {
                binding.tvFoodAddon.setTextColor("#EF0E00".toColorInt())
                binding.tvAmount.setTextColor("#EF0E00".toColorInt())
                binding.tvFoodNote.setTextColor("#EF0E00".toColorInt())
                binding.tvFoodNameQty.setTextColor("#EF0E00".toColorInt())
                //binding.tvFoodCancel.show()
                binding.tvAmount.text = if (currency == "MMK"){
                    "- ${data.food_price?.toDouble()?.toThousandSeparator()} ${currency}"
                }else{
                    "- ${data.food_price_currency?.toDouble()?.toThousandSeparator()} ${currency}"
                }
            }else{
                binding.tvAmount.text = if (currency == "MMK"){
                    "${data.food_price?.toDouble()?.toThousandSeparator()} ${currency}"
                }else{
                    "${data.food_price_currency?.toDouble()?.toThousandSeparator()} ${currency}"
                }
            }
            binding.tvFoodNote.text = data.food_note
            binding.tvFoodNameQty.text = "x ${data.food_qty}   ${data.toDefaultFoodName()}"



        }

        override fun onClick(v: View?)  = Unit

    }


}