package com.orikino.fatty.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemServiceShopBinding
import com.orikino.fatty.domain.responses.ShopData
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ServiceShopViewHolder(
    val binding: ItemServiceShopBinding,
    var callback: (ShopData, String) -> Unit
) : NewBaseViewHolder<ShopData>(binding.root) {
    override fun setData(
        mData: ShopData,
        currentPage: Int
    ) {
        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/store-service/store/").plus(mData.image))
            .error(R.drawable.ic_shop_loading)
            .placeholder(R.drawable.ic_shop_loading)
            .into(binding.ivShop)
        binding.tvCategory.text = mData.service_category.name
        binding.tvShopName.text = mData.name
        val status = getStoreOpenStatus(binding.root.context, mData.open_time, mData.close_time)
        binding.tvShopStatus.text = status

        binding.tvDistance.text = String.format(Locale.US, "%.2fkm", mData.distance)
        binding.tvAddress.text = mData.address
        binding.root.setOnClickListener {
            callback.invoke(mData, "cv_shop")
        }
    }
}


fun getStoreOpenStatus(
    context: Context,
    openTime: String,
    closeTime: String
): String {
    if (openTime.equals("00:00:00", ignoreCase = true) && closeTime.equals(
            "23:59:00",
            ignoreCase = true
        )
    ) {
        return context.getString(R.string.txt_open_24_hours)
    }

    val parser = SimpleDateFormat("HH:mm:ss", Locale.US)
    try {
        val openDate = parser.parse(openTime)
        val closeDate = parser.parse(closeTime)
        val currentDate = parser.parse(parser.format(Date()))

        val isOpen = if (openDate.after(closeDate)) { // Overnight
            !currentDate.before(openDate) || !currentDate.after(closeDate)
        } else { // Same day
            !currentDate.before(openDate) && !currentDate.after(closeDate)
        }

        return if (isOpen) {
            val displayFormatter = SimpleDateFormat("HH:mm", Locale.US)
            "${displayFormatter.format(openDate)} ~ ${displayFormatter.format(closeDate)}"
        } else {
            context.getString(R.string.txt_closed_now)
        }
    } catch (e: ParseException) {
        // Log the exception for debugging
        return context.getString(R.string.txt_closed_now)
    }
}
