package com.orikino.fatty.viewholder

import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemAdsRestaurantBinding
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.helper.loadPhoto
import com.squareup.picasso.Picasso

class AdsRestaurantViewHolder(
    val binding: ItemAdsRestaurantBinding,
    val onClick: (NearByRestaurantVO, String, Int) -> Unit
) : NewBaseViewHolder<NearByRestaurantVO>(binding.root){
    override fun setData(
        mData: NearByRestaurantVO,
        currentPage: Int
    ) {
        binding.ivSlide.loadPhoto(mData.image)
        binding.root.setOnClickListener {
            onClick.invoke(mData, "ads", 0)
        }
    }
}