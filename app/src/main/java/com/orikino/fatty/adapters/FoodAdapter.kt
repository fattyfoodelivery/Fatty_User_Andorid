package com.orikino.fatty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemAddFoodBinding
import com.orikino.fatty.domain.model.SearchFoodsVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultCategoryName
import com.orikino.fatty.utils.helper.toThousandSeparator
import com.orikino.fatty.viewholder.BaseViewHolder

class FoodAdapter(private val context: Context,val callback : (SearchFoodsVO,String,Int) -> Unit) : BaseAdapter<FoodAdapter.FoodViewHolder,SearchFoodsVO>(context) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SearchFoodsVO> {
        return FoodViewHolder(ItemAddFoodBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class FoodViewHolder(val binding : ItemAddFoodBinding) : BaseViewHolder<SearchFoodsVO>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun setData(data: SearchFoodsVO, position: Int) {
            binding.tvPrice.text = "${
                data.food_price.toDouble().toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            binding.tvFoodName.text = data.food_name_en
            binding.tvFoodCategory.text = data.restaurant.category.toDefaultCategoryName()

            if (data.restaurant.restaurant_emergency_status == 0) {
                binding.tvUnavailable.gone()
            } else {
                binding.tvUnavailable.show()
            }

            if (data.food_emergency_status == 1) {
                binding.tvFoodStatus.gone()

            } else {
                binding.tvFoodStatus.gone()
                binding.tvFoodStatus.text = context.resources.getString(R.string.restaurant_unavailable)
            }
            binding.imvFood.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(data.food_image))
            binding.imvAddFood.setOnClickListener {
                callback.invoke(data,"add_food",position)
            }

        }

        override fun onClick(v: View?) {

        }
    }


}