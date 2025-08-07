package com.orikino.fatty.ui.views.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.orikino.fatty.R
import com.orikino.fatty.adapters.AddOnListAdapter
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
import com.orikino.fatty.ui.views.base.BaseBottomSheet
import com.orikino.fatty.ui.views.delegate.AddOnDelegate
import com.orikino.fatty.ui.views.delegate.AddOnItemListDelegate
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.toDefaultFoodName
import com.orikino.fatty.utils.helper.toDefaultOptionName
import com.orikino.fatty.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOnBottomSheetFragment : BaseBottomSheet<OrderViewModel>(), AddOnItemListDelegate {
    override fun getTheme(): Int {
        return R.style.FattyBottomSheetStyle
    }

    private lateinit var binding: FragmentFoodAddOnBottomSheetBinding
    private var addOnAdapter: AddOnListAdapter? = null
    private val viewModel: OrderViewModel by viewModels()
    var foodSubItemList = mutableListOf<CreateFoodSubItem>()

    companion object {
        const val TAG = "FilterBottomSheet"
        var delegate: AddOnDelegate? = null
        var isFastOrder = false
        var restVO = FoodMenuByRestaurantVO()
        var foodVO = FoodVO()
        var subItemList = mutableListOf<FoodSubItemVO>()
        fun newInstance(
            isFastOrder: Boolean,
            restVO: FoodMenuByRestaurantVO,
            foodVO: FoodVO,
            subItemList: MutableList<FoodSubItemVO>,
            delegate: AddOnDelegate
        ): AddOnBottomSheetFragment {
            this.delegate = delegate
            this.isFastOrder = isFastOrder
            this.restVO = restVO
            this.foodVO = foodVO
            this.subItemList = subItemList

            return AddOnBottomSheetFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodAddOnBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            val sheet = it as BottomSheetDialog
            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        initUi()
    }

    @SuppressLint("SetTextI18n")
    private fun initUi() {
        // Set the base price of the food item once. This should not include option prices.
        viewModel.originalPrice = foodVO.food_price?.toDouble() ?: 0.0
        viewModel.qty?.postValue(1) // Set initial quantity

        viewModel.qty?.observe(viewLifecycleOwner) { quantity ->
            binding.tvItemCount.text = "$quantity"
            binding.tvItemSubTotal.text = "$quantity Items"
            updatePrice() // Update price when quantity changes
        }

        if (subItemList.isNotEmpty()) {
            binding.rvFoodAdd.visibility = View.VISIBLE
            setUpRecyclerView() // This will call prepareDefaultOptions which modifies foodSubItemList
        } else {
            binding.rvFoodAdd.visibility = View.GONE
        }

        binding.cvMinus.setOnClickListener {
            val currentQty = viewModel.qty?.value ?: 1
            if (currentQty > 1) {
                viewModel.qty?.postValue(currentQty - 1)
            }
        }

        binding.cvPlus.setOnClickListener {
            val currentQty = viewModel.qty?.value ?: 1
            viewModel.qty?.postValue(currentQty + 1)
        }

        binding.llExtraAdd.setOnClickListener {
            processAddToCart()
            dialog?.dismiss()
        }
        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }
        updatePrice() // Call initially to set price based on default options and quantity
    }

    private fun processAddToCart() {
        viewModel.foodOrderList.clear()
        if (PreferenceUtils.readFoodOrderList()?.isNotEmpty() == true) {
            if (restVO.restaurant_id != PreferenceUtils.readFoodOrderList()
                    ?.get(0)?.restaurant_id
            ) {
                dialog?.dismiss()
                addToCartOnDiffRestaurant()
                delegate?.onDeleteItem(viewModel.foodOrderList)
            } else {
                if (isFastOrder) {
                    addToCart()
                    val intent = Intent(requireContext(), CheckOutActivity::class.java)
                    intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                    intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                    intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                    startActivity(intent)
                } else {
                    addToCart()
                    delegate?.onAddToCart()
                }
            }
        } else {
            if (isFastOrder) {
                addToCart()
                val intent = Intent(requireContext(), CheckOutActivity::class.java)
                intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                startActivity(intent)
            } else {
                addToCart()
                delegate?.onAddToCart()
            }
        }
    }

    private fun addToCart() {
        PreferenceUtils.writeRestaurant(restVO)
        PreferenceUtils.writeAddToCart(true)
        viewModel.foodOrderList.add(
            CreateFoodVO(
                local_food_id = (PreferenceUtils.readFoodOrderList()?.size ?: 0) + 1,
                restaurant_id = restVO.restaurant_id,
                food_id = foodVO.food_id,
                food_name = foodVO.toDefaultFoodName() ?: "",
                initial_price = viewModel.originalPrice, // Base price
                food_qty = viewModel.qty?.value ?: 1,
                food_note = binding.edtNote.text.toString().trim(),
                food_price = viewModel.price, // Total price including options
                sub_item = foodSubItemList // Use the updated list
            )
        )
        val currentOrderList = PreferenceUtils.readFoodOrderList()
        if (currentOrderList.isNullOrEmpty()) {
            PreferenceUtils.writeFoodOrderList(viewModel.foodOrderList)
        } else {
            val resultList = currentOrderList.toMutableList() // Make it mutable
            resultList.addAll(viewModel.foodOrderList)
            PreferenceUtils.writeFoodOrderList(resultList)
        }
        dialog?.dismiss()
    }

    private fun addToCartOnDiffRestaurant() {
        viewModel.foodOrderList.add(
            CreateFoodVO(
                local_food_id = (PreferenceUtils.readFoodOrderList()?.size ?: 0) + 1,
                restaurant_id = restVO.restaurant_id,
                food_id = foodVO.food_id,
                food_name = foodVO.toDefaultFoodName() ?: "",
                initial_price = viewModel.originalPrice, // Base price
                food_qty = viewModel.qty?.value ?: 1,
                food_note = binding.edtNote.text.toString().trim(),
                food_price = viewModel.price, // Total price including options
                sub_item = foodSubItemList // Use the updated list
            )
        )
    }

    private fun setUpRecyclerView() {
        addOnAdapter = AddOnListAdapter(requireContext(), this)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFoodAdd.layoutManager = linearLayoutManager
        // Clear foodSubItemList before preparing default options to avoid duplicates if setUpRecyclerView is called multiple times
        foodSubItemList.clear()
        val preparedList = prepareDefaultOptions(subItemList as ArrayList<FoodSubItemVO>)
        addOnAdapter?.submitList(preparedList)
        binding.rvFoodAdd.adapter = addOnAdapter
    }

    @SuppressLint("SetTextI18n")
    override fun onSelectItem(
        data: OptionVO,
        subItem: FoodSubItemVO,
        isRequire: Int // This parameter from AddOnItemListDelegate might not be directly used now for price logic
    ) {
        modifyOptionList(subItem, data)
        updatePrice() // Centralized price update
    }

    private fun prepareDefaultOptions(data: ArrayList<FoodSubItemVO>): ArrayList<FoodSubItemVO> {
        // This function will select default items and also populate foodSubItemList
        // foodSubItemList should be cleared before calling this if there's a chance of re-initialization
        data.forEach { foodSubItemVO ->
            if (foodSubItemVO.required_type == 1 && foodSubItemVO.option.isNotEmpty()) {
                // Ensure default option is actually selected in the adapter
                // foodSubItemVO.option[0].isSelected = true (Adapter should handle UI selection)

                // Add the default selected option to foodSubItemList
                val defaultOptionVO = foodSubItemVO.option[0]
                val subItemEntry = CreateFoodSubItem(
                    foodSubItemVO.required_type,
                    foodSubItemVO.food_sub_item_id
                )
                val optionEntry = CreateFoodOption(
                    defaultOptionVO.food_sub_item_data_id,
                    defaultOptionVO.food_sub_item_price.toDouble(),
                    defaultOptionVO.toDefaultOptionName() ?: ""
                )
                subItemEntry.option.add(optionEntry)

                // Check if this sub_item_id already exists to avoid duplicates
                // (though clearing foodSubItemList before this helps)
                if (foodSubItemList.none { it.food_sub_item_id == subItemEntry.food_sub_item_id }) {
                    foodSubItemList.add(subItemEntry)
                }
            }
        }
        return data // Return original list for adapter to display all options
    }

    private fun modifyOptionList(foodSubVO: FoodSubItemVO, optionVO: OptionVO) {
        val convertedOption = CreateFoodOption(
            optionVO.food_sub_item_data_id,
            optionVO.food_sub_item_price.toDouble(),
            optionVO.toDefaultOptionName() ?: ""
        )

        val existingSubItem = foodSubItemList.find { it.food_sub_item_id == foodSubVO.food_sub_item_id }

        if (foodSubVO.required_type == 1) { // Single selection
            if (existingSubItem != null) {
                existingSubItem.option.clear()
                existingSubItem.option.add(convertedOption)
            } else {
                // This case should ideally be handled by prepareDefaultOptions if it's a required item.
                // However, if it can be added dynamically:
                val newSubItemEntry = CreateFoodSubItem(
                    foodSubVO.required_type,
                    foodSubVO.food_sub_item_id,
                    mutableListOf(convertedOption)
                )
                foodSubItemList.add(newSubItemEntry)
            }
        } else { // Multiple selections
            if (existingSubItem != null) {
                // Check if this specific option (by food_sub_item_data_id) exists
                val optionExists = existingSubItem.option.any { it.food_sub_item_data_id == convertedOption.food_sub_item_data_id }
                if (optionExists) {
                    existingSubItem.option.removeAll { it.food_sub_item_data_id == convertedOption.food_sub_item_data_id }
                } else {
                    existingSubItem.option.add(convertedOption)
                }
                // If, after removal, an optional sub-item group has no options, remove the group itself
                if (existingSubItem.option.isEmpty()) { // No need to check required_type again, it's already in the else block
                    foodSubItemList.remove(existingSubItem)
                }
            } else {
                // Sub-item group does not exist, create it and add the option
                // This means it's the first option selected for this multi-select group
                val newSubItemEntry = CreateFoodSubItem(
                    foodSubVO.required_type,
                    foodSubVO.food_sub_item_id,
                    mutableListOf(convertedOption)
                )
                foodSubItemList.add(newSubItemEntry)
            }
        }
        Log.d("ModifyOptionList", "Updated foodSubItemList: $foodSubItemList")
    }

    @SuppressLint("SetTextI18n")
    private fun updatePrice() {
        var calculatedOptionsTotal = 0.0
        foodSubItemList.forEach { subItem ->
            subItem.option.forEach { option ->
                calculatedOptionsTotal += option.food_sub_item_price
            }
        }
        viewModel.subItemTotalPrice = calculatedOptionsTotal

        val currentQty = viewModel.qty?.value ?: 1
        val basePrice = viewModel.originalPrice // Price of food item without any options

        viewModel.price = (basePrice + viewModel.subItemTotalPrice) * currentQty

        binding.tvTotalAmt.text = "${viewModel.price.toThousandSeparator()} "
    }
}
