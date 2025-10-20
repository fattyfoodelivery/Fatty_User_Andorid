package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.databinding.ItemRvFoodMenuBinding
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.MenuVO
import com.orikino.fatty.utils.helper.toDefaultMenuName
import com.orikino.fatty.viewholder.BaseViewHolder

class RestaurantDetailFoodMenuAdapter(
    val context: Context,
    val callback : (FoodVO,String,Int) -> Unit
) : BaseAdapter<RestaurantDetailFoodMenuAdapter.RestaurantDetailFoodMenuViewHolder,MenuVO>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MenuVO> {
        return RestaurantDetailFoodMenuViewHolder(ItemRvFoodMenuBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class RestaurantDetailFoodMenuViewHolder(val binding : ItemRvFoodMenuBinding) : BaseViewHolder<MenuVO>(binding.root) {

        override fun setData(data: MenuVO, position: Int) {
            binding.tvActiveMenu.text = data.toDefaultMenuName()
            val restaurantDetailActiveFoodAdapter = RestaurantDetailActiveFoodAdapter(binding.root.context) { data,str,pos ->
                callback.invoke(data,str,position)
            }
            restaurantDetailActiveFoodAdapter.setMenuName(menu = data)
            restaurantDetailActiveFoodAdapter.setNewData(data.food)
            binding.rvActiveFood.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = restaurantDetailActiveFoodAdapter
            }
            binding.ivShowHide.setOnClickListener {
                data.isShow = !data.isShow
                notifyItemChanged(bindingAdapterPosition)
            }
            if (data.isShow) {
                binding.rvActiveFood.visibility = View.VISIBLE
                binding.ivShowHide.rotation = 180f
            } else {
                binding.rvActiveFood.visibility = View.GONE
                binding.ivShowHide.rotation = 0f
            }

        }



        override fun onClick(v: View?) {

        }

    }

}