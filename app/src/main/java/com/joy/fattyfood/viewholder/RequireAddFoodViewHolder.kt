package com.joy.fattyfood.viewholder

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joy.fattyfood.databinding.LayoutItemRequireRadioButtonBinding
import com.joy.fattyfood.databinding.LayoutItemRequireRecyclerBinding
import com.joy.fattyfood.domain.model.FoodSubItemVO
import com.joy.fattyfood.domain.model.OptionVO
import com.joy.fattyfood.utils.bind
import com.joy.fattyfood.utils.helper.toDefaultOptionName
import com.joy.fattyfood.utils.helper.toDefaultSectionName
import com.joy.fattyfood.utils.helper.toThousandSeparator

class RequireAddFoodViewHolder(var binding: LayoutItemRequireRecyclerBinding, val onSelectItem: (data: OptionVO, foodSubItemId: Int, isRequire: Int) -> Unit) : BaseViewHolder<FoodSubItemVO>(
    binding.root
) {

    private var defaultSelectedFoodPrice = 0.0

    override fun setData(data: FoodSubItemVO, position: Int) {
        mData = data
        setUpRequiredItemRecycler(data, data.option)
    }

    fun setUpRequiredItemRecycler(
        data: FoodSubItemVO,
        subFoodItem: MutableList<OptionVO>
    ) {
        binding.tvRequireTitle.text = data.toDefaultSectionName()

        val adapter = binding.rvRequired.bind(subFoodItem) {
                option: OptionVO,_ ->
            val innerBind = LayoutItemRequireRadioButtonBinding.inflate(LayoutInflater.from(context))
            innerBind.radio.text = option.toDefaultOptionName()
            innerBind.tvRequiredFoodPrice.text = "${option.food_sub_item_price.toDouble().toThousandSeparator()} "  //${PreferenceUtils.readCurrencyId()?.currency_symbol}
            /*innerBind.radio.isChecked = option.isSelected
            if (option.isSelected) {
                innerBind.radio.setTextColor(Color.parseColor("#FF6604"))
                innerBind.radio.isEnabled = false
                defaultSelectedFoodPrice += option.food_sub_item_price.toDouble()
                PreferenceUtils.selectedFoodItemPrice = defaultSelectedFoodPrice
            }*/

            innerBind.radio.setOnClickListener {
                onSelectItem(option, option.food_sub_item_id, data.required_type)
            }
        }.layoutManager(
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        )
        binding.rvRequired.adapter = adapter
    }

    override fun onClick(v: View?) {
    }
}
