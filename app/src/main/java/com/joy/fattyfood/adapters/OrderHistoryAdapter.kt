package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemOrderHistoriesBinding
import com.joy.fattyfood.domain.responses.MyOrderHistoryResponse
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.joy.fattyfood.viewholder.OrderHistoryViewHolder

class OrderHistoryAdapter(context: Context, var callback : (MyOrderHistoryResponse.Data.Data, str : String, pos : Int) -> Unit) : BaseAdapter<OrderHistoryViewHolder, MyOrderHistoryResponse.Data.Data>(context){
    private lateinit var binding: ItemOrderHistoriesBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MyOrderHistoryResponse.Data.Data> {

        binding = ItemOrderHistoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderHistoryViewHolder(binding,callback)
    }
}