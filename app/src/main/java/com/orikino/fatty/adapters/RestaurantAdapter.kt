package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemRestaurantViewBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultCategoryName
import com.orikino.fatty.utils.helper.toDefaultRestaurantCategoryName
import com.orikino.fatty.utils.helper.toDefaultRestaurantName

class RestaurantAdapter(private val context: Context,val callback: (RecommendRestaurantVO,String,Int) -> Unit) : BaseListAdapter<RecommendRestaurantVO, NewBaseViewHolder<RecommendRestaurantVO>>(context, object : DiffUtil.ItemCallback<RecommendRestaurantVO>(){
    override fun areItemsTheSame(
        oldItem: RecommendRestaurantVO,
        newItem: RecommendRestaurantVO
    ): Boolean {
        return oldItem.restaurant_id == newItem.restaurant_id
    }

    override fun areContentsTheSame(
        oldItem: RecommendRestaurantVO,
        newItem: RecommendRestaurantVO
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBaseViewHolder<RecommendRestaurantVO> {
        return RestaurantViewHolder(ItemRestaurantViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class RestaurantViewHolder(val binding : ItemRestaurantViewBinding) : NewBaseViewHolder<RecommendRestaurantVO>(binding.root) {
        override fun setData(mData: RecommendRestaurantVO, currentPage: Int) {

            if (mData.is_wish) binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            else binding.imvFav.setImageResource(R.drawable.ic_favorite_white)

            binding.tvRestaurantName.text = mData.toDefaultRestaurantName()
            binding.tvRestaurantAddress.text = mData.restaurant_address
            binding.tvEstimateTime.text = "${mData.distance_time}mins ・ ${mData.distance}km"

            if (mData.restaurant_emergency_status == 1) {
                binding.tvUnavailable.show()
            } else {
                binding.tvUnavailable.gone()
            }
//            if (mData.rating == 0.0) {
//                binding.imvRateCount.gone()
//                binding.tvRatingCount.text = context.resources.getString(R.string.no_review)
//            } else {
//                binding.imvRateCount.show()
//                binding.tvRatingCount.text = mData.rating.toString()
//            }
            binding.tvFoodType.text = mData.category.toDefaultCategoryName()
            binding.imvRestaurant.load(
                PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(mData.restaurant_image)
            ) {
                error(R.drawable.restaurant_default_img)
                placeholder(R.drawable.restaurant_default_img)
            }

            binding.root.setOnClickListener {
                callback.invoke(mData,"root",currentPage)
            }

            binding.imvFav.setOnClickListener {
                callback.invoke(mData,"fav",currentPage)
            }
        }

    }

}