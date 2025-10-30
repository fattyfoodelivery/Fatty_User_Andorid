package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemAddFoodBinding
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.MenuVO
import com.orikino.fatty.utils.ClickGuard
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.toDefaultFoodName
import com.orikino.fatty.utils.helper.toDefaultMenuName
import com.orikino.fatty.viewholder.BaseViewHolder

class RestaurantDetailActiveFoodAdapter(
    val context: Context,
    val callback: (FoodVO, String, Int) -> Unit
) : BaseAdapter<RestaurantDetailActiveFoodAdapter.RestaurantDetailActiveFoodViewHolder, FoodVO>(context) {
    private var menu : MenuVO? = null

    fun setMenuName(menu : MenuVO){
        this.menu = menu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FoodVO> {
        return RestaurantDetailActiveFoodViewHolder(
            ItemAddFoodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),menu!!
        )
    }

   inner class RestaurantDetailActiveFoodViewHolder(
        val binding : ItemAddFoodBinding,
       val menu : MenuVO
    ) : BaseViewHolder<FoodVO>(binding.root) {

        var item : FoodVO? = null
        override fun setData(data: FoodVO, position: Int) {
            item = data
            binding.tvFoodName.text = data.toDefaultFoodName()
            binding.tvPrice.text = data.food_price?.plus(" ").plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)

            /*if(PreferenceUtils.readCurrencyId()?.currency_id == 1) {
                binding.tvPrice.text = data.food_price?.plus(" MMK")
            } else {
                binding.tvPrice.text = data.food_price?.plus(" Yuan")
            }*/
            binding.tvFoodCategory.text = menu.toDefaultMenuName()
            binding.imvFood.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(data.food_image)) {
                error(R.drawable.ic_error_food)
                placeholder(R.drawable.ic_error_food)
            }

            if (data.food_emergency_status == 1){
                binding.imvAddFood.alpha = 0.5f
                binding.tvUnavailable.visibility = View.VISIBLE
            }else{
                binding.imvAddFood.alpha = 1f
                binding.tvUnavailable.visibility = View.GONE
            }

            binding.imvFood.setOnClickListener {
                callback.invoke(data,"image",position)
            }
            binding.root.setOnClickListener {
                callback.invoke(data,"add",position)
            }
            ClickGuard.guard(binding.root)

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