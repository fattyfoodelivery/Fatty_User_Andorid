package com.orikino.fatty.ui.views.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.orikino.fatty.databinding.ItemOrderSubItemBinding
import com.orikino.fatty.domain.model.CreateFoodOption
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toThousandSeparator

class CustomSubItemView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {
    private val binding : ItemOrderSubItemBinding = ItemOrderSubItemBinding.inflate(
        LayoutInflater.from(context), this, true)

    @SuppressLint("SetTextI18n")
    fun bindCheckOut(data : CreateFoodOption){
        binding.tvFoodName.text = data.toDefaultOptionName()
        binding.tvAmount.text =
            "${
                data.food_sub_item_price.toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
    }

    fun bindOrder(data : FoodSubItemVO){
        binding.tvFoodName.text = data.item_name
        binding.tvAmount.text = "${data.food_sub_item_price.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
    }
}