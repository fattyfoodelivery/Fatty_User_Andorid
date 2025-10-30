package com.orikino.fatty.ui.views.activities.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.core.extensions.list.foldable.isNotEmpty
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.orikino.fatty.R
import com.orikino.fatty.adapters.FoodAdapter
import com.orikino.fatty.adapters.RestaurantAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivitySearchBinding
import com.orikino.fatty.databinding.LayoutDialogRemoveCartBinding
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.domain.model.FilterDishVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.model.SearchFilterSubCategoryVO
import com.orikino.fatty.domain.model.SearchFoodsVO
import com.orikino.fatty.domain.view_model.SearchViewModel
import com.orikino.fatty.domain.viewstates.FilterViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.checkout.CheckOutActivity
import com.orikino.fatty.ui.views.fragments.rest_more_info.FoodAddOnBottomSheetFragment
import com.orikino.fatty.domain.viewstates.SearchViewState
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.delegate.AddOnDelegate
import com.orikino.fatty.ui.views.dialog.AddOnBottomSheetFragment
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.helper.createChip
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.hideSoftKeyboard
import com.orikino.fatty.utils.helper.onSearch
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultFoodName
import com.orikino.fatty.utils.helper.toDefaultRestaurantAddress
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.orikino.fatty.utils.viewpod.EmptyViewPod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.text.isNotEmpty
import androidx.core.graphics.toColorInt
import com.orikino.fatty.utils.LocaleHelper
import kotlin.collections.map
import kotlin.collections.toList

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), AddOnDelegate {

    private lateinit var searchBinding: ActivitySearchBinding
    lateinit var sheetBehavior: BottomSheetBehavior<*>
    private val viewModel: SearchViewModel by viewModels()
    lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var mEmptyViewPod: EmptyViewPod
    private var restaurantInfO = FoodMenuByRestaurantVO()
    lateinit var foodAdapter: FoodAdapter
    var keywords: MutableList<String> = mutableListOf()
    var vType : Int = 0
    var foodSize : Int = 0
    var restSize : Int = 0
    private var searchQuery : String = ""
    private var lunchCategory : MutableList<SearchFilterSubCategoryVO>? = null
    private var dissertCategory : MutableList<SearchFilterSubCategoryVO>? = null
    private var drinkCategory : MutableList<SearchFilterSubCategoryVO>? = null
    private var isResume = false

    companion object {

        const val SUB_LIST = "sub_filter_list"
        const val VIEW_TYPE = "vtype"
        fun getIntent(sub_filter : ArrayList<Int>,viewType : Int): Intent {
            val intent = Intent(FattyApp.getInstance(), SearchActivity::class.java)
            intent.putExtra(VIEW_TYPE,viewType)
            intent.putIntegerArrayListExtra(SUB_LIST,sub_filter)
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)


        setUpSearchEdt()
        setUpRestaurantList()
        setUpFoodList()
        setUpObserver()
        setUpSearchView()
        filter()
        onBackPress()

        setTag(PreferenceUtils.readSearchRecent())

    }

    private fun onBackPress() {
        searchBinding.ivBack.setOnClickListener {
            finish()
            viewModel.isFilter = false
        }
    }

    override fun onResume() {
        super.onResume()
        checkGPS()

        if (viewModel.subList.isNotEmpty()) {
            val result = Gson().toJson(viewModel.subList)
            PreferenceUtils.readUserVO().customer_id?.let {
                PreferenceUtils.readUserVO().latitude?.let { it1 ->
                    PreferenceUtils.readUserVO().longitude?.let { it2 ->
                        viewModel.filterRestaurantWithCateID(result,
                            it, it1, it2
                        )
                    }
                }
            }
        }else if (searchQuery.isNotEmpty()){
            isResume = true
            searchBinding.chipFood.setChipIconTintResource(R.color.fattyPrimary)
            searchBinding.chipFood.setTextColor(ColorStateList.valueOf(Color.parseColor("#FF6704")))

            viewModel.searchFoodListLiveData.postValue(mutableListOf())
            viewModel.searchRestaurantListLiveData.postValue(mutableListOf())
            viewModel.filterRestaurantListLiveData.postValue(mutableListOf())
            searchBinding.llRecentView.gone()
            searchBinding.rlFirstSearch.root.gone()
            searchBinding.llSegmentControl.show()
            viewModel.customerSearch(searchQuery)
        }
    }
    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
    }

    private fun setUpSearchEdt() {
        searchBinding.ivClear.gone()
        searchBinding.edtSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchBinding.edtSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()){
                    searchBinding.ivClear.show()
                }else{
                    searchBinding.ivClear.gone()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        searchBinding.edtSearch.onSearch {
            //showShortToast("Search")
            searchQuery = searchBinding.edtSearch.text.toString()
            if (searchQuery.isNotEmpty()){
                searchBinding.chipFood.setChipIconTintResource(R.color.fattyPrimary)
                searchBinding.chipFood.setTextColor(ColorStateList.valueOf(Color.parseColor("#FF6704")))

                viewModel.searchFoodListLiveData.postValue(mutableListOf())
                viewModel.searchRestaurantListLiveData.postValue(mutableListOf())
                viewModel.filterRestaurantListLiveData.postValue(mutableListOf())
                searchBinding.llRecentView.gone()
                searchBinding.rlFirstSearch.root.gone()
                searchBinding.llSegmentControl.show()
                searchBinding.rvFood.show()
                searchBinding.rvRestaurant.show()
                viewModel.customerSearch(searchQuery)
                keywords = PreferenceUtils.readSearchRecent()
                if (!keywords.contains(searchQuery))
                    keywords.add(searchQuery)
                PreferenceUtils.writeSearchRecent(keywords)
            }else{
                searchBinding.ivClear.gone()
                viewModel.searchFoodListLiveData.postValue(mutableListOf())
                viewModel.searchRestaurantListLiveData.postValue(mutableListOf())
                searchBinding.chipFood.text = "0"
                searchBinding.chipRestaurant.text = "0"
                searchBinding.rvFood.gone()
                searchBinding.rvRestaurant.gone()
                searchBinding.llRecentView.show()
                searchBinding.rlFirstSearch.root.gone()
            }
        }

        /*searchBinding.edtSearch
            .textInputAsFlow()
            .map { return@map it }
            .onEach {
                if (it!!.isNotEmpty()) {
                    searchBinding.ivClear.show()


                } else {

                }
            }
            .launchIn(lifecycleScope)*/
        searchBinding.ivClear.setOnClickListener {
            cleanSearch()
        }
    }

    private fun showSearchData(){

    }
    private fun cleanSearch() {
        searchBinding.edtSearch.text?.clear()
        searchBinding.ivClear.gone()
        viewModel.searchFoodListLiveData.postValue(mutableListOf())
        viewModel.searchRestaurantListLiveData.postValue(mutableListOf())
        searchBinding.rvFood.gone()
        searchBinding.rvRestaurant.gone()
        searchBinding.llSegmentControl.gone()
        searchBinding.tvResult.gone()
        searchBinding.llRecentView.show()
        searchBinding.rlFirstSearch.root.gone()
        setTag(PreferenceUtils.readSearchRecent())
    }
    private fun setUpObserver() {
        viewModel.viewState.observe(this) { render(it) }
        viewModel.viewStateFilter.observe(this) {renderFilter(it)}
        viewModel.searchFoodListLiveData.observe(this) {
            if (it.isNotEmpty()) {
                hideEmptyView()
                searchBinding.rvFood.show()
            }
            foodAdapter.setNewData(it)
        }
        viewModel.searchRestaurantListLiveData.observe(this) {
            if (it.isNotEmpty()) {
                hideEmptyView()
                searchBinding.rvRestaurant.show()
                restaurantAdapter.submitList(it.toList())
                searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
            }
        }
        /*viewModel.filterRestaurantListLiveData.observe(this) {
            if (it.isNotEmpty()) {
                hideEmptyView()
                searchBinding.rvRestaurant.show()
                restaurantAdapter.setNewData(it)

                Toast.makeText(this, "in live ${it.size}", Toast.LENGTH_SHORT).show()
            }
        }*/

        viewModel.categoryLiveDataList.observe(this, Observer {
            searchBinding.filterView.tvLucnch.text = it[0].main_category
            searchBinding.filterView.tvDessert.text = it[1].main_category
            searchBinding.filterView.tvDrink.text = it[2].main_category
            lunchCategory = it[0].sub_category
            dissertCategory = it[1].sub_category
            drinkCategory = it[2].sub_category
            bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupLunch,lunchCategory!!)
            bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupDessert,dissertCategory!!)
            bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupDrink,drinkCategory!!)
        })


    }

    private fun renderFilter(state : FilterViewState) {
        when(state) {
            is FilterViewState.OnLoadFilter -> renderOnLoadFilter()
            is FilterViewState.OnSuccessFilter -> renderOnSuccessFilter(state)
            is FilterViewState.OnFailFilter -> renderOnFailFilter(state)
        }
    }

    private fun renderOnLoadFilter() {
        LoadingProgressDialog.showLoadingProgress(this)
        searchBinding.filterView.swipeRefresh.isRefreshing = true
    }

    private fun renderOnFailFilter(state: FilterViewState.OnFailFilter) {
        LoadingProgressDialog.hideLoadingProgress()
        searchBinding.filterView.swipeRefresh.isRefreshing = true
    }
    private fun renderOnSuccessFilter(state: FilterViewState.OnSuccessFilter){
        LoadingProgressDialog.hideLoadingProgress()
        searchBinding.filterView.swipeRefresh.isRefreshing = false
        if (state.data.success) {
            restSize = state.data.data.size
            setUpFilterRestaurants(state.data.data)
        }
    }

    private fun setUpFilterRestaurants(data : MutableList<RecommendRestaurantVO>){
        searchBinding.llRecentView.gone()
        searchBinding.rlFirstSearch.root.gone()
        searchBinding.llSegmentControl.show()
        searchBinding.rvRestaurant.show()
        searchBinding.chipFood.text = "0"
        foodSize = 0
        searchBinding.rvRestaurant.show()
        searchBinding.tvResult.show()
        searchBinding.tvResult.text = "${getString(R.string.restaurants)} ・ ${restSize}"
        searchBinding.chipRestaurant.text = "$restSize"
        restaurantAdapter.submitList(data.toList())
        searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
    }
    private fun render(state: SearchViewState) {
        when (state) {
            is SearchViewState.OnLoadingSearchFoodAndRestaurant -> {
                renderOnLoadingSearchFoodAndRestaurant()
            }
            is SearchViewState.OnSuccessSearchFoodAndRestaurant -> successSearchFoodAndRestaurant(state)
            is SearchViewState.OnFailSearchFoodAndRestaurant -> {
                LoadingProgressDialog.hideLoadingProgress()
            }



            is SearchViewState.OnLoadingFilterRestaurant -> {
                renderOnLoadingFilterRestaurant()
            }
            is SearchViewState.OnSuccessFilterRestaurant -> renderOnSuccessFilterRestaurant(state)

            is SearchViewState.OnFailFilterRestaurant -> {
                LoadingProgressDialog.hideLoadingProgress()
                renderOnFailFilterRestaurant(state)
            }

            is SearchViewState.OnLoadingOperateWishList -> {
                renderOnLoadingOperateWishList()
            }
            is SearchViewState.OnSuccessOperateWishList -> {
                LoadingProgressDialog.hideLoadingProgress()
                renderOnSuccessOperateWishList(state)
            }
            is SearchViewState.OnFailOperateWishList -> {
                LoadingProgressDialog.hideLoadingProgress()
            }

            is SearchViewState.OnLoadingCategoryFilter -> renderOnLoadingCategoryFilter()
            is SearchViewState.OnSuccessCategoryFilter -> renderOnSuccessCategoryFilter(state)
            is SearchViewState.OnFailCategoryFilter -> renderOnFailCategoryFilter(state)

            else -> {}
        }
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun setUpSearchView() {
        searchBinding.chipFood.setOnCheckedChangeListener { compoundButton, b ->
            searchBinding.tvResult.show()
            searchBinding.tvResult.text = "${getString(R.string.txt_food)} ・ $foodSize"
            if (b) {
                searchBinding.chipFood.setChipIconTintResource(R.color.fattyPrimary)
                searchBinding.chipFood.setTextColor(ColorStateList.valueOf(Color.parseColor("#FF6704")))
                searchBinding.rvRestaurant.gone()
                searchBinding.rvFood.show()
            } else {
                searchBinding.chipFood.setChipIconTintResource(R.color.black)
                searchBinding.chipFood.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
                searchBinding.rvRestaurant.show()
                searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
                searchBinding.rvFood.gone()
            }
        }

        searchBinding.chipRestaurant.setOnCheckedChangeListener { compoundButton, b ->
            searchBinding.tvResult.show()
            searchBinding.tvResult.text = "${getString(R.string.restaurants)} ・ ${restSize}"
            if (b) {
                searchBinding.chipRestaurant.setChipIconTintResource(R.color.fattyPrimary)
                searchBinding.chipRestaurant.setTextColor(ColorStateList.valueOf(Color.parseColor("#FF6704")))
                searchBinding.rvFood.gone()
                searchBinding.rvRestaurant.show()
                searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
            } else {
                searchBinding.chipRestaurant.setChipIconTintResource(R.color.black)
                searchBinding.chipRestaurant.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
                searchBinding.rvFood.show()
                searchBinding.rvRestaurant.gone()

            }
        }


    }


    private fun filter() {
        searchBinding.ivFilter.setOnClickListener {
            searchBinding.filterView.swipeRefresh.isRefreshing = false
            searchBinding.filterView.tvTitleLb.text = resources.getString(R.string.filter)
            searchBinding.edtSearch.clearFocus()
            viewModel.fetchCategoryFilterSearch()
            searchBinding.filterView.root.show()
            sheetBehavior = BottomSheetBehavior.from(searchBinding.filterView.bottomSheetFilter)
            sheetBehavior.isDraggable = false
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                searchBinding.filterView.root.gone()
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            searchBinding.filterView.btnClose.setOnClickListener {
                searchBinding.filterView.root.gone()
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                if (viewModel.subList.isEmpty()){
                    cleanSearch()
                    viewModel.filterRestaurantListLiveData.postValue(mutableListOf())
                    bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupLunch,lunchCategory!!)
                    bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupDessert,dissertCategory!!)
                    bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupDrink,drinkCategory!!)
                }
            }
            searchBinding.filterView.btnApply.setOnClickListener {
                showConfirmDialog()
            }
            searchBinding.filterView.btnCancel.setOnClickListener {
                viewModel.subList.clear()
                cleanSearch()
                viewModel.filterRestaurantListLiveData.postValue(mutableListOf())
                bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupLunch,lunchCategory!!)
                bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupDessert,dissertCategory!!)
                bindFilterChip(chipGroup =  searchBinding.filterView.chipGroupDrink,drinkCategory!!)
            }
            searchBinding.filterView.swipeRefresh.setOnRefreshListener {
                searchBinding.filterView.swipeRefresh.isRefreshing = true
                filterApply()

            }
        }
    }

    private fun showConfirmDialog() {
        val title = getString(R.string.txt_apply_filters)
        val desc =
            getString(R.string.txt_if_you_exit_without_applying_the_filter_you_selected_won_t_work)
        val btn = getString(R.string.apply_filter)

        ConfirmDialog.Builder(this@SearchActivity,title,desc,btn,
            callback = {
                viewModel.isFilter = true
                filterApply()
            }
        ).show(supportFragmentManager, SearchActivity::class.java.simpleName)
    }

    private fun filterApply() {
        clearSelectedChips()

        searchBinding.chipRestaurant.isChecked = true
        viewModel.searchFoodListLiveData.postValue(mutableListOf())
        searchBinding.edtSearch.clearFocus()
        searchBinding.edtSearch.text?.clear()
        viewModel.isFilter = true
        viewModel.isFood = false

        if (viewModel.subList.isNotEmpty()) {
            val result = Gson().toJson(viewModel.subList)
            PreferenceUtils.readUserVO().customer_id?.let {
                PreferenceUtils.readUserVO().latitude?.let { it1 ->
                    PreferenceUtils.readUserVO().longitude?.let { it2 ->
                        viewModel.filterRestaurantWithCateID(result,
                            it, it1, it2
                        )
                    }
                }
            }
        }

        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        searchBinding.filterView.root.gone()

    }

    private fun AppCompatEditText.textInputAsFlow() = callbackFlow {
        val watcher: TextWatcher = doOnTextChanged { textInput: CharSequence?, _, _, _ ->
            this.trySend(textInput).isSuccess
        }
        awaitClose { this@textInputAsFlow.removeTextChangedListener(watcher) }
    }


    private fun bindFilterChip(chipGroup: ChipGroup, filterList: MutableList<SearchFilterSubCategoryVO>) {

        chipGroup.removeAllViews()

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.surfaceUnread),
            ContextCompat.getColor(this, R.color.fattyPrimary)
        )

        val colorsStateList = ColorStateList(states, colors)
        if (viewModel.subList.isEmpty()){
            searchBinding.filterView.btnApply.isEnabled = false
            searchBinding.filterView.btnApply.alpha = 0.5f
        }
        for (sub in filterList.indices) {
            val chips = chipGroup.context.createChip(filterList[sub].category_name)
            if (viewModel.subList.contains(FilterDishVO(filterList[sub].restaurant_category_id))){
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
                    viewModel.subList.add(FilterDishVO(filterList[sub].restaurant_category_id))
                    searchBinding.filterView.btnApply.isEnabled = true
                    searchBinding.filterView.btnApply.alpha = 1f
                } else {
                    chips.setTextColor(ColorStateList.valueOf("#2E2E2E".toColorInt()))
                    chips.chipStrokeWidth = 1f
                    chips.chipStrokeColor = ColorStateList.valueOf("#E1E1E1".toColorInt())
                    chips.chipBackgroundColor = colorsStateList
                    viewModel.subList.remove(FilterDishVO(filterList[sub].restaurant_category_id))
                    if (viewModel.subList.isEmpty()){
                        searchBinding.filterView.btnApply.isEnabled = false
                        searchBinding.filterView.btnApply.alpha = 0.5f
                    }
                }
                if (viewModel.subList.isNotEmpty()){
                    searchBinding.filterView.tvTitleLb.text =
                        "${resources.getString(R.string.filter)} (${viewModel.subList.size})"
                }else{
                    searchBinding.filterView.tvTitleLb.text = resources.getString(R.string.filter)
                }

            }
            chipGroup.addView(chips)
        }
    }

    private fun showEmptyView(msg : String) {
    }

    private fun hideEmptyView() {

    }


    private fun renderOnLoadingCategoryFilter(){}
    private fun renderOnSuccessCategoryFilter(state: SearchViewState.OnSuccessCategoryFilter){
        if (state.data.success) {
            viewModel.categoryLiveDataList.postValue(state.data.data)
        }
    }
    private fun renderOnFailCategoryFilter(state: SearchViewState.OnFailCategoryFilter){}

    private fun renderOnFailFilterRestaurant(state: SearchViewState.OnFailFilterRestaurant) {
        when(state.message) {
            "Server Issue" -> {
                CustomToast(this,"Internal Server Error",false).createCustomToast()
            }

        }
    }

    private fun renderOnSuccessOperateWishList(state: SearchViewState.OnSuccessOperateWishList) {
        if (state.data.success) {
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)
            PreferenceUtils.wishListRestaurant.postValue(
                Pair(
                    state.data.data.restaurant_id,
                    state.data.data.is_wish
                )
            )
            val currentAdapterList = restaurantAdapter.currentList
            val newAdapterList = currentAdapterList.map { restaurant ->
                if (state.data.data.restaurant_id == restaurant.restaurant_id) {
                    restaurant.copy(is_wish = state.data.message == "successfull customer wishlist create")
                } else {
                    restaurant
                }
            }
            restaurantAdapter.submitList(newAdapterList.toList())
            searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
//            viewModel.searchRestaurantListLiveData.value?.forEach {
//                if (state.data.data.restaurant_id == it.restaurant_id) {
//                    it.is_wish = state.data.success
//                    viewModel.searchRestaurantListLiveData.observe(this) {
//                        /*rvSearchRestaurants.update(
//                            it
//                        )*/
//                        restaurantAdapter.setNewData(it)
//                    }
//                }
//
//            }
        }
    }

    private fun renderOnLoadingOperateWishList() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnLoadingFilterRestaurant() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnLoadingSearchFoodAndRestaurant() {
        LoadingProgressDialog.showLoadingProgress(this)
    }



    private fun renderOnSuccessFilterRestaurant(state : SearchViewState.OnSuccessFilterRestaurant) {
        LoadingProgressDialog.hideLoadingProgress()
        searchBinding.filterView.swipeRefresh.isRefreshing = false
        if (state.data.success) {

            searchBinding.llRecentView.gone()
            searchBinding.rlFirstSearch.root.gone()
            searchBinding.llSegmentControl.show()
            searchBinding.chipFood.text = "0"
            searchBinding.rvRestaurant.show()
            //foodAdapter.clearData()
            //viewModel.searchFoodListLiveData.postValue(mutableListOf())
            searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
            searchBinding.chipRestaurant.text = state.data.data.size.toString()
        }
    }
    private fun successSearchFoodAndRestaurant(state : SearchViewState.OnSuccessSearchFoodAndRestaurant) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            foodSize = state.data.data.food.size
            restSize = state.data.data.restaurant.size
            if (!isResume) {
                clearSelectedChips()
                searchBinding.tvResult.show()
                searchBinding.tvResult.text = "${getString(R.string.txt_food)} ・ ${foodSize}"

                searchBinding.chipFood.text = "${state.data.data.food.size}"
                searchBinding.chipRestaurant.text = "${state.data.data.restaurant.size}"
                viewModel.filterRestaurantListLiveData.postValue(mutableListOf())
            }


            when (state.data.data.food.size) {
                0 -> {
                    if (!isResume)
                        viewModel.searchFoodListLiveData.postValue(mutableListOf())
                    foodAdapter.setNewData(state.data.data.food)
                }
                else -> {
                    if (!isResume)
                        viewModel.searchFoodListLiveData.postValue(state.data.data.food)
                    foodAdapter.setNewData(state.data.data.food)
                }
            }

            when (state.data.data.restaurant.size) {
                0 -> {
                    if (!isResume)
                        viewModel.searchRestaurantListLiveData.postValue(mutableListOf())
                    restaurantAdapter.submitList(state.data.data.restaurant.toList())
                    searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
                }
                else -> {
                    if (!isResume)
                        viewModel.searchRestaurantListLiveData.postValue(state.data.data.restaurant)
                    restaurantAdapter.submitList(state.data.data.restaurant.toList())
                    searchBinding.rvRestaurant.requestLayout() // Changed from invalidate
                }
            }
            isResume = false

        }
    }

    private fun clearSelectedChips() {
        searchBinding.chipsGroup.clearCheck()
    }


    private fun setUpRestaurantList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        searchBinding.rvRestaurant.layoutManager = linearLayoutManager
        searchBinding.rvRestaurant.addItemDecoration(
            EqualSpacingItemDecoration(
                spacing = 24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        searchBinding.rvRestaurant.setHasFixedSize(true)
        searchBinding.rvRestaurant.isNestedScrollingEnabled = true
        restaurantAdapter = RestaurantAdapter(FattyApp.getInstance()) { data,str,pos ->

            when(str) {
                "fav" -> {
                    viewModel.operateWishList(data.restaurant_id)
                }
                "root" -> {
                    /*startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to data.restaurant_id
                    )*/
                    val intent = Intent(this, RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant_id)
                    startActivity(intent)
                }
            }
            //startActivity(RestaurantDetailActivity.getIntent(data.restaurant_id,"search"))
        }
        searchBinding.rvRestaurant.adapter = restaurantAdapter
    }

    private fun setUpFoodList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        searchBinding.rvFood.layoutManager = linearLayoutManager
        searchBinding.rvFood.addItemDecoration(
            EqualSpacingItemDecoration(
                spacing = 24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        searchBinding.rvFood.setHasFixedSize(true)
        searchBinding.rvFood.isNestedScrollingEnabled = true
        foodAdapter = FoodAdapter(this) { data,str,pos ->
            when(str){
                "root" -> {
                    val intent = Intent(this,RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant.restaurant_id)
                    startActivity(intent)
                }
                else -> {
                    addFoodToCart(data)
                }
            }

        }
        searchBinding.rvFood.adapter = foodAdapter
    }

    private fun addFoodToCart(data : SearchFoodsVO) {
        if (PreferenceUtils.readUserVO().customer_id == 0) {
            SuccessDialog.Builder(
                this,
                resources.getString(R.string.login_message),
                callback = {
                    //startActivity<LoginActivity>()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
                .show(supportFragmentManager, SearchActivity::class.simpleName)
        } else {
            if (data.restaurant.distance > data.restaurant.limit_distance) {
                showSnackBar(resources.getString(R.string.out_of_service))
            } else {
                if (data.restaurant.restaurant_emergency_status == 0) {
                    //if(data.food_emergency_status == 0) {
                    restaurantInfO = FoodMenuByRestaurantVO(
                        restaurant_id = data.restaurant.restaurant_id,
                        restaurant_name_mm = data.restaurant.toDefaultRestaurantName(),
                        restaurant_address_mm = data.restaurant.toDefaultRestaurantAddress(),
                        restaurant_image = data.restaurant.restaurant_image
                    )

                    val transformToFoodVO = FoodVO(
                        food_id = data.food_id,
                        food_name_mm = data.toDefaultFoodName(),
                        food_price = data.food_price,
                        food_image = data.food_image
                    )
                    val bottomSheetFragment =
                        AddOnBottomSheetFragment.newInstance(true,
                            restaurantInfO,
                            transformToFoodVO,
                            data.sub_item, this)
                    supportFragmentManager.let {
                        bottomSheetFragment.show(
                            it,
                            bottomSheetFragment.tag
                        )
                    }
                } else
                    showSnackBar(resources.getString(R.string.unavailable))
            }
        }
        /*tv_food_name.text = data.toDefaultFoodName()
        tv_food_prize.text = "${
            data.food_price.toDouble()//.toThousandSeparator()
        } ${Preference.readCurrencyId().currency_symbol}"
        tv_food_name.setOnClickListener {
            //showRestaurantFoodPhoto(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/food/${data.food_image}" else "${Preference.PRODUCTION_URL}uploads/food/${data.food_image}")
            showRestaurantFoodPhoto(Preference.IMAGE_URL+"/food/"+data.food_image)
        }
        //imv_food.setOnClickListener { showRestaurantFoodPhoto(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/food/${data.food_image}" else "${Preference.PRODUCTION_URL}uploads/food/${data.food_image}") }
        imv_food.setOnClickListener { showRestaurantFoodPhoto(Preference.IMAGE_URL+"/food/"+data.food_image) }
        tv_rest_name.text = data.restaurant.toDefaultRestaurantName()
        tv_rest_location.text = data.restaurant.toDefaultRestaurantAddress()
        *//*imv_food.loadSlide(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/food/${data.food_image}" else "${Preference.PRODUCTION_URL}uploads/food/${data.food_image}")
        iv_rest_cover.loadSlide(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/restaurant/${data.restaurant.restaurant_image}" else "${Preference.PRODUCTION_URL}uploads/restaurant/${data.restaurant.restaurant_image}")*//*

        imv_food.loadSlide(Preference.IMAGE_URL+"/food/"+data.food_image)
        iv_rest_cover.loadSlide(Preference.IMAGE_URL+"/restaurant/"+data.restaurant.restaurant_image)
        btnAddSearchFood.setOnClickListener { it1 ->
            if (Preference.readUserVO().customer_id == 0) {
                SuccessDialog.Builder(
                    context,
                    resources.getString(R.string.login_message),
                    callback = {
                        startActivity<LoginActivity>()
                        finish()
                    })
                    .show(supportFragmentManager, SearchFoodAndRestaurantActivity::class.simpleName)
            } else {
                if (data.restaurant.distance > data.restaurant.limit_distance) {
                    showSnackBar(resources.getString(R.string.out_of_service))
                } else {
                    if (data.restaurant.restaurant_emergency_status == 0) {
                        //if(data.food_emergency_status == 0) {
                        restaurantInfO = FoodMenuByRestaurantVO(
                            restaurant_id = data.restaurant.restaurant_id,
                            restaurant_name_mm = data.restaurant.toDefaultRestaurantName(),
                            restaurant_address_mm = data.restaurant.toDefaultRestaurantAddress(),
                            restaurant_image = data.restaurant.restaurant_image
                        )

                        val transformToFoodVO = FoodVO(
                            food_id = data.food_id,
                            food_name_mm = data.toDefaultFoodName(),
                            food_price = data.food_price
                        )
                        val bottomSheetFragment =
                            FoodAddOnBottomSheetFragment.newInstance(true,
                                restaurantInfO,
                                transformToFoodVO,
                                data.sub_item, onAddCart = { },
                                onDeleteItem = {
                                    showGameOverDialog(it)
                                })
                        supportFragmentManager.let {
                            bottomSheetFragment.show(
                                it,
                                bottomSheetFragment.tag
                            )
                        }
                    } else showSnackBar(resources.getString(R.string.notice_message))
                }
            }
        }

      */
    }

    private fun showGameOverDialog(foodList: MutableList<CreateFoodVO>) {
        val bind = LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this@SearchActivity)
        builder.setView(bind.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)

            bind.btnClose.setOnClickListener {
                dismiss()
            }
            bind.btnRemove.setOnClickListener {
                PreferenceUtils.writeRestaurant(restaurantInfO)
                PreferenceUtils.writeFoodOrderList(mutableListOf())
                PreferenceUtils.writeFoodOrderList(foodList)
                dismiss()
                /*startActivity<CheckOutActivity>(
                    CheckOutActivity.LAT to PreferenceUtils.readUserVO().latitude,
                    CheckOutActivity.LNG to PreferenceUtils.readUserVO().longitude,
                    CheckOutActivity.ADDRESS_TYPE to ""
                )*/
                val intent = Intent(this@SearchActivity, CheckOutActivity::class.java)
                intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                startActivity(intent)
            }
            show()
        }
    }


//    private fun createChip(name: String): Chip {
//        val chip = Chip(this, null, com.google.android.material.R.style.Base_Widget_MaterialComponents_Chip)
//        chip.layoutParams = LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//
//        chip.text = name
//        chip.isCloseIconVisible = true
//        // chip.isChipIconVisible = true
//        chip.closeIconTint = (ColorStateList.valueOf(Color.parseColor("#B1B1B1")))
//        chip.isCheckable = true
//        chip.isClickable = true
//        chip.setOnCloseIconClickListener {
//            viewModel.customerSearch(chip.text.toString())
//        }
//        return chip
//    }

    private fun setTag(keywords: MutableList<String>) {
        if (keywords.isEmpty()){
            searchBinding.rlFirstSearch.root.show()
            searchBinding.llRecentView.gone()
        }else{
            searchBinding.chipRecentFood.removeAllViews()
            for (name in keywords) {
                val tagName =   name
                val chip = Chip(this)
                val paddingDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2f,
                    resources.displayMetrics
                ).toInt()
                val states = arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                )
                val colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.natural300),
                    ContextCompat.getColor(this, R.color.natural300)
                )

                val colorsStateList = ColorStateList(states, colors)
                chip.setPadding(paddingDp,paddingDp, paddingDp, paddingDp)
                chip.text = tagName
                chip.textSize = 12f
                chip.chipStrokeWidth = 1f
                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
                //chip.compoundDrawablePadding
                //chip.setCloseIconResource(R.drawable.baseline_close_24)
                chip.isCloseIconEnabled = true
                chip.chipBackgroundColor = colorsStateList
                chip.setOnCloseIconClickListener {
                    searchBinding.chipRecentFood.removeView(chip)
                    val recents = PreferenceUtils.readSearchRecent()
                    recents.remove(tagName)
                    PreferenceUtils.writeSearchRecent(recents)
                    if (recents.isEmpty()){
                        searchBinding.rlFirstSearch.root.show()
                        searchBinding.llRecentView.gone()
                    }
                }
                chip.setOnClickListener {
                    hideSoftKeyboard()
                    searchBinding.edtSearch.clearFocus()
                    searchBinding.edtSearch.setText(tagName)
                    searchBinding.chipFood.setChipIconTintResource(R.color.fattyPrimary)
                    searchBinding.chipFood.setTextColor(ColorStateList.valueOf(Color.parseColor("#FF6704")))

                    viewModel.searchFoodListLiveData.postValue(mutableListOf())
                    viewModel.searchRestaurantListLiveData.postValue(mutableListOf())
                    viewModel.filterRestaurantListLiveData.postValue(mutableListOf())
                    searchBinding.llRecentView.gone()
                    searchBinding.rlFirstSearch.root.gone()
                    searchBinding.llSegmentControl.show()
                    searchBinding.rvFood.show()
                    searchBinding.rvRestaurant.show()
                    searchQuery = tagName
                    viewModel.customerSearch(tagName)
                    viewModel.isRecent = true
                }
                searchBinding.chipRecentFood.addView(chip)
            }
        }
    }

    override fun onAddToCart() {
    }

    override fun onDeleteItem(foodList: MutableList<CreateFoodVO>) {
        showGameOverDialog(foodList)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}
