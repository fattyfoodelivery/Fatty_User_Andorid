package com.joy.fattyfood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ViewItemRestaurantsBinding
import com.joy.fattyfood.domain.model.RecommendRestaurantVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.squareup.picasso.Picasso

class TopRelatedCategoryAdapter(
    private var context: Context,
    val callback: (RecommendRestaurantVO, String, Int) -> Unit
) : BaseAdapter<TopRelatedCategoryAdapter.TopRelatedViewHolder, RecommendRestaurantVO>(
    context
) {
    lateinit var binding: ViewItemRestaurantsBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RecommendRestaurantVO> {
        return TopRelatedViewHolder(
            ViewItemRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class TopRelatedViewHolder(
        private val binding: ViewItemRestaurantsBinding,
    ) : BaseViewHolder<RecommendRestaurantVO>(
        binding.root
    ) {

        @SuppressLint("SetTextI18n")
        override fun setData(data: RecommendRestaurantVO, position: Int) {
            if (data.is_wish) {
                binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            } else {
                binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            }

            if (data.rating == 0.0) {
                binding.imvRation.gone()
                binding.tvRatingCount.text =
                    ContextCompat.getString(binding.root.context, R.string.no_review)
            } else {
                binding.imvRation.show()
                binding.tvRatingCount.text = data.rating.toString()
            }
            if (data.orders_count == 0) {
                binding.llOrderCountView.gone()
            } else {
                binding.llOrderCountView.show()
                binding.tvOrderCount.text = data.orders_count.toString()
            }
            binding.tvAddress.text = data.address
            binding.tvRatingCount.text = data.rating.toString()
            binding.tvRestaurantName.text = data.restaurant_name
            binding.tvRestaurantCategoryName.text = data.restaurant_category_name
            binding.tvDurationDistance.text = "${data.distance_time}mins ・ ${data.distance}km"

            Picasso.get()
                .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
                .error(R.drawable.restaurant_default_img)
                .placeholder(R.drawable.restaurant_default_img)
                .into(binding.imvRestaurant)
            binding.root.setOnClickListener {
                callback.invoke(data, "root", position)
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
        }

        override fun onClick(v: View?) = Unit
    }
}
