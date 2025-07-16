package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemTopFoodCategoryBinding
import com.joy.fattyfood.domain.model.CategoryVO
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.joy.fattyfood.viewholder.TopFoodCategoryViewHolder

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