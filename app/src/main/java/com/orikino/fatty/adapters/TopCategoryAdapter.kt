package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemTopFoodCategoryBinding
import com.orikino.fatty.databinding.ItemViewMoreCategoryBinding
import com.orikino.fatty.domain.model.*
import com.orikino.fatty.ui.views.activities.category.FoodCategoryActivity
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.toDefaultCategoryName

class TopCategoryAdapter(var dataList : MutableList<CategoryVO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mCtx : Context? = null
    val VIEW_TYPE_ONE = 1
    val VIEW_TYPE_TWO = 2


    fun setData(itemList : MutableList<CategoryVO>) {
        dataList.clear()
        dataList.addAll(itemList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mCtx = parent.context
        return when (viewType) {
            VIEW_TYPE_ONE -> {
                FoodViewHolder(ItemTopFoodCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }

            else -> {
                MoreViewHolder(ItemViewMoreCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]
        when (getItemViewType(position)) {
            VIEW_TYPE_ONE -> {
                (holder as FoodViewHolder).bindView(data)
            }
            VIEW_TYPE_TWO -> {
                (holder as MoreViewHolder).bindView()
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if(position == (dataList.size - 1)) {
            VIEW_TYPE_TWO
        } else {
            VIEW_TYPE_ONE
        }
    }


    inner class FoodViewHolder(var binding : ItemTopFoodCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(item : CategoryVO) {
            binding.tvFoodName.text = item.toDefaultCategoryName()

            binding.foodImage.load(
                PreferenceUtils.IMAGE_URL+"/category/"+item.restaurant_category_image
            ) {
                error(R.drawable.food_default_icon)
                placeholder(R.drawable.food_default_icon)
            }

            /*binding.root.setOnClickListener {
                mCtx?.startActivity(item.toDefaultCategoryName()
                    ?.let { TopRelatedCategoryActivity.getIntent(it) })
            }*/
            PreferenceUtils.needToShow = false
            PreferenceUtils.isBackground = false

        }
    }

    inner class MoreViewHolder(var binding : ItemViewMoreCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            binding.root.setOnClickListener {
                mCtx?.startActivity(FoodCategoryActivity.getIntent("Categories"))
            }
        }
    }
}