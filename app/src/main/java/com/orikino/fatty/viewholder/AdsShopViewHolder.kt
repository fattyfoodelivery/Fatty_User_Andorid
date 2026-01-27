package com.orikino.fatty.viewholder

import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemAdsRestaurantBinding
import com.orikino.fatty.domain.responses.ShopData
import com.orikino.fatty.ui.views.base.NewBaseViewHolder

class AdsShopViewHolder(
    val binding: ItemAdsRestaurantBinding,
    val onClick: (ShopData, String, String) -> Unit
) : NewBaseViewHolder<ShopData>(binding.root){
    override fun setData(
        mData: ShopData,
        currentPage: Int
    ) {
        binding.ivSlide.load(mData.image) {
            error(R.drawable.updown_slide_dummy)
        }
        binding.root.setOnClickListener {
            onClick.invoke(mData, "ads", "")
        }
    }
}