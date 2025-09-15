package com.orikino.fatty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.ViewItemRestaurantsBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.model.WishListVO
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.viewholder.WishViewHolder

class WishListAdapter(
    context: Context,
    val callback: (RecommendRestaurantVO, String, Int) -> Unit
) : BaseListAdapter<WishListVO, NewBaseViewHolder<WishListVO>>(context, object : DiffUtil.ItemCallback<WishListVO>(){
    override fun areItemsTheSame(oldItem: WishListVO, newItem: WishListVO): Boolean {
        return oldItem.restaurant?.restaurant_id == newItem.restaurant?.restaurant_id
    }
    override fun areContentsTheSame(oldItem: WishListVO, newItem: WishListVO): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewBaseViewHolder<WishListVO> {
        return WishViewHolder(
            ViewItemRestaurantsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            callback
        )
    }
}
