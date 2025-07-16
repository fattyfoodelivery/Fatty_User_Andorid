package com.joy.fattyfood.viewholder

import android.view.View
import coil.load
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemTopFoodCategoryBinding
import com.joy.fattyfood.domain.model.CategoryVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.toDefaultCategoryName

class TopFoodCategoryViewHolder(var binding : ItemTopFoodCategoryBinding,var callback : (CategoryVO) -> Unit) : BaseViewHolder<CategoryVO>(binding.root)  {

    override fun setData(data: CategoryVO, position: Int) {
        binding.tvFoodName.text = data.toDefaultCategoryName()
        binding.foodImage.load(PreferenceUtils.IMAGE_URL+"/category/"+data.restaurant_category_image) {
            error(R.drawable.food_default_icon)
            placeholder(R.drawable.food_default_icon)
        }

        binding.root.setOnClickListener {
            callback.invoke(data)
        }
    }

    override fun onClick(v: View?) {

    }
}