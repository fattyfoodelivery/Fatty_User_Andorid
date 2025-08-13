package com.orikino.fatty.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemNearbyRestaurantsBinding
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.squareup.picasso.Picasso

class NearByIdRestViewHolder(
    val binding: ItemNearbyRestaurantsBinding,
    var callback: (NearByRestaurantVO, String, Int) -> Unit
) : NewBaseViewHolder<NearByRestaurantVO>(binding.root){
    override fun setData(
        mData: NearByRestaurantVO,
        currentPage: Int
    ) {
        if (mData.is_wish) {
            binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
        } else {
            binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
        }
        if (mData.rating.equals(0.0)) {
            binding.imvRating.gone()
            binding.tvRatingCount.text = ContextCompat.getString(binding.root.context,R.string.no_review)
        } else {
            binding.imvRating.show()
            binding.tvRatingCount.text = mData.rating.toString()
        }

        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(mData.restaurant_image))
            .error(R.drawable.restaurant_default_img)
            .placeholder(R.drawable.restaurant_default_img)
            .into(binding.imvRestaurant)

        binding.tvRestaurantName.text = mData.restaurant_name
        binding.tvRestaurantAddress.text = mData.restaurant_address
        binding.tvRestaurantCategoryName.text =
            mData.restaurant_category_name
        binding.tvDurationDistance.text = "${mData.distance_time}mins ・ ${mData.distance}km"

        if (mData.restaurant_emergency_status == 1) {
            binding.tvUnavailable.visibility = View.VISIBLE
        } else {
            binding.tvUnavailable.visibility = View.GONE
        }

        binding.imvFav.setOnClickListener {
            callback.invoke(mData, "fav", 0)

            if (!mData.is_wish) {
                mData.is_wish = true
                binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            } else {
                mData.is_wish = false
                binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            }
        }

        binding.cvRestaurant.setOnClickListener {
            callback.invoke(mData, "cv_rest", 0)
        }
    }
}