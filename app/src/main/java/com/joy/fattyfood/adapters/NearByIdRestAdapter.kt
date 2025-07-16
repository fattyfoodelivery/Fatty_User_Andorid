package com.joy.fattyfood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemAdsRestaurantBinding
import com.joy.fattyfood.databinding.ItemNearbyRestaurantsBinding
import com.joy.fattyfood.domain.model.NearByRestaurantVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.ViewType
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.squareup.picasso.Picasso

class NearByIdRestAdapter(
    val context: Context,
    val dataList: MutableList<NearByRestaurantVO>,
    var callback: (NearByRestaurantVO, String, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateData(newData: List<NearByRestaurantVO>, isFullRefresh: Boolean = true) {
        if (isFullRefresh) {
            dataList.clear()
            dataList.addAll(newData)
            notifyDataSetChanged()
        } else {
            val positionStart = dataList.size
            dataList.addAll(newData)
            notifyItemRangeInserted(positionStart, newData.size)
        }
    }

    fun addMoreData(newData: List<NearByRestaurantVO>) {
        updateData(newData, false)
    }



    /*fun updateData(mDataList: MutableList<NearByRestaurantVO>) {
        dataList.clear()
        dataList.addAll(mDataList)
        notifyDataSetChanged()
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.Restaurant.ordinal -> {
                NearRestViewHolder(
                    ItemNearbyRestaurantsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                AdsRestaurantViewHolder(
                    ItemAdsRestaurantBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]

        when (getItemViewType(position)) {
            ViewType.Ads.ordinal -> {
                (holder as AdsRestaurantViewHolder).bindAds(data)
            }
            ViewType.Restaurant.ordinal -> {
                (holder as NearRestViewHolder).bindRest(data)
            }
            else -> (holder as NearRestViewHolder).bindRest(data)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        val lsType = dataList[position].listing_type
        return if (lsType == 2) ViewType.Ads.ordinal else ViewType.Restaurant.ordinal
    }

    inner class NearRestViewHolder(val binding: ItemNearbyRestaurantsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindRest(data: NearByRestaurantVO) {
            if (data.is_wish) {
                binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            } else {
                binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            }
            if (data.rating.equals(0.0)) {
                binding.imvRating.gone()
                binding.tvRatingCount.text = ContextCompat.getString(binding.root.context,R.string.no_review)
            } else {
                binding.imvRating.show()
                binding.tvRatingCount.text = data.rating.toString()
            }

            Picasso.get()
                .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
                .error(R.drawable.restaurant_default_img)
                .placeholder(R.drawable.restaurant_default_img)
                .into(binding.imvRestaurant)

            binding.tvRestaurantName.text = data.restaurant_name
            binding.tvRestaurantAddress.text = data.restaurant_address
            binding.tvRestaurantCategoryName.text =
                data.restaurant_category_name
            binding.tvDurationDistance.text = "${data.distance_time}mins ・ ${data.distance}km"

            // Ma Nwe Eain
            /*if deliFee <= 0.0 && defineAmt > 0.0 {
                if additional <= 0 {
                    show - deli free above defineAmont
                } else {
                    show - not show anything
                }
            }     else if deliFee <= 0 {
                show deli-Free
            } else {
                show - not show anything
            }*/
            /*if (data.delivery_fee <= 0.0 && data.define_amount > 0.0) {
                if (data.additional_delivery_fee <= 0) {
                    binding.imvMoto.visibility = View.VISIBLE
                    //nearResBinding.tvDeliveryFee.visibility = View.VISIBLE
                    //nearResBinding.tvDeliveryFee.text = when (Preference.readLanguage()) {
             */
            /*"my" -> {
                            "ပို့ဆောင်ခ အခမဲ့ (${
                                data.define_amount.toThousandSeparator()
                            } ${Preference.readCurrencyId().currency_symbol} အထက်)"
                        }
                        "en" -> {
                            "Deli Free (above ${
                                data.define_amount.toThousandSeparator()
                            } ${Preference.readCurrencyId().currency_symbol})"
                        }
                        else -> {
                            "免费派送（${
                                data.define_amount.toThousandSeparator()
                            } ${Preference.readCurrencyId().currency_symbol}起)"
                        }*/
            /*
                    }
                } else {
                    // not show
                    binding.imvMoto.visibility = View.INVISIBLE
                    //binding.tv.visibility = View.INVISIBLE
                }

            }  else if (data.delivery_fee <= 0.0) {
                binding.imvMoto.visibility = View.VISIBLE
                nearResBinding.tvDeliveryFee.visibility = View.VISIBLE
            } else {
                // not show
                nearResBinding.ivMoto.visibility = View.INVISIBLE
                nearResBinding.tvDeliveryFee.visibility = View.INVISIBLE
            }*/

            // origin code
            /*if (data.delivery_fee == 0.0 && data.define_amount > 0.0) {
                nearResBinding.tvDeliveryFee.text = when (Preference.readLanguage()) {
                    "my" -> {
                        "ပို့ဆောင်ခ အခမဲ့ (${
                            data.define_amount.toThousandSeparator()
                        } ${Preference.readCurrencyId().currency_symbol} အထက်)"
                    }
                    "en" -> {
                        "Deli Free (above ${
                            data.define_amount.toThousandSeparator()
                        } ${Preference.readCurrencyId().currency_symbol})"
                    }
                    else -> {
                        "免费派送（${
                            data.define_amount.toThousandSeparator()
                        } ${Preference.readCurrencyId().currency_symbol}起)"
                    }
                }
            } else if (data.delivery_fee == 0.0)
                nearResBinding.tvDeliveryFee.text = context.resources.getString(R.string.free_delivery)
            else
                nearResBinding.tvDeliveryFee.text = "${data.delivery_fee.toThousandSeparator()} ${Preference.readCurrencyId().currency_symbol}"*/

            if (data.restaurant_emergency_status == 1) {
                binding.tvUnavailable.visibility = View.VISIBLE
            } else {
                binding.tvUnavailable.visibility = View.GONE
            }

            binding.imvFav.setOnClickListener {
                callback.invoke(data, "fav", 0)

                if (!data.is_wish) {
                    data.is_wish = true
                    binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                } else {
                    data.is_wish = false
                    binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                }
            }

            binding.cvRestaurant.setOnClickListener {
                callback.invoke(data, "cv_rest", 0)
            }
        }
    }

    inner class AdsRestaurantViewHolder(val binding: ItemAdsRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindAds(data: NearByRestaurantVO) {
            Picasso.get()
                .load(data.image)
                .error(R.drawable.restaurant_default_img)
                .placeholder(R.drawable.restaurant_default_img)
                .into(binding.ivSlide)
        }
    }
}
