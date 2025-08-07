package com.orikino.fatty.adapters

import android.content.Context
import android.view.ViewGroup
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.databinding.ItemCheckboxFoodOptionBinding
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.ui.views.base.BaseMultiSelectableRecyclerAdapter
import com.orikino.fatty.viewholder.CheckBoxFoodOptionViewHolder

class CheckBoxFoodOptionItemAdapter(
    context: Context,
    private val recyclerItemSelectedListener: BaseSelectableViewHolder.RecyclerItemSelectedListener
) : BaseMultiSelectableRecyclerAdapter<OptionVO, BaseSelectableViewHolder<OptionVO>>(context){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseSelectableViewHolder<OptionVO> {
        return CheckBoxFoodOptionViewHolder(
            ItemCheckboxFoodOptionBinding.inflate(
                inflater,
                parent,
                false
            ), recyclerItemSelectedListener
        )
    }
}