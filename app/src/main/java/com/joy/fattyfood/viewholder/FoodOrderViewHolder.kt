package com.joy.fattyfood.viewholder

import android.annotation.SuppressLint
import android.view.View
import com.joy.fattyfood.databinding.ItemFoodOrderCheckoutBinding
import com.joy.fattyfood.domain.model.CreateFoodVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.toThousandSeparator

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
        itemQty = data.food_qty

        _bind.tvItemName.text = data.food_name

        _bind.tvItemPrice.text =
            "${
                data.food_price.toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
        _bind.tvItemQty.text = "$itemQty"
        //callback.onTap(data,"",position)
        _bind.tvAddMore.setOnClickListener {
            callback.invoke(data,"add_more",position)
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