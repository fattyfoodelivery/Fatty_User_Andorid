package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemRecommendedRestaurantsBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.RecommendedRestaurantsViewHolder

class RecommendedRestaurantAdapter(context: Context, private var callback : (RecommendRestaurantVO,String,Int) -> Unit) : BaseAdapter<RecommendedRestaurantsViewHolder, RecommendRestaurantVO>(context){
    private lateinit var binding: ItemRecommendedRestaurantsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RecommendRestaurantVO> {

        binding = ItemRecommendedRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendedRestaurantsViewHolder(binding,callback)
    }
}