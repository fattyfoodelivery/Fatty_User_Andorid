package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.ItemAdsRestaurantBinding
import com.orikino.fatty.databinding.ItemNearbyRestaurantsBinding
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.helper.ViewType
import com.orikino.fatty.viewholder.AdsRestaurantViewHolder
import com.orikino.fatty.viewholder.NearByIdRestViewHolder

class NearByIdRestAdapter(
    val context: Context,
    var callback: (NearByRestaurantVO, String, Int) -> Unit
) : BaseListAdapter<NearByRestaurantVO, NewBaseViewHolder<NearByRestaurantVO>>(context, object : DiffUtil.ItemCallback<NearByRestaurantVO>() {
    override fun areItemsTheSame(oldItem: NearByRestaurantVO, newItem: NearByRestaurantVO): Boolean {
        return oldItem.restaurant_id == newItem.restaurant_id
    }

    override fun areContentsTheSame(oldItem: NearByRestaurantVO, newItem: NearByRestaurantVO): Boolean {
        return oldItem == newItem // Make sure NearByRestaurantVO is a data class or has a proper equals()
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBaseViewHolder<NearByRestaurantVO> {
        return when (viewType) {
            ViewType.Restaurant.ordinal -> {
                NearByIdRestViewHolder(
                    ItemNearbyRestaurantsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    callback
                )
            }
            else -> { // Assuming ViewType.Ads.ordinal
                AdsRestaurantViewHolder(
                    ItemAdsRestaurantBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    // If AdsRestaurantViewHolder needs a callback or data, adjust accordingly
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        val lsType = item.listing_type
        return if (lsType == 2) ViewType.Ads.ordinal else ViewType.Restaurant.ordinal
    }
}
