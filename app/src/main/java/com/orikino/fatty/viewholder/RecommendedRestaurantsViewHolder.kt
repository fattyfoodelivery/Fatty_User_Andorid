package com.orikino.fatty.viewholder

import android.view.View
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemRecommendedRestaurantsBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
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