package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemTopFoodCategoryBinding
import com.orikino.fatty.domain.model.CategoryVO
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.TopFoodCategoryViewHolder

class TopFoodCategoryAdapter(context: Context, private var callbacks: (CategoryVO) -> Unit) : BaseAdapter<TopFoodCategoryViewHolder, CategoryVO>(context){
    private lateinit var binding: ItemTopFoodCategoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CategoryVO> {

        binding = ItemTopFoodCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopFoodCategoryViewHolder(binding,callbacks)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}