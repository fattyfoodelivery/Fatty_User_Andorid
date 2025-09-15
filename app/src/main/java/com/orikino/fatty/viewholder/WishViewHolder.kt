package com.orikino.fatty.viewholder

import android.view.View
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ViewItemRestaurantsBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.model.WishListVO
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultRestaurantAddress
import com.orikino.fatty.utils.helper.toDefaultRestaurantCategoryName
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.squareup.picasso.Picasso

class WishViewHolder(
    val binding: ViewItemRestaurantsBinding,
    val callback: (RecommendRestaurantVO, String, Int) -> Unit
) : NewBaseViewHolder<WishListVO>(binding.root) {
    override fun setData(mData: WishListVO, currentPage: Int) {
        mData.restaurant?.let { data ->
            binding.tvDurationDistance.text = "${data.distance_time}mins ・ ${data.distance}km"
            binding.tvRestaurantName.text = data.toDefaultRestaurantName()
            binding.tvRestaurantCategoryName.text = data.toDefaultRestaurantCategoryName()
            binding.tvAddress.text = data.toDefaultRestaurantAddress()
            Picasso.get()
                .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
                .error(R.drawable.restaurant_default_img)
                .placeholder(R.drawable.restaurant_default_img)
                .into(binding.imvRestaurant)

            if (data.distance > data.limit_distance) {
                binding.tvUnavailable.show()
                binding.tvUnavailable.text = binding.root.context.resources.getString(R.string.out_of_service)
            } else {
                if (data.restaurant_emergency_status == 1) binding.tvUnavailable.show()
            }

            binding.root.setOnClickListener {
                callback.invoke(data, "root", currentPage)
            }
            if (data.is_wish) {
                binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            } else {
                binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            }

            if (data.orders_count == 0) {
                binding.llOrderCountView.gone()
            } else {
                binding.llOrderCountView.show()
                binding.tvOrderCount.text = data.orders_count.toString()
            }

            binding.imvFav.setOnClickListener {
                callback.invoke(data, "fav", currentPage)
                if (data.is_wish) {
                    data.is_wish = false
                    binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                } else {
                    data.is_wish = true
                    binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                }
            }
            if (data.rating == 0.0) {
                binding.llReview.gone()
            } else {
                binding.llReview.show()
                binding.tvRatingCount.text = data.rating.toString()
            }
        }
    }
}