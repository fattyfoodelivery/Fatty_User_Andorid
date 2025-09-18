package com.orikino.fatty.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemNotificationsBinding
import com.orikino.fatty.domain.responses.UserNotificationVO
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show

class NotiViewHolder(var binding : ItemNotificationsBinding,var callback: (UserNotificationVO,String,Int) -> Unit) : BaseViewHolder<UserNotificationVO>(binding.root)  {

    override fun setData(data: UserNotificationVO, position: Int) {

        binding.tvDate.text = data.time
        binding.llContentView.setOnClickListener {
            if (data.order_type == "parcel") {
                callback.invoke(data,"parcel",position)
            } else if (data.order_type == "food"){
                callback.invoke(data,"food",position)
            }

        }

        if (data.read) {

            binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,
                R.color.surfaceRead))
        } else {
            binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(this.itemView.context,R.color.surfaceUnread))
        }


        if (data.order_type == "parcel") {
            binding.tvOrderName.text = data.title
            binding.tvOrderStauts.text = data.body
            binding.imvFood.gone()
            binding.imvParcel.show()
            binding.imvParcel.setImageResource(R.drawable.dummy_parcel_item)
            when(data.order_status_id) {
                "11" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.success200))
                }
            }

        } else {
            binding.tvOrderName.text = data.title
            binding.imvParcel.gone()
            binding.imvFood.show()
            binding.imvFood.setImageResource(R.drawable.food_default_icon)
            binding.tvOrderStauts.text = data.body
            when (data.order_status_id) {
                "0" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.textError))
                }
                "1" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.success200))
                }
                "2" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.textError))
                }
                "9" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.textError))
                }
                "16" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.textError))
                }
                "19" -> {
                    binding.tvOrderStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.success200))
                }
                else -> {}
            }
        }


       /* if (data.price == "") {
            //show message
            binding.llOrder.visibility = View.GONE
            binding.tvNotiMsg.visibility = View.VISIBLE
            binding.tvNotiMsg.text = data.appVersionMsg
        } else {
            binding.llOrder.visibility = View.VISIBLE
            binding.tvNotiMsg.visibility = View.GONE

            binding.tvStauts.text = data.appVersionMsg
            binding.tvName.text = data.name

            // show order
        }
        if (data.status) {
            binding.tvStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.success200))
            binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(this.itemView.context,R.color.surfaceRead))
        } else {
            binding.tvStauts.setTextColor(ContextCompat.getColor(this.itemView.context,R.color.textError))
            binding.cvNotiStatus.setCardBackgroundColor(ContextCompat.getColor(this.itemView.context,R.color.surfaceUnread))
        }*/
    }

    override fun onClick(v: View?) {
    }
}