package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.NearRestaurantViewHolder
import com.joy.fattyfood.databinding.ItemNearbyRestaurantsBinding
import com.joy.fattyfood.domain.model.NearByRestaurantVO
import com.joy.fattyfood.viewholder.BaseViewHolder

class NearByRestaurantAdapter(context: Context, private var callback: (NearByRestaurantVO,String,Int) -> Unit) : BaseAdapter<NearRestaurantViewHolder, NearByRestaurantVO>(context){
    private lateinit var binding: ItemNearbyRestaurantsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<NearByRestaurantVO> {

        binding = ItemNearbyRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NearRestaurantViewHolder(binding,callback)
    }
}