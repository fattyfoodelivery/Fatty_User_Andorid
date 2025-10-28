package com.orikino.fatty.viewholder

import android.annotation.SuppressLint
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.databinding.ItemRadioFoodOptionBinding
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toThousandSeparator

class RadioFoodOptionViewHolder (
    private val binding : ItemRadioFoodOptionBinding,
    recyclerItemSelectedListener: RecyclerItemSelectedListener
) : BaseSelectableViewHolder<OptionVO>(binding.root, recyclerItemSelectedListener){
    @SuppressLint("SetTextI18n")
    override fun setData(mData: OptionVO, isSelected: Boolean, isEnable: Boolean) {
        binding.root.isChecked = isSelected
        binding.rbOption.isChecked = isSelected
        binding.tvOption.text = mData.toDefaultOptionName()
        if (mData.food_sub_item_price.toDouble() != 0.0){
            binding.tvPrice.show()
            binding.tvPrice.text = "${mData.food_sub_item_price.toDouble().toThousandSeparator()} "  //${PreferenceUtils.readCurrencyId()?.currency_symbol}
        }else{
            binding.tvPrice.gone()
        }
    }
}