package com.orikino.fatty.ui.views.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import arrow.core.extensions.list.foldable.isNotEmpty
import com.google.android.material.chip.ChipGroup
import com.orikino.fatty.R
import com.orikino.fatty.databinding.FilterBottomSheetBinding
import com.orikino.fatty.domain.model.FilterDishVO
import com.orikino.fatty.domain.model.SearchFilterCategoryVO
import com.orikino.fatty.domain.model.SearchFilterSubCategoryVO
import com.orikino.fatty.ui.views.activities.search.SearchActivity
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.helper.createChip

class SearchFilterDialogFragment: DialogFragment() {

    private var _binding: FilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var lunchCategory : MutableList<SearchFilterSubCategoryVO>? = null
    private var dissertCategory : MutableList<SearchFilterSubCategoryVO>? = null
    private var drinkCategory : MutableList<SearchFilterSubCategoryVO>? = null

    var onApplyClick : ((MutableList<FilterDishVO>) -> Unit)? = null
    var onCancelClick : (() -> Unit)? = null
    private var filterCategories: List<SearchFilterCategoryVO>? = null
    private var filterSubList: List<FilterDishVO>? = null
    var subList = mutableListOf<FilterDishVO>()

    private var marginTop = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_EdgeToEdge)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.TRANSPARENT
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = true
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun setData(list : List<SearchFilterCategoryVO>, subList: List<FilterDishVO>, topMargin : Int){
        this.filterCategories = list
        this.filterSubList = subList
        this.marginTop = topMargin
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.constraint.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = marginTop
        }
        binding.tvTitleLb.text = getString(R.string.filter)

        filterCategories?.let { list ->
            filterSubList?.let { subList ->
                this.subList.addAll(subList)
            }
            if (this.subList.isNotEmpty()){
                binding.tvTitleLb.text =
                    "${resources.getString(R.string.filter)} (${this.subList.size})"
            }

            binding.tvLucnch.text = list[0].main_category
            binding.tvDessert.text = list[1].main_category
            binding.tvDrink.text = list[2].main_category
            lunchCategory = list[0].sub_category
            dissertCategory = list[1].sub_category
            drinkCategory = list[2].sub_category
            bindFilterChip(chipGroup =  binding.chipGroupLunch,lunchCategory!!)
            bindFilterChip(chipGroup =  binding.chipGroupDessert,dissertCategory!!)
            bindFilterChip(chipGroup =  binding.chipGroupDrink,drinkCategory!!)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            subList.clear()
            bindFilterChip(binding.chipGroupLunch,lunchCategory!!)
            bindFilterChip(binding.chipGroupDessert,dissertCategory!!)
            bindFilterChip(binding.chipGroupDrink,drinkCategory!!)
            onCancelClick?.invoke()
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            showConfirmDialog()
        }
    }

    private fun showConfirmDialog() {
        val title = getString(R.string.txt_apply_filters)
        val desc =
            getString(R.string.txt_if_you_exit_without_applying_the_filter_you_selected_won_t_work)
        val btn = getString(R.string.apply_filter)

        ConfirmDialog.Builder(requireContext(),title,desc,btn,
            callback = {
                onApplyClick?.invoke(getSelectedFilter())
                dismiss()
            }
        ).show(childFragmentManager, SearchActivity::class.java.simpleName)
    }

    private fun bindFilterChip(chipGroup: ChipGroup, filterList: MutableList<SearchFilterSubCategoryVO>) {

        chipGroup.removeAllViews()

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.surfaceUnread),
            ContextCompat.getColor(requireContext(), R.color.fattyPrimary)
        )

        val colorsStateList = ColorStateList(states, colors)
        if (subList.isEmpty()){
            binding.btnApply.isEnabled = false
            binding.btnApply.alpha = 0.5f
        }
        for (sub in filterList.indices) {
            val chips = chipGroup.context.createChip(filterList[sub].category_name)
            if (subList.contains(FilterDishVO(filterList[sub].restaurant_category_id))){
                chips.isChecked = true
                chips.setTextColor(ColorStateList.valueOf("#E1E1E1".toColorInt()))
                chips.chipBackgroundColor = colorsStateList
                chips.chipStrokeWidth = 1f
                chips.chipStrokeColor = ColorStateList.valueOf("#E1E1E1".toColorInt())
            }
            chips.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    chips.setTextColor(ColorStateList.valueOf("#E1E1E1".toColorInt()))
                    chips.chipBackgroundColor = colorsStateList
                    chips.chipStrokeWidth = 1f
                    chips.chipStrokeColor = ColorStateList.valueOf("#E1E1E1".toColorInt())
                    subList.add(FilterDishVO(filterList[sub].restaurant_category_id))
                    binding.btnApply.isEnabled = true
                    binding.btnApply.alpha = 1f
                } else {
                    chips.setTextColor(ColorStateList.valueOf("#2E2E2E".toColorInt()))
                    chips.chipStrokeWidth = 1f
                    chips.chipStrokeColor = ColorStateList.valueOf("#E1E1E1".toColorInt())
                    chips.chipBackgroundColor = colorsStateList
                    subList.remove(FilterDishVO(filterList[sub].restaurant_category_id))
                    if (subList.isEmpty()){
                        binding.btnApply.isEnabled = false
                        binding.btnApply.alpha = 0.5f
                    }
                }
                if (subList.isNotEmpty()){
                    binding.tvTitleLb.text =
                        "${resources.getString(R.string.filter)} (${subList.size})"
                }else{
                    binding.tvTitleLb.text = resources.getString(R.string.filter)
                }

            }
            chipGroup.addView(chips)
        }
    }

    private fun getSelectedFilter(): MutableList<FilterDishVO> {
        return subList
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
