package com.orikino.fatty.viewholder

import com.orikino.fatty.databinding.ItemAdsRestaurantBinding
import com.orikino.fatty.domain.responses.ShopData
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.helper.loadPhoto

class AdsShopViewHolder(
    val binding: ItemAdsRestaurantBinding,
    val onClick: (ShopData, String) -> Unit
) : NewBaseViewHolder<ShopData>(binding.root){
    override fun setData(
        mData: ShopData,
        currentPage: Int
    ) {
        binding.ivSlide.loadPhoto(mData.image)
        binding.root.setOnClickListener {
            onClick.invoke(mData, "ads")
        }
    }
}