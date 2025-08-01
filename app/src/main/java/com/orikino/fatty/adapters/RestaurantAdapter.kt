package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemRestaurantViewBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultRestaurantCategoryName
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.orikino.fatty.viewholder.BaseViewHolder

class RestaurantAdapter(private val context: Context,val callback: (RecommendRestaurantVO,String,Int) -> Unit) : BaseAdapter<RestaurantAdapter.RestaurantViewHolder,RecommendRestaurantVO>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RecommendRestaurantVO> {
        return RestaurantViewHolder(ItemRestaurantViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class RestaurantViewHolder(val binding : ItemRestaurantViewBinding) : BaseViewHolder<RecommendRestaurantVO>(binding.root) {
        override fun setData(data: RecommendRestaurantVO, position: Int) {

            if (data.is_wish) binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            else binding.imvFav.setImageResource(R.drawable.ic_favorite_white)

            binding.tvRestaurantName.text = data.toDefaultRestaurantName()
            binding.tvRestaurantAddress.text = data.restaurant_address
            binding.tvEstimateTime.text = "${data.distance_time}mins ・ ${data.distance}km"

            if (data.restaurant_emergency_status == 0) {
                binding.tvUnavailable.show()
            } else {
                binding.tvUnavailable.gone()
            }
            if (data.rating == 0.0) {
                binding.imvRateCount.gone()
                binding.tvRatingCount.text = context.resources.getString(R.string.no_review)
            } else {
                binding.imvRateCount.show()
                binding.tvRatingCount.text = data.rating.toString()
            }
            binding.tvFoodType.text = data.toDefaultRestaurantCategoryName()
            binding.imvRestaurant.load(
                PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image)
            ) {
                error(R.drawable.restaurant_default_img)
                placeholder(R.drawable.restaurant_default_img)
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

}