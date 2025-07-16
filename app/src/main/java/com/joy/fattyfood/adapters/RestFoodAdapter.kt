package com.joy.fattyfood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joy.fattyfood.databinding.ItemRestaurantViewBinding
import com.joy.fattyfood.domain.model.SearchFoodsVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.load

class RestFoodAdapter(
    val context: Context,
    private val food: MutableList<SearchFoodsVO>,
    private val callBack: () -> Unit
) : RecyclerView.Adapter<RestFoodAdapter.RestFoodViewHolder>() {

    lateinit var binding: ItemRestaurantViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestFoodViewHolder {
        binding = ItemRestaurantViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestFoodViewHolder(binding, callBack)
    }

    override fun getItemCount(): Int {
        return food.size
    }

    override fun onBindViewHolder(holder: RestFoodViewHolder, position: Int) {
        val food = food[position]
        holder.bindFood(food)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(itemList: MutableList<SearchFoodsVO>) {
        food.clear()
        food.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class RestFoodViewHolder(var binding: ItemRestaurantViewBinding, var callBack: () -> Unit) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bindFood(food: SearchFoodsVO) {
            binding.tvRestaurantName.text = food.food_name_en
            binding.imvRestaurant.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(food.food_image))
            callBack.invoke()
        }
    }
}


