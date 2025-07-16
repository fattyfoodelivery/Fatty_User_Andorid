package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemSystemNotificationBinding
import com.joy.fattyfood.domain.responses.SystemNotificationVO
import com.joy.fattyfood.viewholder.BaseViewHolder

class SysNotiAdapter(context: Context,var callback: (SystemNotificationVO, String, Int) -> Unit )
    : BaseAdapter<SysNotiAdapter.SystemNotiViewHolder, SystemNotificationVO>(context){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SystemNotificationVO> {

        val binding = ItemSystemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SystemNotiViewHolder(binding)
    }
    inner class SystemNotiViewHolder(val binding : ItemSystemNotificationBinding)
        : BaseViewHolder<SystemNotificationVO>(binding.root) {
        override fun setData(data: SystemNotificationVO, position: Int) {

            binding.tvSystemBody.text = data.title
            binding.tvDate.text = data.time

            if (data.read) {
                binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,
                    R.color.surfaceRead))
            } else {
                binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,
                    R.color.surfaceUnread))
            }

            binding.cvNotiStatus.setOnClickListener {
                callback.invoke(data,"view_read",position)
            }
        }

        override fun onClick(v: View?) = Unit

    }
}