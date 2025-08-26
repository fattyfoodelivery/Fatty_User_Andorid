package com.orikino.fatty.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemOrderHistoriesBinding
import com.orikino.fatty.domain.responses.MyOrderHistoryResponse
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toThousandSeparator
import com.squareup.picasso.Picasso

@SuppressLint("SetTextI18n")
class OrderHistoryViewHolder(
    var binding: ItemOrderHistoriesBinding,
    var callback: (MyOrderHistoryResponse.Data.Data, str: String, pos: Int) -> Unit
) : BaseViewHolder<MyOrderHistoryResponse.Data.Data>(binding.root) {

    override fun setData(data: MyOrderHistoryResponse.Data.Data, position: Int) {


        binding.tvOrderTime.text = data.order_time
        binding.tvOrderId.text =
            ContextCompat.getString(binding.root.context, R.string.order_id).plus(data.customer_order_id)

        binding.tvItemQty.text =
            "${data.item_count}".plus(if (data.item_count == 1) " Item" else " Items")



        if (data.order_type == "food") {
            binding.tvRestName.visibility = View.VISIBLE
            binding.tvRestName.text = data.restaurant_name
            binding.tvFromAddress.visibility = View.GONE
            binding.tvToAddress.visibility = View.GONE
            binding.tvPrice.visibility = View.VISIBLE
            binding.tvPrice.text =
                "${data.total?.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"
            if (data.rider_rating == "") {
                binding.llReview.gone()
            } else {
                binding.llReview.show()
                binding.tvRiderReviewCount.text = data.rider_rating
            }
            if (data.restaurant_rating == "") {
                binding.llReview.gone()
            } else {
                binding.llReview.show()
                binding.tvRestaurantRatingCount.text = data.restaurant_rating
            }
            binding.tvFrom.gone()
            binding.tvTo.gone()
            binding.imvRiderAccept.gone()
            binding.imvRestaurant.show()
            binding.llOrderBg.setBackgroundResource(R.drawable.bg_orderid_pink)
            binding.imvOrderTypeIcon.setImageResource(R.drawable.ic_food_id_24dp)
            Picasso.get()
                .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
                .placeholder(R.drawable.restaurant_default_img)
                .error(R.drawable.restaurant_default_img)
                .into(binding.imvRestaurant)

            binding.root.setOnClickListener {
                callback.invoke(data, "track_food_finish", position)
            }

            when (data.order_status_id) {
                1 -> {
                    binding.tvOrderActionsStatus.text =
                        ContextCompat.getString(binding.root.context, R.string.str_cancel)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_gray)
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                2 -> {
                    binding.tvOrderActionsStatus.text =
                        ContextCompat.getString(binding.root.context, R.string.str_cancel)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_gray)
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                6 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderActionsStatus.text =
                        ContextCompat.getString(binding.root.context, R.string.str_track)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                7 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderActionsStatus.text =
                        ContextCompat.getString(binding.root.context, R.string.str_track)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                8 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderActionsStatus.text =
                        ContextCompat.getString(binding.root.context, R.string.str_track)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                9 -> {
                    binding.tvOrderActionsStatus.gone()
                    binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_status_error_20dp)
                    binding.tvOrderStatusMsg.text = data.order_status
                    binding.tvOrderStatusMsg.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.order_status_error
                        )
                    )
                }

                18 -> {
                    binding.tvOrderActionsStatus.gone()
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                19 -> {
                    binding.tvOrderActionsStatus.text =
                        ContextCompat.getString(binding.root.context, R.string.str_cancel)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_gray)
                    binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
                    binding.tvOrderStatusMsg.setTextColor(Color.parseColor("#FF00B11E"))
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                else -> {
                    binding.tvOrderActionsStatus.gone()
                    binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
                    binding.tvOrderStatusMsg.setTextColor(Color.parseColor("#FF00B11E"))
                    binding.tvOrderStatusMsg.text = data.order_status
                }

            }
            binding.tvOrderActionsStatus.setOnClickListener {
                callback.invoke(data, "cancel", position)
            }
        } else {
            var actions: String = ""
            binding.tvFrom.show()
            binding.tvTo.show()
            binding.imvRiderAccept.show()
            binding.imvRestaurant.gone()
            if (data.rider_rating == "") {
                binding.llReview.gone()
            } else {
                binding.llReview.show()
                binding.tvRiderReviewCount.text = data.rider_rating
            }
            binding.tvFromAddress.visibility = View.VISIBLE
            binding.tvFromAddress.text = data.from_block
            binding.tvRestName.visibility = View.GONE
            binding.tvToAddress.visibility = View.VISIBLE
            binding.tvToAddress.text = data.to_block
            binding.tvPrice.visibility = View.GONE
            binding.llOrderBg.setBackgroundResource(R.drawable.bg_orderid_yellow)
            binding.imvOrderTypeIcon.setImageResource(R.drawable.ic_order_status_box)
            Picasso.get()
                .load(R.drawable.order_his_parcel)
                .into(binding.imvRiderAccept)

            binding.root.setOnClickListener {
                callback.invoke(data, "track_parcel_finish", position)
            }



            when (data.order_status_id) {
                11 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                12 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderActionsStatus.text =
                        binding.root.context.getString(R.string.str_track)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
                    binding.tvOrderStatusMsg.text = data.order_status
                    binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_processing_20dp)
                    binding.tvOrderStatusMsg.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.order_processing_color
                        )
                    )
                    actions = "track"
                }

                13 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                14 -> {
                    binding.tvOrderActionsStatus.show()
                    binding.tvOrderActionsStatus.text =
                        binding.root.context.getString(R.string.str_track)
                    binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
                    binding.tvOrderStatusMsg.text = data.order_status
                    binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_processing_20dp)
                    binding.tvOrderStatusMsg.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.order_processing_color
                        )
                    )
                    actions = "track"
                }

                15 -> {
                    binding.tvOrderActionsStatus.gone()
                    binding.tvOrderStatusMsg.text = data.order_status
                    binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
                    binding.tvOrderStatusMsg.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.success200
                        )
                    )
                }

                16 -> {
                    binding.tvOrderStatusMsg.text = data.order_status
                }

                17 -> {
                    binding.tvOrderStatusMsg.text = data.order_status
                }
            }

            binding.tvOrderActionsStatus.setOnClickListener {
                callback.invoke(data, actions, position)
            }
        }

        /* 1 -> {
             binding.imvRiderAccept.gone()
             binding.imvRestaurant.show()
             Picasso.get()
                 .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant?.image))
                 .placeholder(R.drawable.restaurant_default_img)
                 .error(R.drawable.restaurant_default_img)
                 .into(binding.imvRestaurant)
             binding.tvOrderActionsStatus.text =
                 ContextCompat.getString(binding.root.context, R.string.str_cancel)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_gray)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         2 -> {
             //
             binding.imvRiderAccept.gone()
             binding.imvRestaurant.show()
             Picasso.get()
                 .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant?.image))
                 .placeholder(R.drawable.restaurant_default_img)
                 .error(R.drawable.restaurant_default_img)
                 .into(binding.imvRestaurant)
             binding.tvOrderActionsStatus.show()
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         3 -> {
             binding.imvRiderAccept.gone()
             binding.imvRestaurant.show()
             Picasso.get()
                 .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant?.image))
                 .placeholder(R.drawable.restaurant_default_img)
                 .error(R.drawable.restaurant_default_img)
                 .into(binding.imvRestaurant)
             binding.tvOrderActionsStatus.show()
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         4 -> {
             binding.imvRiderAccept.gone()
             binding.imvRestaurant.show()
             Picasso.get()
                 .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant?.image))
                 .placeholder(R.drawable.restaurant_default_img)
                 .error(R.drawable.restaurant_default_img)
                 .into(binding.imvRestaurant)
             binding.tvOrderActionsStatus.show()
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         5 -> {
             binding.imvRiderAccept.gone()
             binding.imvRestaurant.show()
             Picasso.get()
                 .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant?.image))
                 .placeholder(R.drawable.restaurant_default_img)
                 .error(R.drawable.restaurant_default_img)
                 .into(binding.imvRestaurant)
             binding.tvOrderActionsStatus.show()
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         6 -> {
             binding.imvRiderAccept.show()
             binding.imvRestaurant.gone()
             binding.tvOrderActionsStatus.show()
             binding.llOrderBg.setBackgroundResource(R.drawable.bg_orderid_yellow)
             binding.imvOrderTypeIcon.setImageResource(R.drawable.ic_order_status_box)
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()

             if (data.rider == null) {

             } else {

             }
         }
         7 -> {
             binding.tvOrderActionsStatus.show()
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         8,9 -> {
             binding.tvOrderActionsStatus.show()
             binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
             binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
             binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
         }
         10 -> {
             *//*rider arrive to rest*//*
                binding.tvOrderActionsStatus.show()
                binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_gray)
                binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
            }
            11,12,13,14,15,16,17 -> {
                binding.tvOrderActionsStatus.show()
                binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
                binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_track)
                binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
            }
            18 -> {
                binding.tvOrderActionsStatus.gone()
                binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_status_error_20dp)
            }
            19 -> {
                binding.tvOrderActionsStatus.gone()
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
                binding.tvOrderStatusMsg.setTextColor(Color.parseColor("#FF00B11E"))
                binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
            }

            else -> {
                binding.tvOrderActionsStatus.show()
                binding.tvOrderActionsStatus.text = ContextCompat.getString(binding.root.context, R.string.str_track)
                binding.tvOrderActionsStatus.setBackgroundResource(R.drawable.negative_corner_review)
                binding.tvOrderStatusMsg.text = data.order_status.toDefaultStatusName()
                //binding.llReview.gone()
            }*/


        /*binding.root.setOnClickListener {
            callback.invoke(data, "root", position)
        }

        binding.lbtnReview.setOnClickListener {
            callback.invoke(data,"view_rating",position)
        }*/


    }

    override fun onClick(v: View?) {

    }
}