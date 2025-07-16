package com.joy.fattyfood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemAddFoodBinding
import com.joy.fattyfood.domain.model.SearchFoodsVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.toDefaultCategoryName
import com.joy.fattyfood.utils.helper.toThousandSeparator
import com.joy.fattyfood.viewholder.BaseViewHolder

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