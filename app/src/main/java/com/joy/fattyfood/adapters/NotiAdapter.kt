package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemNotificationsBinding
import com.joy.fattyfood.domain.responses.UserNotificationVO
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.joy.fattyfood.viewholder.NotiViewHolder

class NotiAdapter(context: Context, private var callback: (UserNotificationVO,String,Int) -> Unit ): BaseAdapter<NotiViewHolder, UserNotificationVO>(context){
    private lateinit var binding: ItemNotificationsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserNotificationVO> {

        binding = ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding,callback)
    }
}