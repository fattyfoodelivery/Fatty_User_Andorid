package com.orikino.fatty.viewholder

import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemServicesBinding
import com.orikino.fatty.domain.responses.ServiceItem
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils
import com.squareup.picasso.Picasso

class ServicesViewHolder(
    val binding: ItemServicesBinding,
    var callback: (ServiceItem) -> Unit
) : NewBaseViewHolder<ServiceItem>(binding.root) {
    override fun setData(
        mData: ServiceItem,
        currentPage: Int
    ) {
        binding.tvFirstServiceTitle.text = mData.name
        binding.tvFirstServiceDesc.text = mData.sub_title
        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/store-service/service_type/").plus(mData.image))
            .error(R.drawable.ic_service_loading)
            .placeholder(R.drawable.ic_service_loading)
            .into(binding.ivFirstService)
        binding.root.setOnClickListener {
            callback(mData)
        }
    }
}