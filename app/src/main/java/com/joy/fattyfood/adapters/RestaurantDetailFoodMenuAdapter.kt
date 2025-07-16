package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.joy.fattyfood.databinding.ItemRvFoodMenuBinding
import com.joy.fattyfood.domain.model.FoodVO
import com.joy.fattyfood.domain.model.MenuVO
import com.joy.fattyfood.viewholder.BaseViewHolder

class RestaurantDetailFoodMenuAdapter(
    val context: Context,
    val callback : (FoodVO,String,Int) -> Unit
) : BaseAdapter<RestaurantDetailFoodMenuAdapter.RestaurantDetailFoodMenuViewHolder,MenuVO>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MenuVO> {
        return RestaurantDetailFoodMenuViewHolder(ItemRvFoodMenuBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class RestaurantDetailFoodMenuViewHolder(val binding : ItemRvFoodMenuBinding) : BaseViewHolder<MenuVO>(binding.root) {

        override fun setData(data: MenuVO, position: Int) {

            val restaurantDetailActiveFoodAdapter = RestaurantDetailActiveFoodAdapter(binding.root.context) { data,str,pos ->

                callback.invoke(data,"add",position)

            }
            restaurantDetailActiveFoodAdapter.setNewData(data.food)
            binding.rvActiveFood.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = restaurantDetailActiveFoodAdapter
            }


        }



        override fun onClick(v: View?) {

        }

    }

}