package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.viewholder.NearRestaurantViewHolder
import com.orikino.fatty.databinding.ItemNearbyRestaurantsBinding
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.viewholder.BaseViewHolder

class NearByRestaurantAdapter(context: Context, private var callback: (NearByRestaurantVO,String,Int) -> Unit) : BaseAdapter<NearRestaurantViewHolder, NearByRestaurantVO>(context){
    private lateinit var binding: ItemNearbyRestaurantsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<NearByRestaurantVO> {

        binding = ItemNearbyRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NearRestaurantViewHolder(binding,callback)
    }
}