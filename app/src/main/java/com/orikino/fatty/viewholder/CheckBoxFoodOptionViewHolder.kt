package com.orikino.fatty.viewholder

import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.databinding.ItemCheckboxFoodOptionBinding
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toThousandSeparator

class CheckBoxFoodOptionViewHolder(
    private val binding : ItemCheckboxFoodOptionBinding,
    recyclerItemSelectedListener: RecyclerItemSelectedListener
) : BaseSelectableViewHolder<OptionVO>(binding.root, recyclerItemSelectedListener){
    override fun setData(mData: OptionVO, isSelected: Boolean, isEnable: Boolean) {
        binding.root.isChecked = isSelected
        binding.rbOption.isChecked = isSelected
        binding.tvOption.text = mData.toDefaultOptionName()
        binding.tvPrice.text = "${mData.food_sub_item_price.toDouble().toThousandSeparator()} "//${PreferenceUtils.readCurrencyId()?.currency_symbol}"
    }
}