package com.orikino.fatty.adapters

import android.content.Context
import android.view.ViewGroup
import com.orikino.fatty.ui.views.base.BaseSelectableRecyclerAdapter
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.viewholder.RadioFoodOptionViewHolder
import com.orikino.fatty.databinding.ItemRadioFoodOptionBinding
import com.orikino.fatty.domain.model.OptionVO

class RadioFoodOptionsItemAdapter(
    context: Context,
    private val recyclerItemSelectedListener: BaseSelectableViewHolder.RecyclerItemSelectedListener
) : BaseSelectableRecyclerAdapter<OptionVO, BaseSelectableViewHolder<OptionVO>>(context){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseSelectableViewHolder<OptionVO> {
        return RadioFoodOptionViewHolder(ItemRadioFoodOptionBinding.inflate(inflater, parent, false), recyclerItemSelectedListener)
    }
}