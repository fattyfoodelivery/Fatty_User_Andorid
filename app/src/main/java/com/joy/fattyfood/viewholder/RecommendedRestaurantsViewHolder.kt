package com.joy.fattyfood.viewholder

import android.view.View
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemRecommendedRestaurantsBinding
import com.joy.fattyfood.domain.model.RecommendRestaurantVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.squareup.picasso.Picasso

class RecommendedRestaurantsViewHolder(
    var binding : ItemRecommendedRestaurantsBinding,
    var callback: (RecommendRestaurantVO,String,Int) -> Unit
) : BaseViewHolder<RecommendRestaurantVO>(binding.root)  {

    override fun setData(data: RecommendRestaurantVO, position: Int) {

        binding.tvDurationDistance.text = "${data.distance_time}mins ・ ${data.distance}km"

        binding.tvRestaurantName.text = data.toDefaultRestaurantName()
        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
            .error(R.drawable.restaurant_default_img)
            .placeholder(R.drawable.restaurant_default_img)
            .into(binding.imvRestaurantPicture)

        if (data.restaurant_emergency_status == 1) {
            binding.tvUnavailable.show()
        } else {
            binding.tvUnavailable.gone()
        }

        binding.rootContentView.setOnClickListener {
            callback.invoke(data,"root_content",position)
        }

    }

    override fun onClick(v: View?) = Unit

    fun RecommendRestaurantVO.toDefaultRestaurantName(): String? {
        return when (PreferenceUtils.readLanguage()) {
            "en" -> {
                this.restaurant_name_en ?: this.restaurant_name_mm ?: "Hello"
            }
            "zh" -> {
                this.restaurant_name_ch ?: this.restaurant_name_mm ?: "Hello"
            }
            else -> {
                this.restaurant_name_mm ?: "Hello"
            }
        }
    }
}