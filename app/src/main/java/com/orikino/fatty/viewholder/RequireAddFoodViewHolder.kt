package com.orikino.fatty.viewholder

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.databinding.LayoutItemRequireRadioButtonBinding
import com.orikino.fatty.databinding.LayoutItemRequireRecyclerBinding
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.utils.bind
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toDefaultSectionName
import com.orikino.fatty.utils.helper.toThousandSeparator

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
