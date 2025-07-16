package com.joy.fattyfood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ViewItemRestaurantsBinding
import com.joy.fattyfood.domain.model.RecommendRestaurantVO
import com.joy.fattyfood.domain.model.WishListVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.toDefaultRestaurantAddress
import com.joy.fattyfood.utils.helper.toDefaultRestaurantCategoryName
import com.joy.fattyfood.utils.helper.toDefaultRestaurantName
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.squareup.picasso.Picasso

class WishListAdapter(
    private val context: Context,
    val callback: (RecommendRestaurantVO, String, Int) -> Unit
) : BaseAdapter<WishListAdapter.WishViewHolder, WishListVO>(context) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<WishListVO> {
        return WishViewHolder(
            ViewItemRestaurantsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class WishViewHolder(val binding: ViewItemRestaurantsBinding) :
        BaseViewHolder<WishListVO>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun setData(data: WishListVO, position: Int) {
            data.restaurant?.let { data ->
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
                    binding.tvUnavailable.text = context.resources.getString(R.string.out_of_service)
                } else {
                    if (data.restaurant_emergency_status == 1) binding.tvUnavailable.show()
                }

                binding.root.setOnClickListener {
                    callback.invoke(data, "root", position)
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
                    callback.invoke(data, "fav", position)
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

        override fun onClick(v: View?) = Unit
    }
}
