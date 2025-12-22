package com.orikino.fatty.ui.views.activities.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.chip.ChipGroup
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivitySearchFilterBinding
import com.orikino.fatty.domain.model.FilterCategorySearch
import com.orikino.fatty.domain.model.SearchFilterSubCategoryVO
import com.orikino.fatty.domain.responses.SearchCategoryFilterResponse
import com.orikino.fatty.domain.view_model.SearchViewModel
import com.orikino.fatty.domain.viewstates.SearchViewState
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.createChip
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("ResourceAsColor")
@AndroidEntryPoint
class SearchFilterActivity : AppCompatActivity() {

    private lateinit var _binding : ActivitySearchFilterBinding

    private val viewModel : SearchViewModel by viewModels()

    var filterSubList : MutableList<FilterCategorySearch> = mutableListOf()

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),SearchFilterActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySearchFilterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(_binding.root)
        _binding.root.fixCutoutOfEdgeToEdge(_binding.root)

        filterRefresh()
        setUpObserver()
        filterApply()
        onBack()
    }

    private fun filterApply() {
        _binding.btnApply.setOnClickListener {
            viewModel.isFilter = true
            viewModel.isFood = false
            if (filterSubList.size > 0) {
                viewModel.subList
                //startActivity<SearchActivity>()
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun filterRefresh() {
        _binding.swipeRefresh.setOnRefreshListener {
            _binding.swipeRefresh.isRefreshing = true
            clearChips()
            setUpObserver()
        }
    }

    private fun clearChips() {
        _binding.tvLucnch.text = ""
        _binding.tvDrink.text = ""
        _binding.tvDessert.text = ""
        _binding.chipGroupLunch.removeAllViews()
        _binding.chipGroupDessert.removeAllViews()
        _binding.chipGroupDrink.removeAllViews()
        _binding.tvFilterTitleCount.text = resources.getString(R.string.filter)
    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun setUpObserver() {

        viewModel.fetchCategoryFilterSearch()
        viewModel.viewState.observe(this, Observer {
            render(it)
        })
        viewModel.categoryLiveDataList.observe(this, Observer {
            _binding.tvLucnch.text = it[0].main_category
            _binding.tvDessert.text = it[1].main_category
            _binding.tvDrink.text = it[2].main_category
            bindChip(chipGroup = _binding.chipGroupLunch,it[0].sub_category)
            bindChip(chipGroup = _binding.chipGroupDessert,it[1].sub_category)
            bindChip(chipGroup = _binding.chipGroupDrink,it[1].sub_category)
        })
    }

    private fun successSearchCategory(data : SearchCategoryFilterResponse) {
        if (data.success) {
            viewModel.categoryLiveDataList.postValue(data.data)
        }
    }
    private fun bindChip(chipGroup: ChipGroup, filterList: MutableList<SearchFilterSubCategoryVO>) {

        chipGroup.removeAllViews()

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.fattyPrimary)
        )

        val colorsStateList = ColorStateList(states, colors)
        for (sub in filterList.indices) {
            val chips = chipGroup.context.createChip(filterList[sub].category_name)


            chips.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    chips.setTextColor(ColorStateList.valueOf(Color.parseColor("#E1E1E1")))
                    chips.chipBackgroundColor = colorsStateList
                    chips.chipStrokeWidth = 1f
                    chips.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#E1E1E1"))
                    //filterSubList.add(SearchFilterSubCategoryVO(filterList[sub].restaurant_category_id))
                    filterSubList.add(FilterCategorySearch(filterList[sub].restaurant_category_id))
                } else {
                    chips.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                    chips.chipStrokeWidth = 1f
                    chips.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#FF6704"))
                    chips.chipBackgroundColor = colorsStateList
                    filterSubList.remove(FilterCategorySearch(filterList[sub].restaurant_category_id))
                }
                _binding.tvFilterTitleCount.text =
                    "${resources.getString(R.string.filter)} (${viewModel.subList.size})"
            }
            chipGroup.addView(chips)
        }
    }
    private fun render(state : SearchViewState) {
        when(state) {
            is SearchViewState.OnSuccessCategoryFilter -> {
                _binding.swipeRefresh.isRefreshing = false
                successSearchCategory(state.data)
            }
            is SearchViewState.OnFailCategoryFilter -> {
                _binding.swipeRefresh.isRefreshing = false
            }
            else -> {}
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }

}