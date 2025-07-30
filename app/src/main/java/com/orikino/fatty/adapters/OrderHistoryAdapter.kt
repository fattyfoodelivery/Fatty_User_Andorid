package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemOrderHistoriesBinding
import com.orikino.fatty.domain.responses.MyOrderHistoryResponse
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.OrderHistoryViewHolder

class OrderHistoryAdapter(context: Context, var callback : (MyOrderHistoryResponse.Data.Data, str : String, pos : Int) -> Unit) : BaseAdapter<OrderHistoryViewHolder, MyOrderHistoryResponse.Data.Data>(context){
    private lateinit var binding: ItemOrderHistoriesBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MyOrderHistoryResponse.Data.Data> {

        binding = ItemOrderHistoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderHistoryViewHolder(binding,callback)
    }
}