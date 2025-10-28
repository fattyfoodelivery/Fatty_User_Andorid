package com.orikino.fatty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.LayoutLoadingViewBinding
import com.orikino.fatty.databinding.ViewItemRestaurantsBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.ui.views.base.BaseListAdapter
import com.orikino.fatty.ui.views.base.NewBaseViewHolder
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.viewholder.BaseViewHolder
import com.squareup.picasso.Picasso

class TopRelatedCategoryAdapter(
    private var context: Context,
    val callback: (RecommendRestaurantVO, String, Int) -> Unit
) : BaseListAdapter<RecommendRestaurantVO, NewBaseViewHolder<RecommendRestaurantVO> >(
    context, object : androidx.recyclerview.widget.DiffUtil.ItemCallback<RecommendRestaurantVO>() {
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

    }
) {
    lateinit var binding: ViewItemRestaurantsBinding

    companion object{
        const val LOADING_VIEW = 1
        const val NORMAL_VIEW = 2
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewBaseViewHolder<RecommendRestaurantVO> {
        return when(viewType){
            LOADING_VIEW -> LoadingViewHolder(
                LayoutLoadingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> TopRelatedViewHolder(
                ViewItemRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isLoadingView) LOADING_VIEW else NORMAL_VIEW
    }

    inner class LoadingViewHolder(
        private val binding : LayoutLoadingViewBinding
    ) : NewBaseViewHolder<RecommendRestaurantVO>(binding.root){
        override fun setData(
            mData: RecommendRestaurantVO,
            currentPage: Int
        ) {
        }

    }

    inner class TopRelatedViewHolder(
        private val binding: ViewItemRestaurantsBinding,
    ) : NewBaseViewHolder<RecommendRestaurantVO>(
        binding.root
    ) {

        @SuppressLint("SetTextI18n")
        override fun setData(data: RecommendRestaurantVO, position: Int) {
            if (data.is_wish) {
                binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            } else {
                binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            }
            binding.tvAddress.text = data.address
            binding.tvRatingCount.text = data.rating.toString()
            binding.tvRestaurantName.text = data.restaurant_name
            binding.tvRestaurantCategoryName.text = data.restaurant_category_name
            binding.tvDurationDistance.text = "${data.distance_time}mins ・ ${data.distance}km"

            Picasso.get()
                .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
                .error(R.drawable.restaurant_default_img)
                .placeholder(R.drawable.restaurant_default_img)
                .into(binding.imvRestaurant)
            binding.root.setOnClickListener {
                callback.invoke(data, "root", position)
            }
            if (data.restaurant_emergency_status == 1) {
                binding.tvUnavailable.visibility = View.VISIBLE
            } else {
                binding.tvUnavailable.visibility = View.GONE
            }
            binding.imvFav.setOnClickListener {
                callback.invoke(data, "fav", position)
                if (data.is_wish) {
                    data.is_wish = false
                    binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                } else {
                    data.is_wish = true
                    binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                }
            }
        }
    }
}
