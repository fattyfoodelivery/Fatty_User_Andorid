package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemSystemNotificationBinding
import com.orikino.fatty.domain.responses.SystemNotificationVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.viewholder.BaseViewHolder

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
            binding.ivNotification.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(data.image))
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