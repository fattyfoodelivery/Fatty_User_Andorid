package com.orikino.fatty.ui.views.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.orikino.fatty.utils.CustomToast
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

    @SuppressLint("SetTextI18n", "StringFormatMatches")
    private fun initUi() {
        // Set the base price of the food item once. This should not include option prices.
        viewModel.originalPrice = foodVO.food_price?.toDouble() ?: 0.0
        viewModel.qty?.postValue(1) // Set initial quantity

        viewModel.qty?.observe(viewLifecycleOwner) { quantity ->
            binding.tvItemCount.text = "$quantity"
            binding.tvItemSubTotal.text = getString(R.string.txt_total_items, quantity)
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
            // Dialog dismiss is handled within addToCart or processAddToCart logic
        }
        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }
        updatePrice() // Call initially to set price based on default options and quantity
        setUpRestNote()
    }

    private fun processAddToCart() {
        // viewModel.foodOrderList.clear() // Clearing here might not be needed if addToCart manages the list correctly
        val currentCart = PreferenceUtils.readFoodOrderList()
        if (!currentCart.isNullOrEmpty()) {
            if (restVO.restaurant_id != currentCart[0].restaurant_id) {
                dialog?.dismiss()
                addToCartOnDiffRestaurant() // This prepares viewModel.foodOrderList for the delegate
                delegate?.onDeleteItem(viewModel.foodOrderList) // Delegate handles confirmation and cart clearing
            } else {
                addToCart() // Merges or adds to current restaurant's cart
//                if (isFastOrder) {
//                    val intent = Intent(requireContext(), CheckOutActivity::class.java)
//                    intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
//                    intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
//                    intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
//                    startActivity(intent)
//                } else {
//
//                }
                delegate?.onAddToCart()
                dialog?.dismiss() // Dismiss after successful add/merge or navigation
            }
        } else {
            // Cart is empty, so just add
            addToCart()
//            if (isFastOrder) {
//                val intent = Intent(requireContext(), CheckOutActivity::class.java)
//                intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
//                intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
//                intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
//                startActivity(intent)
//            } else {
//
//            }
            delegate?.onAddToCart()
            dialog?.dismiss() // Dismiss after successful add or navigation
        }
    }

    // Helper function to compare lists of CreateFoodSubItem
    // Relies on CreateFoodSubItem and CreateFoodOption being data classes for proper equals()
    private fun areSubItemsEqual(list1: List<CreateFoodSubItem>, list2: List<CreateFoodSubItem>): Boolean {
        // Data class `equals` should handle comparison of content and order.
        return list1 == list2
    }

    private fun addToCart() {
        PreferenceUtils.writeRestaurant(restVO)
        PreferenceUtils.writeAddToCart(true)

        val currentOrderList = PreferenceUtils.readFoodOrderList()?.toMutableList() ?: mutableListOf()
        val newItemNote = binding.edtNote.text.toString().trim()
        val quantityToAdd = viewModel.qty?.value ?: 1

        // Calculate the price of a single unit of the item with its selected options.
        val singleItemPriceWithSelectedOptions = viewModel.originalPrice + viewModel.subItemTotalPrice

        var itemMerged = false
        for (i in currentOrderList.indices) {
            val existingItem = currentOrderList[i]
            if (existingItem.food_id == foodVO.food_id &&
                existingItem.food_note == newItemNote &&
                areSubItemsEqual(existingItem.sub_item, foodSubItemList)
            ) {
                val newQuantity = existingItem.food_qty + quantityToAdd
                val newTotalPrice = singleItemPriceWithSelectedOptions * newQuantity

                currentOrderList[i] = existingItem.copy(
                    food_qty = newQuantity,
                    food_price = newTotalPrice
                )
                itemMerged = true
                break
            }
        }

        if (!itemMerged) {
            val maxLocalFoodId = currentOrderList.maxOfOrNull { it.local_food_id } ?: 0
            val newFoodVO = CreateFoodVO(
                local_food_id = maxLocalFoodId + 1,
                restaurant_id = restVO.restaurant_id,
                food_id = foodVO.food_id,
                food_name = foodVO.toDefaultFoodName() ?: "",
                food_image = foodVO.food_image ?: "",
                initial_price = viewModel.originalPrice,
                food_qty = quantityToAdd,
                food_note = newItemNote,
                food_price = singleItemPriceWithSelectedOptions * quantityToAdd,
                sub_item = foodSubItemList.map { subItem ->
                    subItem.copy(option = subItem.option.map { opt -> opt.copy() }.toMutableList())
                }.toMutableList() // Deep copy
            )
            currentOrderList.add(newFoodVO)
        }

        PreferenceUtils.writeFoodOrderList(currentOrderList)
        // dialog?.dismiss() // Dismissal is handled in processAddToCart or after navigation
    }

    private fun setUpRestNote() {
        binding.edtNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedNote = s?.toString()?.trim() ?: ""
                binding.tvNoteCount.text = "${trimmedNote.length}/100"
                if (trimmedNote.length > 100) {
                    binding.edtNote.clearFocus()
                    CustomToast(requireContext(),
                        "Note exceeds 100 characters!",false).createCustomToast()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


    private fun addToCartOnDiffRestaurant() {
        // This function prepares the item in viewModel.foodOrderList for the delegate
        // to handle after user confirmation about switching restaurants.
        // It should not merge, but create the item as if it's the first in a new order.
        viewModel.foodOrderList.clear() // Ensure it only contains the item to be added
        viewModel.foodOrderList.add(
            CreateFoodVO(
                local_food_id = 1, // Or some other logic if this ID matters before actual save
                restaurant_id = restVO.restaurant_id,
                food_id = foodVO.food_id,
                food_name = foodVO.toDefaultFoodName() ?: "",
                food_image = foodVO.food_image ?: "",
                initial_price = viewModel.originalPrice,
                food_qty = viewModel.qty?.value ?: 1,
                food_note = binding.edtNote.text.toString().trim(),
                food_price = viewModel.price, // Price as calculated for the current quantity and options
                sub_item = foodSubItemList.map { subItem -> // Deep copy
                    subItem.copy(option = subItem.option.map { opt -> opt.copy() }.toMutableList())
                }.toMutableList()
            )
        )
    }

    private fun setUpRecyclerView() {
        addOnAdapter = AddOnListAdapter(requireContext(), this)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFoodAdd.layoutManager = linearLayoutManager
        foodSubItemList.clear()
        val preparedList = prepareDefaultOptions(subItemList as ArrayList<FoodSubItemVO>)
        addOnAdapter?.submitList(preparedList)
        binding.rvFoodAdd.adapter = addOnAdapter
    }

    @SuppressLint("SetTextI18n")
    override fun onSelectItem(
        data: OptionVO,
        subItem: FoodSubItemVO,
        isRequire: Int
    ) {
        modifyOptionList(subItem, data)
        updatePrice()
    }

    private fun prepareDefaultOptions(data: ArrayList<FoodSubItemVO>): ArrayList<FoodSubItemVO> {
        data.forEach { foodSubItemVO ->
            if (foodSubItemVO.required_type == 1 && foodSubItemVO.option.isNotEmpty()) {
                val defaultOptionVO = foodSubItemVO.option[0]
                val subItemEntry = CreateFoodSubItem(
                    foodSubItemVO.required_type,
                    foodSubItemVO.food_sub_item_id
                )
                val optionEntry = CreateFoodOption(
                    defaultOptionVO.food_sub_item_data_id,
                    defaultOptionVO.food_sub_item_price.toDouble(),
                    defaultOptionVO.item_name_en ?: "",
                    defaultOptionVO.item_name_mm ?: "",
                    defaultOptionVO.item_name_ch ?: ""
                )
                subItemEntry.option.add(optionEntry)
                if (foodSubItemList.none { it.food_sub_item_id == subItemEntry.food_sub_item_id }) {
                    foodSubItemList.add(subItemEntry)
                }
            }
        }
        return data
    }

    private fun modifyOptionList(foodSubVO: FoodSubItemVO, optionVO: OptionVO) {
        val convertedOption = CreateFoodOption(
            optionVO.food_sub_item_data_id,
            optionVO.food_sub_item_price.toDouble(),
            optionVO.item_name_en ?: "",
            optionVO.item_name_mm ?: "",
            optionVO.item_name_ch ?: ""
        )

        val existingSubItem = foodSubItemList.find { it.food_sub_item_id == foodSubVO.food_sub_item_id }

        if (foodSubVO.required_type == 1) { // Single selection
            if (existingSubItem != null) {
                existingSubItem.option.clear()
                existingSubItem.option.add(convertedOption)
            } else {
                val newSubItemEntry = CreateFoodSubItem(
                    foodSubVO.required_type,
                    foodSubVO.food_sub_item_id,
                    mutableListOf(convertedOption)
                )
                foodSubItemList.add(newSubItemEntry)
            }
        } else { // Multiple selections
            if (existingSubItem != null) {
                val optionExists = existingSubItem.option.any { it.food_sub_item_data_id == convertedOption.food_sub_item_data_id }
                if (optionExists) {
                    existingSubItem.option.removeAll { it.food_sub_item_data_id == convertedOption.food_sub_item_data_id }
                } else {
                    existingSubItem.option.add(convertedOption)
                }
                if (existingSubItem.option.isEmpty()) {
                    foodSubItemList.remove(existingSubItem)
                }
            } else {
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
        val basePrice = viewModel.originalPrice

        viewModel.price = (basePrice + viewModel.subItemTotalPrice) * currentQty

        binding.tvTotalAmt.text = "${viewModel.price.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
    }
}
