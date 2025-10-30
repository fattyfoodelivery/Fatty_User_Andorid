package com.orikino.fatty.viewholder

import android.annotation.SuppressLint
import android.view.View
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemFoodOrderCheckoutBinding
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toThousandSeparator

class FoodOrderViewHolder(
    var _bind : ItemFoodOrderCheckoutBinding,
    var callback: (CreateFoodVO,str : String,pos : Int) -> Unit,
) : BaseViewHolder<CreateFoodVO>(_bind.root)  {

    var itemQty = 0

    @SuppressLint("SetTextI18n")
    override fun setData(data: CreateFoodVO, position: Int) {

        if (position == -1) {
            _bind.tvAddMore.show()
        } else {
            _bind.tvAddMore.gone()
        }
        _bind.imvFoodItem.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(data.food_image)) {
            error(R.drawable.ic_error_food)
            placeholder(R.drawable.ic_error_food)
        }
        itemQty = data.food_qty

        _bind.tvItemName.text = data.food_name

        _bind.tvItemPrice.text =
            "${
                data.food_price.toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
        _bind.tvItemQty.text = "$itemQty"
        if (data.food_note.isNullOrEmpty()){
            _bind.tvNote.gone()
            _bind.tvEdit.gone()
        }else{
            _bind.tvNote.show()
            _bind.tvEdit.show()
        }
        _bind.tvNote.text = data.food_note
        //callback.onTap(data,"",position)
        _bind.tvAddMore.setOnClickListener {
            callback.invoke(data,"add_more",position)
        }

        _bind.tvEdit.setOnClickListener {
            callback.invoke(data,"edit",position)
        }

        _bind.cvMinus.setOnClickListener {
            _bind.tvItemQty.text = "$itemQty"
            callback.invoke(data,"minus",position)
        }
        _bind.cvPlus.setOnClickListener {
            _bind.tvItemQty.text = "$itemQty"
            callback.invoke(data,"plus",position)
        }

    }

    override fun onClick(v: View?) {
    }
}