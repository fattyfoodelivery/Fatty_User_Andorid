package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemNotificationsBinding
import com.orikino.fatty.domain.responses.UserNotificationVO
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.NotiViewHolder

class NotiAdapter(context: Context, private var callback: (UserNotificationVO,String,Int) -> Unit ): BaseAdapter<NotiViewHolder, UserNotificationVO>(context){
    private lateinit var binding: ItemNotificationsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserNotificationVO> {

        binding = ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding,callback)
    }
}