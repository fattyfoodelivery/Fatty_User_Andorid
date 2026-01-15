package com.orikino.fatty.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.ItemServicesBinding
import com.orikino.fatty.domain.responses.ServiceItem
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.viewholder.ServicesViewHolder

class ServicesAdapter(
    val context: Context,
    var callback: (ServiceItem) -> Unit
) : BaseListAdapter<ServiceItem, NewBaseViewHolder<ServiceItem>> (
    context,
    object : DiffUtil.ItemCallback<ServiceItem>() {
        override fun areItemsTheSame(oldItem: ServiceItem, newItem: ServiceItem): Boolean {
            return oldItem.service_item_id == newItem.service_item_id
        }

        override fun areContentsTheSame(oldItem: ServiceItem, newItem: ServiceItem): Boolean {
            return oldItem == newItem // Make sure NearByRestaurantVO is a data class or has a proper equals()
        }

    }) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewBaseViewHolder<ServiceItem> {
        return ServicesViewHolder(
            ItemServicesBinding.inflate(
                android.view.LayoutInflater.from(parent.context),
                parent,
                false
            ),
            callback
        )
    }
}