package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemSystemNotificationBinding
import com.orikino.fatty.domain.responses.SystemNotificationVO
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils

class SysNotiAdapter(context: Context,var callback: (SystemNotificationVO, String, Int) -> Unit )
    : BaseListAdapter<SystemNotificationVO, NewBaseViewHolder<SystemNotificationVO> >(context, object : DiffUtil.ItemCallback<SystemNotificationVO>(){
    override fun areItemsTheSame(
        oldItem: SystemNotificationVO,
        newItem: SystemNotificationVO
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SystemNotificationVO,
        newItem: SystemNotificationVO
    ): Boolean {
        return oldItem == newItem
    }

}){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBaseViewHolder<SystemNotificationVO> {

        val binding = ItemSystemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SystemNotiViewHolder(binding)
    }
    inner class SystemNotiViewHolder(val binding : ItemSystemNotificationBinding)
        : NewBaseViewHolder<SystemNotificationVO>(binding.root) {
        override fun setData(mData: SystemNotificationVO, currentPage: Int) {

            binding.tvSystemBody.text = mData.title
            binding.tvDate.text = mData.time
            if (mData.image.isNotEmpty()){
                binding.ivNotification.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(mData.image)) {
                    error(R.drawable.fatty_tran_logo)
                }            }
            if (mData.read) {
                binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,
                    R.color.surfaceRead))
            } else {
                binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,
                    R.color.surfaceUnread))
            }

            binding.cvNotiStatus.setOnClickListener {
                callback.invoke(mData,"view_read",currentPage)
            }
        }

    }
}