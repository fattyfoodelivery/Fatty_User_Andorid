package com.orikino.fatty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orikino.fatty.databinding.LayoutItemNonRequiredRecyclerBinding
import com.orikino.fatty.databinding.LayoutItemRequireRecyclerBinding
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.NonRequireAddFoodViewHolder
import com.orikino.fatty.viewholder.RequireAddFoodViewHolder

class FoodAddOnListAdapter(val context: Context,val onSelectItem : (data : OptionVO, foodSubItemId : Int, isRequire : Int)-> Unit) :BaseAdapter<RecyclerView.ViewHolder, FoodSubItemVO>(context) {

    val VIEW_TYPE_REQUIRE = 1
    val VIEW_TYPE_NON_REQUIRE = 2


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<FoodSubItemVO> {

        if (viewType == VIEW_TYPE_REQUIRE) {
            return RequireAddFoodViewHolder(
                LayoutItemRequireRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false),
                onSelectItem
            )
        } else {
            return NonRequireAddFoodViewHolder(
                LayoutItemNonRequiredRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false),
                onSelectItem
            )
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun optionSelect(data : OptionVO, foodSubItemId: Int, requireType : Int){
        mData.map { foodSubItem ->
            if(foodSubItem.food_sub_item_id == foodSubItemId && requireType == 1){
                foodSubItem.option.map {
                    if (data.food_sub_item_data_id == it.food_sub_item_data_id) {
                        it.isSelected= it.isSelected.not()
                        //Toast.makeText(context,"${data.food_sub_item_price}",Toast.LENGTH_SHORT).show()
                    }
                    else
                        it.isSelected = false
                    it
                    //Toast.makeText(context,"${data.food_sub_item_data_id} - ${it.food_sub_item_data_id}",Toast.LENGTH_SHORT).show()
                }
            }else {
                foodSubItem.option.map {
                    if (data.food_sub_item_data_id == it.food_sub_item_data_id) {
                        it.isSelected=it.isSelected.not()
                    }
                    it
                    //Toast.makeText(context,"${data.food_sub_item_data_id} - ${it.food_sub_item_data_id}",Toast.LENGTH_SHORT).show()
                }
                // Toast.makeText(context,"${foodSubItem.food_sub_item_id} - ${foodSubItemId}",Toast.LENGTH_SHORT).show()
            }

        }
        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int {
        when {
            mData[position].required_type == 1 -> {
                return VIEW_TYPE_REQUIRE
            }
            else -> {
                return VIEW_TYPE_NON_REQUIRE
            }
        }
    }

}