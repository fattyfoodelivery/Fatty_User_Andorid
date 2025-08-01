package com.orikino.fatty.viewholder

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.databinding.LayoutItemAddOnsCheckboxBinding
import com.orikino.fatty.databinding.LayoutItemNonRequiredRecyclerBinding
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.utils.bind
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toDefaultSectionName
import com.orikino.fatty.utils.helper.toThousandSeparator

class NonRequireAddFoodViewHolder(var binding : LayoutItemNonRequiredRecyclerBinding,val onSelectItem : (data : OptionVO, foodSubItemId : Int, isRequire : Int)-> Unit) : BaseViewHolder<FoodSubItemVO>(binding.root)  {

    private var defaultSelectedFoodPrice = 0.0

    override fun setData(data: FoodSubItemVO, position: Int) {
        mData = data
        setUpOptionRequiredItemRecycler(data, data.option)

    }

    private fun setUpOptionRequiredItemRecycler(
        data: FoodSubItemVO,
        subFoodItem: MutableList<OptionVO>
    ) {
        binding.tvNonRequireTitle.text = data.toDefaultSectionName()
        val adapter = binding.rvNonRequire.bind(subFoodItem) {
                option: OptionVO, _: Int ->
            val innerBind = LayoutItemAddOnsCheckboxBinding.inflate(LayoutInflater.from(context))
            innerBind.checkbox.text = option.toDefaultOptionName()
            innerBind.tvFoodPrice.text = "${option.food_sub_item_price.toDouble().toThousandSeparator()} "//${PreferenceUtils.readCurrencyId()?.currency_symbol}"
            /*innerBind.checkbox.isChecked = option.isSelected
            if (option.isSelected) {
                innerBind.checkbox.setTextColor(Color.parseColor("#FF6604"))
                innerBind.checkbox.isEnabled = false
                defaultSelectedFoodPrice += option.food_sub_item_price.toDouble()
                PreferenceUtils.selectedFoodItemPrice = defaultSelectedFoodPrice
            }*/

            innerBind.checkbox.setOnClickListener {
                onSelectItem(option, option.food_sub_item_id, data.required_type)
            }
        }.layoutManager(
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        )
        binding.rvNonRequire.adapter = adapter
    }

    override fun onClick(v: View?) {
    }
}