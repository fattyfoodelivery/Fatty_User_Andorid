package com.orikino.fatty.ui.views.fragments.rest_more_info

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orikino.fatty.R
import com.orikino.fatty.adapters.FoodAddOnListAdapter
import com.orikino.fatty.databinding.FragmentFoodAddOnBottomSheetBinding
import com.orikino.fatty.domain.model.CreateFoodOption
import com.orikino.fatty.domain.model.CreateFoodSubItem
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.domain.view_model.OrderViewModel
import com.orikino.fatty.ui.views.activities.checkout.CheckOutActivity
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.toDefaultFoodName
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class FoodAddOnBottomSheetFragment(
    val onAddCart: () -> Unit,
    val onDeleteItem: (foodList: MutableList<CreateFoodVO>) -> Unit? = {}
) : BottomSheetDialogFragment() {

    private var _binding : FragmentFoodAddOnBottomSheetBinding? = null
    private var optionFoodAdapter : FoodAddOnListAdapter? = null

    private var aa = 0.0
    private var isSelected = false
    lateinit var mAdapter: FoodAddOnListAdapter
    private var subItemList = mutableListOf<FoodSubItemVO>()
    private val viewModel: OrderViewModel by viewModels()
    private var foodVO = FoodVO()
    var foodSubItemList = mutableListOf<CreateFoodSubItem>()

    private var fastOrder = false
    private var restaurantVO = FoodMenuByRestaurantVO()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                if (subItemList.size > 0)  {
                    setupFullHeight(it)
                } else {
                    setupDefaultHeight(it)
                }
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                _binding?.ivClose?.setOnClickListener {
                    behaviour.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodAddOnBottomSheetBinding.inflate(inflater,container,false)
        return (_binding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        subItemList = Gson().fromJson(
            arguments?.getString(ITEM_LIST),
            object : TypeToken<MutableList<FoodSubItemVO>>() {}.type
        )
        setUpFoodAddOnRecyclerView()
        when (subItemList.size) {
            0 -> {
                _binding?.rvFoodAdd?.gone()
            }
            1 -> {
                _binding?.rvFoodAdd?.show()
            }
            else -> {
                //guideline2.setGuidelinePercent(0.7f)
                _binding?.rvFoodAdd?.show()
            }
        }
        foodVO = Gson().fromJson(arguments?.getString(FOOD), object : TypeToken<FoodVO>() {}.type)
        restaurantVO = Gson().fromJson(
            arguments?.getString(RESTAURANT),
            object : TypeToken<FoodMenuByRestaurantVO>() {}.type
        )
        arguments?.getBoolean(FAST_ORDER)?.let { fastOrder = it }
        subscribeUI()
        _binding?.llExtraAdd?.setOnClickListener {
            processAddToCart()
            dialog?.dismiss()
        }

    }


    @SuppressLint("SetTextI18n")
    private fun subscribeUI() {
        viewModel.qty?.postValue(1)

        viewModel.qty?.observe(viewLifecycleOwner) {
            _binding?.tvItemCount?.text = "$it"
            _binding?.tvItemSubTotal?.text = "$it Items"
            if (isSelected) {
                viewModel.price =
                    it.times(
                        foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0
                    )
                viewModel.originalPrice =
                    foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0

                _binding?.tvTotalAmt?.text = "${
                    viewModel.price.toDouble().toThousandSeparator()
                } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            } else {
                viewModel.price =
                    it.times(foodVO.food_price?.toDouble()?.plus(aa) ?: 0.0)
                viewModel.originalPrice =
                    foodVO.food_price?.toDouble()?.plus(aa) ?: 0.0
                _binding?.tvTotalAmt?.text = "${
                    viewModel.price.toDouble().toThousandSeparator()
                } ${PreferenceUtils.readCurrCurrency()?.currency_symbol} "
            }

        }

        _binding?.cvMinus?.setOnClickListener {
            if (viewModel.qty?.value!! > 1) {
                viewModel.qty!!.postValue(viewModel.qty?.value!!.minus(1))
            }
        }

        _binding?.cvPlus?.setOnClickListener {
            viewModel.qty!!.postValue(viewModel.qty?.value!!.plus(1))
        }

    }

    private fun setupDefaultHeight(bottomSheet : View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun setupFullHeight(bottomSheet : View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun processAddToCart() {
        viewModel.foodOrderList.clear()
        show()
        if (PreferenceUtils.readFoodOrderList()?.isNotEmpty() == true) {
            if (restaurantVO.restaurant_id != PreferenceUtils.readFoodOrderList()?.get(0)?.restaurant_id) {
                dialog?.dismiss()
                addToCartOnDiffRestaurant()
                onDeleteItem(viewModel.foodOrderList)

            } else {
                if (fastOrder) {
                    addToCart()
                    /*requireActivity().startActivity<CheckOutActivity>(
                        CheckOutActivity.LAT to PreferenceUtils.readUserVO().latitude,
                        CheckOutActivity.LNG to PreferenceUtils.readUserVO().longitude,
                        CheckOutActivity.ADDRESS_TYPE to ""
                    )*/
                    val intent = Intent(requireContext(), CheckOutActivity::class.java)
                    intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                    intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                    intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                    startActivity(intent)
                } else {
                    addToCart()
                    onAddCart()
                }

            }

        } else {
            if (fastOrder) {
                addToCart()
               /* context?.startActivity<CheckOutActivity>(
                    CheckOutActivity.LAT to PreferenceUtils.readUserVO().latitude,
                    CheckOutActivity.LNG to PreferenceUtils.readUserVO().longitude,
                    CheckOutActivity.ADDRESS_TYPE to ""
                )*/
                val intent = Intent(requireContext(), CheckOutActivity::class.java)
                intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                startActivity(intent)

            } else {
                addToCart()
                onAddCart()
            }
        }
    }


    private fun addToCart() {
        PreferenceUtils.writeRestaurant(restaurantVO)
        PreferenceUtils.writeAddToCart(true)
        viewModel.foodOrderList.add(
            CreateFoodVO(
                local_food_id = (PreferenceUtils.readFoodOrderList()!!.size + 1),
                restaurant_id = restaurantVO.restaurant_id,
                food_id = foodVO.food_id,
                food_name = foodVO.toDefaultFoodName() ?: "",
                initial_price = viewModel.originalPrice,
                food_qty = viewModel.qty?.value!!,
                food_note = _binding?.edtNote?.text.toString().trim(),
                food_price = viewModel.price,
                sub_item = foodSubItemList
            )
        )
        if (PreferenceUtils.readFoodOrderList().isNullOrEmpty()) {
            PreferenceUtils.writeFoodOrderList(viewModel.foodOrderList)
        } else {
            val resultList = PreferenceUtils.readFoodOrderList()
            resultList?.forEach {
                viewModel.foodOrderList.add(it)
            }
            PreferenceUtils.writeFoodOrderList(viewModel.foodOrderList)
        }
        dialog?.dismiss()
    }

    private fun addToCartOnDiffRestaurant() {
        viewModel.foodOrderList.add(
            CreateFoodVO(
                local_food_id = PreferenceUtils.readFoodOrderList()!!.size + 1,
                restaurant_id = restaurantVO.restaurant_id,
                food_id = foodVO.food_id,
                food_name = foodVO.toDefaultFoodName() ?: "",
                initial_price = viewModel.originalPrice,
                food_qty = viewModel.qty?.value!!,
                food_note = _binding?.edtNote?.text.toString().trim(),
                food_price = viewModel.price,
                sub_item = foodSubItemList
            )
        )
    }

    private fun show() {
        val aa = mAdapter.mData
        filterOption(aa).forEach {
            val aa = CreateFoodSubItem(it.required_type, it.food_sub_item_id)
            it.option.forEach {
                val ab = CreateFoodOption(
                    it.food_sub_item_data_id,
                    it.food_sub_item_price.toDouble(),
                    it.toDefaultOptionName()!!
                )
                aa.option.add(ab)
            }
            foodSubItemList.add(aa)
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setUpFoodAddOnRecyclerView() {
        mAdapter = FoodAddOnListAdapter(requireContext()) { option, foodSubItemId, requireType ->
            mAdapter.optionSelect(option, foodSubItemId, requireType)
            if (requireType == 1) {
                if (option.isSelected) {
                    isSelected = true
                    viewModel.subItemTotalPrice = 0.0
                    calculateAddOnPrice()
                    viewModel.originalPrice = foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0

                    viewModel.price = viewModel.qty?.value!!.times(foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0)
                    _binding?.tvTotalAmt?.text = "${
                        viewModel.price.toDouble().toThousandSeparator()
                    } " //${PreferenceUtils.readCurrencyId()?.currency_symbol}

                }

            } else {
                isSelected = true
                /*if (option.isSelected) {
                    calculateAddOnPrice()
                    viewModel.originalPrice =
                        foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0

                    viewModel.price = viewModel.qty?.value!!.times(
                        foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0
                    )
                    _binding?.tvTotalAmt?.text = "${
                        viewModel.price.toThousandSeparator()
                    } " //${PreferenceUtils.readCurrencyId()?.currency_symbol}
                } else {
                    calculateAddOnPrice()
                    viewModel.originalPrice =
                        foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0

                    viewModel.price = viewModel.qty?.value!!.times(
                        foodVO.food_price?.toDouble()?.plus(viewModel.subItemTotalPrice) ?: 0.0
                    )
                    _binding?.tvTotalAmt?.text = "${
                        viewModel.price.toThousandSeparator()
                    } " //${PreferenceUtils.readCurrencyId()?.currency_symbol}
                }*/
            }
            viewModel.updateSubItemTotalPrice.postValue(true)
        }

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        _binding?.rvFoodAdd?.layoutManager = linearLayoutManager
        mAdapter.setNewData(prepareDefaultOptions(subItemList as ArrayList<FoodSubItemVO>))
        _binding?.rvFoodAdd?.adapter = mAdapter
    }

    private fun calculateAddOnPrice() {
        var bb = mAdapter.mData
        var cc = 0.0
        /*var aa = bb.filter {
            it.option.filter {
                it.isSelected
            }.isEmpty().not()

        }.toMutableList()*/

        /*var dd = mutableListOf<OptionVO>()
        aa.map { it ->
            it.option.forEach {
                if (it.isSelected)
                    dd.add(
                        OptionVO(
                            item_name_en = it.item_name_en,
                            food_sub_item_price = it.food_sub_item_price,
                            isSelected = it.isSelected
                        )
                    )
            }
        }.toMutableList()*/

        /*dd.forEach {
            cc += it.food_sub_item_price.toDouble()
        }
*/
        viewModel.subItemTotalPrice = cc
    }

    private fun filterOption(data: MutableList<FoodSubItemVO>): MutableList<FoodSubItemVO> {
        val newData = arrayListOf<FoodSubItemVO>()
        newData.clear()
        newData.addAll(data)
        /*val foodItem =
            newData.filter {
                it.option.filter {
                    it.isSelected
                }.isEmpty().not()

            }*/
        val foodList = arrayListOf<FoodSubItemVO>()
        //foodList.addAll(foodItem)
        return foodList.map {
            val takeOption = arrayListOf<OptionVO>()
            /*it.option.forEach {
                if (it.isSelected) {
                    takeOption.add(it)

                }
            }*/
            it.option = takeOption
            it
        }.toMutableList()

        /*

        return data.map {

            it.option

           *//* val selectOption = it.option.filter {
                it.isSelected
            }
            it.option = selectOption.toMutableList()*//*
            it
        }.toMutableList()
*/
    }



    private fun prepareDefaultOptions(data: ArrayList<FoodSubItemVO>): ArrayList<FoodSubItemVO> {
        return data.map { foodSubItemVO ->
            if (foodSubItemVO.required_type == 1) {
                if (foodSubItemVO.option.isNotEmpty()) {
                    //foodSubItemVO.option[0].isSelected = true
                    aa += foodSubItemVO.option[0].food_sub_item_price.toDouble()
                }
            }
            foodSubItemVO
        }.toMutableList() as ArrayList<FoodSubItemVO>

    }

    companion object {
        @JvmStatic
        fun newInstance(
            isFastOrder: Boolean,
            restVO: FoodMenuByRestaurantVO,
            foodVO: FoodVO,
            subItemList: MutableList<FoodSubItemVO>,
            onAddCart: () -> Unit,
            onDeleteItem: (foodList: MutableList<CreateFoodVO>) -> Unit? = {}
        ): FoodAddOnBottomSheetFragment {
            return FoodAddOnBottomSheetFragment(onAddCart, onDeleteItem).apply {
                arguments = Bundle().apply {
                    putString(ITEM_LIST, Gson().toJson(subItemList))
                    putString(FOOD, Gson().toJson(foodVO))
                    putString(RESTAURANT, Gson().toJson(restVO))
                    putBoolean(FAST_ORDER, isFastOrder)
                }
            }
        }

        const val ITEM_LIST = "item-list"
        const val FOOD = "food"
        const val RESTAURANT = "restaurant"
        const val FAST_ORDER = "fast-order"
    }


}