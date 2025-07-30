/*
package com.solinx_tech.fattyfood.viewholder

import android.view.View
import com.solinx_tech.fattyfood.databinding.ItemAddFoodBinding
import com.solinx_tech.fattyfood.delegate.ItemIdDelegate
import com.solinx_tech.fattyfood.model.constants.model.RestaurantVO

class RestFoodViewHolder(var binding : ItemAddFoodBinding, var delegate: ItemIdDelegate) : BaseViewHolder<RestaurantVO>(binding.root)  {

    lateinit var item : RestaurantVO

    override fun setData(data: RestaurantVO, position: Int) {

        item = data

        binding.tvRestaurantName.text = data.name



    }

    override fun onClick(v: View?) {

        delegate.onTapItemID(item.id)
    }
}*/
