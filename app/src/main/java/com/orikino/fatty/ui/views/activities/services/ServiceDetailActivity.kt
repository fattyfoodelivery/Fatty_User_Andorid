package com.orikino.fatty.ui.views.activities.services

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.orikino.fatty.R
import com.orikino.fatty.adapters.ServiceShopsAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityServiceDetailBinding
import com.orikino.fatty.databinding.CustomTabItemBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.responses.ServiceCategoryItem
import com.orikino.fatty.domain.responses.ShopData
import com.orikino.fatty.domain.viewstates.ServiceViewState
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.delegate.FilterDelegate
import com.orikino.fatty.ui.views.dialog.FilterBottomSheetFragment
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SmartScrollListener
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.onSearch
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class ServiceDetailActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, SmartScrollListener.OnSmartScrollListener {
    private lateinit var binding : ActivityServiceDetailBinding
    private val viewModel : ServicesViewModel by viewModels()
    private var verticalOffsets: Int = 0
    private var searchQuery : String? = null

    private var serviceId : Int = 0
    private var serviceName : String = ""
    private var serviceDesc : String = ""
    private var serviceCover : String = ""

    private var shopAdapter : ServiceShopsAdapter? = null

    private var currentSelectedCategory : Int? = null

    private var currentFilter : String = "recommended"

    companion object{
        const val SERVICE_ID = "SERVICE_ID"
        const val SERVICE_NAME = "SERVICE_NAME"
        const val SERVICE_DESC = "SERVICE_DESC"
        const val SERVICE_COVER = "SERVICE_COVER"

        fun getIntent(
            context: Context,
            serviceId : Int,
            serviceName : String,
            serviceDesc : String,
            serviceCover : String
        ) : Intent {
            return Intent(context, ServiceDetailActivity::class.java).apply {
                putExtra(SERVICE_ID, serviceId)
                putExtra(SERVICE_NAME, serviceName)
                putExtra(SERVICE_DESC, serviceDesc)
                putExtra(SERVICE_COVER, serviceCover)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fixCutoutOfEdgeToEdge(binding.toolbar, true){ mTopMargin ->
            binding.tvTitleRestName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = mTopMargin
            }
        }
        binding.appbarLayout.addOnOffsetChangedListener(this)
        binding.ivBack.setOnClickListener {
            finish()
        }
        serviceId = intent.getIntExtra(SERVICE_ID, 0)
        serviceName = intent.getStringExtra(SERVICE_NAME) ?: ""
        serviceDesc = intent.getStringExtra(SERVICE_DESC) ?: ""
        serviceCover = intent.getStringExtra(SERVICE_COVER) ?: ""
        binding.tvTitleRestName.text = serviceName
        binding.tvRestaurantDesc.text = serviceDesc
        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/store-service/service_type/").plus(serviceCover))
            .error(R.drawable.ic_service_cover)
            .placeholder(R.drawable.ic_service_cover)
            .into(binding.imvRestaurant)
        viewModel.fetchServiceCategory(serviceId)
        listenToViewModel()
        initUI()
        setUpRecyclerView()
        setUpEmptyView()
    }

    private fun setUpEmptyView(){
        binding.tvErrorMessage.text = getString(R.string.txt_there_is_no_shop_in_s, serviceName)
    }

    private fun setUpRecyclerView(){
        shopAdapter = ServiceShopsAdapter(this, { data, type ->
            when(type){
                "cv_shop" -> {
                    startActivity(ServiceShopWebView.getIntent(this, data.store_id, data.name))
                }
                else -> {

                }
            }
        })
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvServices.layoutManager = linearLayoutManager
        binding.rvServices.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding.rvServices.adapter = shopAdapter
        binding.rvServices.addOnScrollListener(
            SmartScrollListener(
                this,
                null
            )
        )
    }

    private fun listenToViewModel(){
        viewModel.viewState.observe(this){
            render(it)
        }
    }

    private fun render(state : ServiceViewState){
        when(state){
            is ServiceViewState.OnLoadingServiceCategory -> {onLoadingServiceCategory()}
            is ServiceViewState.OnSuccessServiceCategory -> {onSuccessServiceCategory(state)}
            is ServiceViewState.OnFailServiceCategory -> {onFailServiceCategory(state)}

            is ServiceViewState.OnLoadingShopByCategory -> {onLoadingShopByCategory()}
            is ServiceViewState.OnSuccessShopByCategory -> {onSuccessShopByCategory(state)}
            is ServiceViewState.OnFailShopByCategory -> {onFailShopByCategory(state)}
            is ServiceViewState.OnListEndReachShop -> {onListEndReachShop()}
            else -> {}
        }
    }

    private fun onListEndReachShop(){
        val currentList = shopAdapter?.currentList?.toMutableList() ?: mutableListOf()
        if (currentList.isNotEmpty() && currentList[currentList.size-1].isLoadingView){
            currentList.removeAt(currentList.size-1)
        }
        shopAdapter?.submitList(currentList)
    }

    private fun onLoadingShopByCategory(){
        if (viewModel.getTopRelatedCurrentPage() == 1){
            LoadingProgressDialog.showLoadingProgress(this)
        }
    }

    private fun onSuccessShopByCategory(state : ServiceViewState.OnSuccessShopByCategory){
        LoadingProgressDialog.hideLoadingProgress()
        val currentList = shopAdapter?.currentList?.toMutableList() ?: mutableListOf()
        if (currentList.isNotEmpty() && currentList[currentList.size-1].isLoadingView){
            currentList.removeAt(currentList.size-1)
        }
        currentList.addAll(state.data.data!!)
        shopAdapter?.submitList(state.data.data)
        if (state.data.data.isNullOrEmpty()){
            if (searchQuery.isNullOrEmpty()){
                binding.llEmptyShop.show()
                binding.llEmptySearch.gone()
            }
            else {
                binding.llEmptySearch.show()
                binding.llEmptyShop.gone()
            }
        }else{
            binding.llEmptyShop.gone()
            binding.llEmptySearch.gone()
        }
    }

    private fun onFailShopByCategory(state : ServiceViewState.OnFailShopByCategory){
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Error" -> {
                binding.layoutNetworkError.root.show()
            }
            "Another Login" -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        val intent = Intent(this,SplashActivity::class.java)
                        startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(supportFragmentManager, HomeFragment::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, ServiceDetailActivity::class.simpleName)

            else ->
            {
                showSnackBar(state.message)
            }
        }
    }

    private fun onLoadingServiceCategory(){
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun onSuccessServiceCategory(state : ServiceViewState.OnSuccessServiceCategory){
        //LoadingProgressDialog.hideLoadingProgress()
        setupTabs(state.data.data?.service_categories)
        fetchShopByCategory()
    }

    private fun fetchShopByCategory(){
        PreferenceUtils.readUserVO().let {
            viewModel.fetchShopByCategory(serviceId, currentFilter, it.latitude ?: 0.00, it.latitude ?: 0.00, currentSelectedCategory, searchQuery)
        }
    }


    private fun onFailServiceCategory(state : ServiceViewState.OnFailServiceCategory){
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Error" -> {
                binding.layoutNetworkError.root.show()
            }
            "Another Login" -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        val intent = Intent(this,SplashActivity::class.java)
                        startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(supportFragmentManager, HomeFragment::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, ServiceDetailActivity::class.simpleName)

            else ->
            {
                showSnackBar(state.message)
            }
        }
    }

    private fun initUI(){
        binding.layoutFilter.setOnClickListener {
            val bottomSheet = FilterBottomSheetFragment.newInstance(object : FilterDelegate{
                override fun onClickFilterApply(type: String) {
                    currentFilter = type
                    if (currentFilter == "nearby"){
                        binding.filterDot.show()
                    }else{
                        binding.filterDot.gone()
                    }
                    fetchShopByCategory()
                    //Toast.makeText(this@ServiceDetailActivity, type, Toast.LENGTH_SHORT).show()
                }

            }, currentFilter)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        binding.ivClear.gone()
        binding.edtSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        binding.edtSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()){
                    binding.ivClear.show()
                }else{
                    binding.ivClear.gone()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtSearch.onSearch {
            //showShortToast("Search")
            searchQuery = binding.edtSearch.text.toString()
            if (searchQuery?.isNotEmpty() == true){
                fetchShopByCategory()
            }else{
                binding.ivClear.gone()
            }
        }

        if (currentFilter == "nearby"){
            binding.filterDot.show()
        }else{
            binding.filterDot.gone()
        }
        binding.ivClear.setOnClickListener {
            cleanSearch()
            fetchShopByCategory()
            //binding.llEmptySearch.gone()
        }
    }

    private fun cleanSearch() {
        searchQuery = ""
        binding.edtSearch.text?.clear()
        binding.ivClear.gone()
    }

    private fun setupTabs(list : List<ServiceCategoryItem>?) {
        binding.menuTab.removeAllTabs()

        list?.forEach { tab ->
            val tabBinding = CustomTabItemBinding.inflate(layoutInflater)
            tabBinding.tabIcon.setImageResource(R.drawable.ic_tab_sample)
            tabBinding.tabText.text = tab.name
            Log.d("APP_TAG", PreferenceUtils.IMAGE_URL.plus("/store-service/service_category/").plus(tab.image))
            tabBinding.tabIcon.load(PreferenceUtils.IMAGE_URL.plus("/store-service/service_category/").plus(tab.image)) {
                placeholder(R.drawable.ic_category_loading)
                error(R.drawable.ic_category_loading)
            }

            val newTab = binding.menuTab.newTab()
            newTab.tag = tab.service_category_id
            newTab.customView = tabBinding.root

            binding.menuTab.addTab(newTab)
        }

        binding.menuTab.clearOnTabSelectedListeners()

        binding.menuTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateTabState(tab, true)

                // 🔥 access your ID here
                currentSelectedCategory = tab?.tag as? Int
                if (currentSelectedCategory == 0){
                    currentSelectedCategory = null
                }
                fetchShopByCategory()
                // use tabId for navigation / logic
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateTabState(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // ✅ Apply selected state manually for first tab
        binding.menuTab.getTabAt(binding.menuTab.selectedTabPosition)
            ?.let { updateTabState(it, true) }
    }


    private fun updateTabState(tab: TabLayout.Tab?, selected: Boolean) {
        val tabView = tab?.customView ?: return

        val color = if (selected)
            ContextCompat.getColor(this, R.color.fattyPrimary)
        else
            ContextCompat.getColor(this, R.color.textPrimary02)

        tabView.findViewById<TextView>(R.id.tabText).setTextColor(color)

//        tabView.findViewById<ImageView>(R.id.tabIcon).imageTintList =
//            ColorStateList.valueOf(color)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        verticalOffsets = verticalOffset
        when {
            abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                showToolbar()
            } // collapse
            verticalOffset == 0 -> {
                binding.imvRestaurant.alpha = 1f
                hideToolbar()
            } // fully expand
            else -> {
                binding.imvRestaurant.alpha = 0.5f
                hideToolbar()
            } // still collapse or expand
        }
    }

    private fun showToolbar() {
        binding.tvToolbarTitleRestName.show()
    }

    private fun hideToolbar() {
        binding.tvToolbarTitleRestName.gone()
    }

    override fun onListEndReach() {
        val currentList = shopAdapter?.currentList?.toMutableList() ?: mutableListOf()
        currentList.add(ShopData(isLoadingView = true))
        shopAdapter?.submitList(currentList)
        PreferenceUtils.readUserVO().let {
            viewModel.onListEndReachShopByCategory(
                serviceId, currentFilter, it.latitude ?: 0.00, it.latitude ?: 0.00, currentSelectedCategory, searchQuery
            )
        }
    }
}
