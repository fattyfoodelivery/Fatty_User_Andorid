package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemRecommendedRestaurantsBinding
import com.joy.fattyfood.domain.model.RecommendRestaurantVO
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.joy.fattyfood.viewholder.RecommendedRestaurantsViewHolder

class RecommendedRestaurantAdapter(context: Context, private var callback : (RecommendRestaurantVO,String,Int) -> Unit) : BaseAdapter<RecommendedRestaurantsViewHolder, RecommendRestaurantVO>(context){
    private lateinit var binding: ItemRecommendedRestaurantsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RecommendRestaurantVO> {

        binding = ItemRecommendedRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendedRestaurantsViewHolder(binding,callback)
    }
}