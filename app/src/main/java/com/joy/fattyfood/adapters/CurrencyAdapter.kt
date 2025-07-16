package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemCurrencyViewBinding
import com.joy.fattyfood.domain.model.CurrencyVO
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.squareup.picasso.Picasso

class CurrencyAdapter(private val context: Context, val callback : (CurrencyVO, String, Int) -> Unit) : BaseAdapter<CurrencyAdapter.CurrencyViewHolder,CurrencyVO>(context){

    lateinit var binding: ItemCurrencyViewBinding

    var defaultHome = false
    var lastPos = 0


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<CurrencyVO> {
        return CurrencyViewHolder(ItemCurrencyViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    inner class CurrencyViewHolder(val binding : ItemCurrencyViewBinding) : BaseViewHolder<CurrencyVO>(binding.root) {


        override fun setData(data: CurrencyVO, position: Int) {


            if (defaultHome) {
                binding.imgFlag.gone()
            } else {
                binding.imgFlag.show()
                Picasso.get()
                    .load(data.image)
                    .into(binding.imgFlag)

            }

            binding.tvName.text = data.currency_name
            binding.tvCurrencyUnit.text = data.currency_symbol

            lastPos = position

            /*if (data.) {
                binding.llContextView.setBackgroundResource(R.drawable.lang_selected_bg)
                binding.imgCheck.setImageResource(R.drawable.radio_check)
            } else {
                binding.llContextView.setBackgroundResource(R.drawable.bg_unselectd)
                binding.imgCheck.setImageResource(R.drawable.uncheck_circle_18dp)
            }
            binding.llContextView.setOnClickListener {
                callback.invoke(data, "pos0", lastPos)
                if (data.isCheck) {
                    data.isCheck = false
                    binding.llContextView.setBackgroundResource(R.drawable.bg_unselectd)
                    binding.imgCheck.setImageResource(R.drawable.uncheck_circle_18dp)
                } else {
                    data.isCheck = true
                    binding.llContextView.setBackgroundResource(R.drawable.lang_selected_bg)
                    binding.imgCheck.setImageResource(R.drawable.radio_check)
                }
            }*/


        }

        override fun onClick(v: View?) {
        }

    }
}