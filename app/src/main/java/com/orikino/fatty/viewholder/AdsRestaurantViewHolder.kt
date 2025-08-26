package com.orikino.fatty.viewholder

import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemAdsRestaurantBinding
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.squareup.picasso.Picasso

class AdsRestaurantViewHolder(
    val binding: ItemAdsRestaurantBinding,
    val onClick: (NearByRestaurantVO, String, Int) -> Unit
) : NewBaseViewHolder<NearByRestaurantVO>(binding.root){
    override fun setData(
        mData: NearByRestaurantVO,
        currentPage: Int
    ) {
        Picasso.get()
            .load(mData.image)
            .error(R.drawable.restaurant_default_img)
            .placeholder(R.drawable.restaurant_default_img)
            .into(binding.ivSlide)
        binding.root.setOnClickListener {
            onClick.invoke(mData, "ads", 0)
        }
    }
}