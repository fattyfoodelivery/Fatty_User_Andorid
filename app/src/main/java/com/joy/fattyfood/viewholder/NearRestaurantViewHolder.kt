package com.joy.fattyfood

import android.annotation.SuppressLint
import android.view.View
import com.joy.fattyfood.databinding.ItemNearbyRestaurantsBinding
import com.joy.fattyfood.domain.model.NearByRestaurantVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.toDefaultRestaurantCategoryName
import com.joy.fattyfood.utils.helper.toDefaultRestaurantName
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.squareup.picasso.Picasso

class NearRestaurantViewHolder(var binding : ItemNearbyRestaurantsBinding, var callback : (NearByRestaurantVO,String,Int) -> Unit) : BaseViewHolder<NearByRestaurantVO>(binding.root)  {

    @SuppressLint("SetTextI18n")
    override fun setData(data: NearByRestaurantVO, position: Int) {
        binding.tvRestaurantName.text = data.toDefaultRestaurantName()
        binding.tvRestaurantAddress.text = data.restaurant_address
        if (data.order_count.isEmpty()) {
            binding.llOrderCountView.gone()
        } else {
            binding.llOrderCountView.show()
            binding.tvOrderCount.text = data.order_count
        }
        binding.tvDurationDistance.text = "${data.distance_time}mins ・ ${data.distance}km"
        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
            .error(R.drawable.restaurant_default_img)
            .placeholder(R.drawable.restaurant_default_img)
            .into(binding.imvRestaurant)
        binding.tvRestaurantCategoryName.text = data.toDefaultRestaurantCategoryName()

        if (data.restaurant_emergency_status == 1)
            binding.tvUnavailable.show()
        else
            binding.tvUnavailable.gone()

        if (data.is_wish) {
            binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
        } else {
            binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
        }

        binding.root.setOnClickListener {
            callback.invoke(data,"root",position)
        }

        binding.imvFav.setOnClickListener {
            callback.invoke(data,"fav",position)
        }
    }

    override fun onClick(v: View?) {

    }
}