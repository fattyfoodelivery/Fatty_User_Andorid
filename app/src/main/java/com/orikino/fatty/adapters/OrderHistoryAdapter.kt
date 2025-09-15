package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.ItemOrderHistoriesBinding
import com.orikino.fatty.domain.responses.MyOrderHistoryResponse
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.OrderHistoryViewHolder

class OrderHistoryAdapter(context: Context, var callback : (MyOrderHistoryResponse.Data.Data, str : String, pos : Int) -> Unit) : BaseListAdapter< MyOrderHistoryResponse.Data.Data, NewBaseViewHolder<MyOrderHistoryResponse.Data.Data>>(context, object : DiffUtil.ItemCallback<MyOrderHistoryResponse.Data.Data>() {
    override fun areItemsTheSame(
        oldItem: MyOrderHistoryResponse.Data.Data,
        newItem: MyOrderHistoryResponse.Data.Data
    ): Boolean {
        return oldItem.order_id == newItem.order_id
    }

    override fun areContentsTheSame(
        oldItem: MyOrderHistoryResponse.Data.Data,
        newItem: MyOrderHistoryResponse.Data.Data
    ): Boolean {
        return oldItem == newItem
    }

}){
    private lateinit var binding: ItemOrderHistoriesBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBaseViewHolder<MyOrderHistoryResponse.Data.Data> {

        binding = ItemOrderHistoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderHistoryViewHolder(binding,callback)
    }
}