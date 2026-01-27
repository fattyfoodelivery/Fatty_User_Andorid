package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.ItemAdsRestaurantBinding
import com.orikino.fatty.databinding.ItemServiceShopBinding
import com.orikino.fatty.databinding.LayoutLoadingViewBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.responses.ShopData
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.helper.ViewType
import com.orikino.fatty.viewholder.AdsShopViewHolder
import com.orikino.fatty.viewholder.ServiceShopViewHolder

class ServiceShopsAdapter(
    val context: Context,
    var callback: (ShopData, String, String) -> Unit
) : BaseListAdapter<ShopData, NewBaseViewHolder<ShopData>>(
    context,
    object : DiffUtil.ItemCallback<ShopData>() {
        override fun areItemsTheSame(oldItem: ShopData, newItem: ShopData): Boolean {
            return oldItem.store_id == newItem.store_id
        }

        override fun areContentsTheSame(oldItem: ShopData, newItem: ShopData): Boolean {
            return oldItem == newItem // Make sure NearByRestaurantVO is a data class or has a proper equals()
        }

    }) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewBaseViewHolder<ShopData> {
        return when (viewType) {
            ViewType.Restaurant.ordinal -> {
                ServiceShopViewHolder(
                    ItemServiceShopBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    callback
                )
            }

            ViewType.Loading.ordinal -> {
                LoadingViewHolder(
                    LayoutLoadingViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> { // Assuming ViewType.Ads.ordinal
                AdsShopViewHolder(
                    ItemAdsRestaurantBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    callback
                    // If AdsRestaurantViewHolder needs a callback or data, adjust accordingly
                )
            }
        }
    }

    class LoadingViewHolder(
        private val binding: LayoutLoadingViewBinding
    ) : NewBaseViewHolder<ShopData>(binding.root) {
        override fun setData(
            mData: ShopData,
            currentPage: Int
        ) {
        }

    }

    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        val lsType = item.listing_type
        return if (item.isLoadingView)
            ViewType.Loading.ordinal
        else if (lsType == 2)
            ViewType.Ads.ordinal
        else
            ViewType.Restaurant.ordinal
    }
}