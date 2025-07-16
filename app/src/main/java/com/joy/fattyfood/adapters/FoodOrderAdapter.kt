package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemFoodOrderCheckoutBinding
import com.joy.fattyfood.domain.model.CreateFoodVO
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.joy.fattyfood.viewholder.FoodOrderViewHolder

class FoodOrderAdapter(
    private val context: Context,
    var callback: (CreateFoodVO,str : String,pos : Int) -> Unit
) : BaseAdapter<FoodOrderViewHolder, CreateFoodVO>(context){
    private lateinit var itemFoodOrderCheckoutBinding : ItemFoodOrderCheckoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CreateFoodVO> {

        itemFoodOrderCheckoutBinding = ItemFoodOrderCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodOrderViewHolder(itemFoodOrderCheckoutBinding,callback)
    }
}