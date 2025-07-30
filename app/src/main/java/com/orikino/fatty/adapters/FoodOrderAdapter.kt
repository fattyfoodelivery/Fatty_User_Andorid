package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemFoodOrderCheckoutBinding
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.FoodOrderViewHolder

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