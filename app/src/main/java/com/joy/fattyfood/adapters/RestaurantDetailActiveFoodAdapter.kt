package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ItemAddFoodBinding
import com.joy.fattyfood.domain.model.FoodVO
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.toDefaultFoodName
import com.joy.fattyfood.viewholder.BaseViewHolder

class RestaurantDetailActiveFoodAdapter(
    val context: Context,
    val callback: (FoodVO, String, Int) -> Unit
) : BaseAdapter<RestaurantDetailActiveFoodAdapter.RestaurantDetailActiveFoodViewHolder, FoodVO>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FoodVO> {
        return RestaurantDetailActiveFoodViewHolder(
            ItemAddFoodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

   inner class RestaurantDetailActiveFoodViewHolder(
        val binding : ItemAddFoodBinding
    ) : BaseViewHolder<FoodVO>(binding.root) {

        var item : FoodVO? = null
        override fun setData(data: FoodVO, position: Int) {
            item = data
            binding.tvFoodName.text = data.toDefaultFoodName()
            binding.tvPrice.text = data.food_price?.plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)

            /*if(PreferenceUtils.readCurrencyId()?.currency_id == 1) {
                binding.tvPrice.text = data.food_price?.plus(" MMK")
            } else {
                binding.tvPrice.text = data.food_price?.plus(" Yuan")
            }*/
            binding.imvFood.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(data.food_image)) {
                error(R.drawable.food_default_icon)
                placeholder(R.drawable.food_default_icon)
            }

            binding.imvAddFood.setOnClickListener {
                callback.invoke(data,"add",position)
            }
            binding.root.setOnClickListener {
                callback.invoke(data,"root",position)
            }

            /*binding.imvAddFood.setOnClickListener {
                if (PreferenceUtils.readUserVO()?.customer_id == 0) {
                    SuccessDialog.Builder(
                        this.itemView.context,
                        this.itemView.context.resources.getString(R.string.login_message),
                        callback = {
                            this.itemView.context.startActivity(LoginActivity.getIntent())
                            (this.itemView.context as AppCompatActivity).finish()
                        })
                        .show(frgmgr, this@RestaurantDetailActiveFoodAdapter::class.simpleName)
                } else {
                    when {

                        viewModel.foodMenuByRestaurantLiveDataList.value?.distance!! > viewModel.foodMenuByRestaurantLiveDataList.value?.limit_distance!! -> {
                            showSnackBar(resources.getString(R.string.out_of_service))
                        }

                        viewModel.foodMenuByRestaurantLiveDataList.value?.distance!! <= viewModel.foodMenuByRestaurantLiveDataList.value?.limit_distance!! -> {
                            println("foodemergencystatattata ${viewModel.foodMenuByRestaurantLiveDataList.value?.restaurant_emergency_status}")
                            if (viewModel.foodMenuByRestaurantLiveDataList.value?.restaurant_emergency_status == 1) showSnackBar(
                                resources.getString(R.string.restaurant_unavailable)
                            )
                            else {

                                when (data.food_emergency_status) {
                                    1 -> {
                                        *//*showNoItemDialog(
                                            resources.getString(R.string.notice),
                                            resources.getString(R.string.notice_message),
                                            "OK"
                                        )*//*

                                    }
                                    else -> {
                                        *//*val bottomSheetFragment =
                                            FoodAddOnBottomSheetFragment.newInstance(
                                                false,
                                                restaurantInfO,
                                                data,
                                                data.sub_item,
                                                onAddCart = {
                                                    viewModel.isEmptyCart.postValue(Preference.readAddToCart())
                                                },
                                                onDeleteItem = {
                                                    showGameOverDialog(it)
                                                })
                                        bottomSheetFragment.show(
                                            supportFragmentManager,
                                            bottomSheetFragment.tag
                                        )*//*
                                    }
                                }
                            }

                        }
                    }
                }
            }*/


        }

        override fun onClick(v: View?)  = Unit


    }




}