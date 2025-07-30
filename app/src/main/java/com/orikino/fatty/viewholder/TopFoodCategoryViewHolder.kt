package com.orikino.fatty.viewholder

import android.view.View
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemTopFoodCategoryBinding
import com.orikino.fatty.domain.model.CategoryVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.toDefaultCategoryName

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