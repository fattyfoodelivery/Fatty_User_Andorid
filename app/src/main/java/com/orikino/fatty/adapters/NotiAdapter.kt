package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.orikino.fatty.databinding.ItemNotificationsBinding
import com.orikino.fatty.domain.responses.UserNotificationVO
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.NotiViewHolder

class NotiAdapter(context: Context, private var callback: (UserNotificationVO,String,Int) -> Unit ): BaseListAdapter<UserNotificationVO,NewBaseViewHolder<UserNotificationVO>>(context, object : DiffUtil.ItemCallback<UserNotificationVO>() {
    override fun areItemsTheSame(
        oldItem: UserNotificationVO,
        newItem: UserNotificationVO
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: UserNotificationVO,
        newItem: UserNotificationVO
    ): Boolean {
        return oldItem == newItem
    }

}){
    private lateinit var binding: ItemNotificationsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBaseViewHolder<UserNotificationVO> {

        binding = ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding,callback)
    }
}