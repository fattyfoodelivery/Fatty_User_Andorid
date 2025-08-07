package com.orikino.fatty.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.LayoutItemRequireRecyclerBinding
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.ui.views.delegate.AddOnItemListDelegate
import com.orikino.fatty.viewholder.AddOnListViewHolder

class AddOnListAdapter(
    context: Context,
    private val delegate: AddOnItemListDelegate
): BaseListAdapter<FoodSubItemVO, NewBaseViewHolder<FoodSubItemVO>>(context, object : DiffUtil.ItemCallback<FoodSubItemVO>() {
    override fun areItemsTheSame(oldItem: FoodSubItemVO, newItem: FoodSubItemVO): Boolean {
        return oldItem.food_sub_item_id == newItem.food_sub_item_id
    }

    override fun areContentsTheSame(oldItem: FoodSubItemVO, newItem: FoodSubItemVO): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewBaseViewHolder<FoodSubItemVO> {
        return AddOnListViewHolder(LayoutItemRequireRecyclerBinding.inflate(inflater, parent, false), delegate = delegate)
    }
}