package com.orikino.fatty.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.adapters.CheckBoxFoodOptionItemAdapter
import com.orikino.fatty.adapters.RadioFoodOptionsItemAdapter
import com.orikino.fatty.databinding.LayoutItemRequireRecyclerBinding
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.ui.views.base.BaseSelectableViewHolder
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.ui.views.delegate.AddOnItemListDelegate
import com.orikino.fatty.utils.helper.toDefaultSectionName

class AddOnListViewHolder(
    private val binding : LayoutItemRequireRecyclerBinding,
    private val delegate : AddOnItemListDelegate
) : NewBaseViewHolder<FoodSubItemVO>(binding.root) {

    private lateinit var radioOptionAdapter : RadioFoodOptionsItemAdapter
    private lateinit var checkBoxOptionAdapter : CheckBoxFoodOptionItemAdapter

    override fun setData(
        mData: FoodSubItemVO,
        currentPage: Int
    ) {
        binding.tvRequireTitle.text = mData.toDefaultSectionName()
        if (mData.required_type == 1){
            radioOptionAdapter = RadioFoodOptionsItemAdapter(binding.root.context, object : BaseSelectableViewHolder.RecyclerItemSelectedListener{
                override fun onSelected(selectedPosition: Int, view: View?) {
                    radioOptionAdapter.toggleSelectedItem(selectedPosition)
                    delegate.onSelectItem(mData.option[selectedPosition], mData, mData.required_type)
                }
            })
            binding.rvRequired.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = radioOptionAdapter
            }
            radioOptionAdapter.appendNewData(mData.option)
        }else{
            checkBoxOptionAdapter = CheckBoxFoodOptionItemAdapter(binding.root.context, object : BaseSelectableViewHolder.RecyclerItemSelectedListener{
                override fun onSelected(selectedPosition: Int, view: View?) {
                    checkBoxOptionAdapter.toggleSelectedItem(selectedPosition)
                    delegate.onSelectItem(mData.option[selectedPosition], mData, mData.required_type)
                }
            })
            binding.rvRequired.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = checkBoxOptionAdapter
            }
            checkBoxOptionAdapter.appendNewData(mData.option)
        }
    }
}