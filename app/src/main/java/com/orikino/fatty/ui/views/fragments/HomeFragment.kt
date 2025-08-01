package com.orikino.fatty.ui.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orikino.fatty.domain.model.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.LatLng
import com.orikino.fatty.HomeViewModel
import com.orikino.fatty.HomeViewState
import com.orikino.fatty.R
import com.orikino.fatty.adapters.HomeSlideAdapter
//import com.solinx_tech.fattyfood.adapters.AdsSlideAdapter
import com.orikino.fatty.adapters.NearByIdRestAdapter
import com.orikino.fatty.adapters.RecommendedRestaurantAdapter
import com.orikino.fatty.adapters.TopCategoryAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.FragmentHomeBinding
import com.orikino.fatty.databinding.LayoutCurrencyZoneBinding
import com.orikino.fatty.domain.model.UpAndDownVO
import com.orikino.fatty.ui.views.activities.account_setting.help_center.HelpCenterActivity
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.category.TopRelatedCategoryActivity
import com.orikino.fatty.ui.views.activities.parcel.BookingOrderActivity
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.activities.search.SearchActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.activities.wish_list.WishListActivity
import com.orikino.fatty.ui.views.fragments.address_bottom_sheet.AddressBottomSheetFragment
import com.orikino.fatty.ui.views.fragments.address_bottom_sheet.AddressBottomSheetMapboxFragment
import com.orikino.fatty.ui.views.fragments.address_bottom_sheet.MapsFragment
import com.orikino.fatty.utils.AccountRestrictedDialog
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.delegate.CallBackMapLatLngListener
import com.orikino.fatty.utils.helper.correctLocale
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() , CallBackMapLatLngListener {


    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    private var topCategoryAdapter: TopCategoryAdapter? = null
    private var recommendedRestaurantAdapter: RecommendedRestaurantAdapter? = null
    private var nearByIdRestAdapter: NearByIdRestAdapter? = null
    //private lateinit var adsSlideAdapter: AdsSlideAdapter
    private var addresses: List<Address> = listOf()
    private  var nearByRestaurantList : MutableList<NearByRestaurantVO>? = mutableListOf()
    var lastSelected = 0

    companion object {
        private const val GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return (binding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PreferenceUtils.isBackground = false

        setUpRecommendedRestaurants()
        setUpTopFoodCategory()
        setUpNearByRestaurants()
        subscribeUI()
        requireContext().correctLocale()
        navigator()
        navigateToTopRated()
        navigateToParcel()
        onRefreshHome()


    }

    private fun navigateToParcel() {
        binding?.rlTopParcel?.setOnClickListener {
            PreferenceUtils.isBackground = false
            if (PreferenceUtils.readUserVO().customer_id == 0) {
                SuccessDialog.Builder(
                    requireContext(),
                    resources.getString(R.string.login_message),
                    callback = {
                        //requireContext().startActivity<LoginActivity>()
                        val intent = Intent(requireContext(),LoginActivity::class.java)
                        context?.startActivity(intent)
                    })
                    .show(
                        childFragmentManager,
                        HomeFragment::class.simpleName
                    )
            } else if (PreferenceUtils.readUserVO().is_restricted == 1){
                AccountRestrictedDialog.Builder(
                    requireContext(),
                    callback = {
                        //requireActivity().startActivity<HelpCenterActivity>()
                        val intent = Intent(requireContext(),HelpCenterActivity::class.java)
                        context?.startActivity(intent)
                    })
                    .show(
                        childFragmentManager,
                        HomeFragment::class.simpleName
                    )
            } else {
                /*requireActivity().startActivity<BookingOrderActivity>(
                    BookingOrderActivity.IS_EDIT to false
                )*/
                val intent = Intent(requireContext(),BookingOrderActivity::class.java)
                intent.putExtra(BookingOrderActivity.IS_EDIT,false)
                context?.startActivity(intent)
            }

        }
    }

    private fun navigateToTopRated() {
        binding?.rlTopRestaurant?.setOnClickListener {
            PreferenceUtils.isBackground = false
            /*requireActivity().startActivity<TopRelatedCategoryActivity>(
                TopRelatedCategoryActivity.CATG to "Top-Rated"
            )*/
            val intent = Intent(requireContext(),TopRelatedCategoryActivity::class.java)
            intent.putExtra(TopRelatedCategoryActivity.CATG,"Top-Rated")
            context?.startActivity(intent)
        }
    }

    private fun checkService() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(requireContext())


        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                //setUpMapBoxBottomSheet()
                setUpMapBox()
            }
        } else {
            if (result == 0) setUpMapBox() //setUpGoogleMapBottomSheet()
            else setUpMapBox() //setUpMapBoxBottomSheet()

        }
    }

    private fun setUpGoogleMapBottomSheet() {
        if (fragmentManager?.findFragmentByTag("sheet") == null) {
            val bottomSheetFragment = AddressBottomSheetFragment.newInstance(onConfirmAddress = {
                binding?.tvUserAddress?.text = convertLatLangToAddress(
                    PreferenceUtils.readUserVO().latitude ?: 0.0,
                    PreferenceUtils.readUserVO().longitude ?: 0.0
                )
                if (PreferenceUtils.readUserVO().customer_id != 0) PreferenceUtils.readUserVO().customer_id?.let { it1 ->
                    viewModel.updateUserInfo(
                        it1,
                        PreferenceUtils.readUserVO().latitude ?: 0.0,
                        PreferenceUtils.readUserVO().longitude ?: 0.0
                    )
                }
                //update
                else PreferenceUtils.readUserVO()?.customer_id?.let { it1 ->
                    viewModel.fetchHome(
                        it1,
                        viewModel.stateName,
                        PreferenceUtils.readUserVO().latitude ?: 0.0,
                        PreferenceUtils.readUserVO().longitude ?: 0.0
                    )
                }

            })
            requireActivity().supportFragmentManager.let {
                bottomSheetFragment.show(
                    it, "sheet"
                )
            }
        } else {
        }
    }

    private fun setUpMapBoxBottomSheet() {
        if (fragmentManager?.findFragmentByTag("sheet") == null) {
            val bottomSheetFragment =
                AddressBottomSheetMapboxFragment.newInstance(onConfirmAddress = {
                    binding?.tvUserAddress?.text = convertLatLangToAddress(
                        PreferenceUtils.readUserVO().latitude ?: 0.0,
                        PreferenceUtils.readUserVO().longitude ?: 0.0
                    )
                    if (PreferenceUtils.readUserVO().customer_id != 0) PreferenceUtils.readUserVO().customer_id?.let { it1 ->
                        viewModel.updateUserInfo(
                            it1,
                            PreferenceUtils.readUserVO().latitude ?: 0.0,
                            PreferenceUtils.readUserVO().longitude ?: 0.0
                        )
                    }
                    else PreferenceUtils.readUserVO().customer_id?.let { it1 ->
                        viewModel.fetchHome(
                            it1,
                            viewModel.stateName,
                            PreferenceUtils.readUserVO().latitude ?: 0.0,
                            PreferenceUtils.readUserVO().longitude ?: 0.0
                        )
                    }
                })
            requireActivity().supportFragmentManager.let {
                bottomSheetFragment.show(
                    it, "sheet"
                )
            }
        } else {


        }
    }

    private fun setUpMapBox() {
        if (fragmentManager?.findFragmentByTag("signature") == null) {
            val fullDialogMap = MapsFragment.newInstance(this)
                /*MapsFragment(cal = {
                    binding?.tvUserAddress?.text = convertLatLangToAddress(
                        PreferenceUtils.readUserVO().latitude ?: 0.0,
                        PreferenceUtils.readUserVO().longitude ?: 0.0
                    )
                    if (PreferenceUtils.readUserVO().customer_id != 0) PreferenceUtils.readUserVO()?.customer_id?.let { it1 ->
                        viewModel.updateUserInfo(
                            it1,
                            PreferenceUtils.readUserVO().latitude ?: 0.0,
                            PreferenceUtils.readUserVO().longitude ?: 0.0
                        )
                    }
                    else PreferenceUtils.readUserVO().customer_id.let { it1 ->
                        viewModel.fetchHome(
                            it1,
                            viewModel.stateName,
                            PreferenceUtils.readUserVO().latitude ?: 0.0,
                            PreferenceUtils.readUserVO().longitude ?: 0.0
                        )
                    }
                })*/
            requireActivity().supportFragmentManager.let {
                fullDialogMap.show(
                    it, "signature"
                )
            }
        } else {
        }
    }


    private fun checkGPS() {
        var gpsTracker = GpsTracker(requireContext())
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        } else {

            if (gpsTracker.latitude != 0.0 && gpsTracker.longitude != 0.0) {
                if (MainActivity.isFirstTime) {
                    binding?.tvUserAddress?.show()
                    PreferenceUtils.writeUserVO(
                        PreferenceUtils.readUserVO()
                            .copy(latitude = gpsTracker.latitude, longitude = gpsTracker.longitude)
                    )
                    binding?.tvUserAddress?.text = convertLatLangToAddress(
                        PreferenceUtils.readUserVO().latitude?:0.0,
                        PreferenceUtils.readUserVO().longitude?:0.0
                    )
                    shouldUpdateOrFetchHome()
                    MainActivity.isFirstTime = false
                }

            } else checkService()
        }
    }

    private fun isPlayStoreInstalled(context: Context): Boolean {
        var result = false
        result = try {
            val packageInfo = context.packageManager.getPackageInfo(GOOGLE_PLAY_STORE_PACKAGE, 0)
            packageInfo.applicationInfo.enabled
        } catch (exc: PackageManager.NameNotFoundException) {
            false
        }
        return result
    }

    private fun onRefreshHome() {
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.isRefresh = true
            shouldUpdateOrFetchHome()
        }
    }

    private fun navigator() {
        binding?.edtSearch?.setOnClickListener {
            PreferenceUtils.needToShow = false
            PreferenceUtils.isBackground = false
            val intent = Intent(requireContext(),SearchActivity::class.java)
            context?.startActivity(intent)
            //context?.startActivity<SearchActivity>()
        }
        binding?.imvFav?.setOnClickListener {
            PreferenceUtils.needToShow = false
            PreferenceUtils.isBackground = false
            if (PreferenceUtils.readUserVO().customer_id == 0) {
                SuccessDialog.Builder(requireActivity(),
                    resources.getString(R.string.login_message),
                    callback = {
                        //requireActivity().startActivity<LoginActivity>()
                        val intent = Intent(requireContext(),LoginActivity::class.java)
                        context?.startActivity(intent)
                    }).show(
                    requireActivity().supportFragmentManager, HomeFragment::class.simpleName
                )
            } else {
                //context?.startActivity<WishListActivity>()
                val intent = Intent(requireContext(),WishListActivity::class.java)
                context?.startActivity(intent)
            }
        }

        binding?.tvEdit?.setOnClickListener {
            try {
                checkService()
            } catch (e: Exception) {
            }
        }


    }


    private fun convertLatLangToAddress(lat: Double, lng: Double): String {
        var address = ""
        try {
            val geocoder = Geocoder(requireContext(), Locale.US)
            addresses = geocoder.getFromLocation(lat, lng, 1)!!
            address = addresses[0].getAddressLine(0)
        } catch (e: Exception) { }
        return address
    }


    private fun startShimmerEffect() {
        binding?.shimmerView?.show()
        binding?.shimmerView?.startShimmer()
        binding?.rlTopView?.gone()
        //binding?.tvYouMightLike?.gone()
        binding?.tvNearYou?.gone()


        if (viewModel.isRefresh) binding?.swipeRefresh?.isRefreshing = true
    }

    private fun stopShimmerEffect() {
        binding?.shimmerView?.stopShimmer()
        binding?.shimmerView?.gone()
        binding?.rlTopView?.show()
        //binding?.tvYouMightLike?.show()
        binding?.tvNearYou?.show()
        if (viewModel.isRefresh) binding?.swipeRefresh?.isRefreshing = false
    }



    private fun shouldUpdateOrFetchHome() {
        if (PreferenceUtils.readUserVO().customer_id != 0) PreferenceUtils.readUserVO().customer_id?.let {
            viewModel.updateUserInfo(
                it,
                PreferenceUtils.readUserVO().latitude?:0.0,
                PreferenceUtils.readUserVO().longitude?:0.0
            )
        }
        //update
        else PreferenceUtils.readUserVO().customer_id?.let {
            viewModel.fetchHome(
                it,
                viewModel.stateName,
                PreferenceUtils.readUserVO().latitude?:0.0,
                PreferenceUtils.readUserVO().longitude?:0.0
            )
        }
    }

    private fun subscribeUI() { observers() }

    @SuppressLint("NotifyDataSetChanged")
    private fun observers() {
        MainActivity.isCurrencyUpdate.observe(viewLifecycleOwner) {
            if (it == true) shouldUpdateOrFetchHome()
            MainActivity.isCurrencyUpdate.postValue(false)
        }
        PreferenceUtils.wishListCount.observe(viewLifecycleOwner) {
            //top_badge.text = "$it"
        }
        viewModel.viewState.observe(viewLifecycleOwner) { render(it) }

    }

    private fun render(state: HomeViewState) {
        when (state) {
            is HomeViewState.OnLoadingUpdateUserInfo -> {  } //renderOnLoadingUpdateInfo()
            is HomeViewState.OnSuccessUpdateUserInfo -> renderOnSuccessUpdateInfo(state)
            is HomeViewState.OnFailUpdateUserInfo -> renderOnFailUpdateInfo(state)

            is HomeViewState.OnLoadingHome -> { renderOnLoadingHome() }
            is HomeViewState.OnSuccessHome ->  renderOnSuccessHome(state)
            is HomeViewState.OnFailHome -> renderOnFailHome(state)

            is HomeViewState.OnLoadingCurrency -> {}//renderOnLoadingCurrency()
            is HomeViewState.OnSuccessCurrency -> renderOnSuccessCurrency(state)
            is HomeViewState.OnFailCurrency -> renderOnFailCurrency(state)

            is HomeViewState.OnSuccessAdsEngagement -> {}
            is HomeViewState.OnFailAdsEngagement -> {}

            is HomeViewState.OnLoadingOperateWishList -> renderOnLoadingOperateWishList()
            is HomeViewState.OnSuccessOperateWishList -> renderOnSuccessOperateWishList(state)
            is HomeViewState.OnFailOperateWishList -> renderOnFailOperateWishList(state)

            else -> {}
        }
    }

    private fun renderOnLoadingHome() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderOnLoadingUpdateInfo() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderOnSuccessUpdateInfo(state: HomeViewState.OnSuccessUpdateUserInfo) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            binding?.layoutNetworkError?.root?.gone()
            PreferenceUtils.writeUserVO(state.data.data)
            //update version
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchHome(
                    it,
                    viewModel.stateName,
                    PreferenceUtils.readUserVO().latitude?:0.0 ,
                    PreferenceUtils.readUserVO().longitude?:0.0
                )
            }
        }
    }

    private fun renderOnFailUpdateInfo(state: HomeViewState.OnFailUpdateUserInfo) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Connection Issue" -> {
                stopShimmerEffect()
                binding?.layoutNetworkError?.root?.show()
            }
            "Server Issue" -> {
                stopShimmerEffect()
                binding?.layoutNetworkError?.root?.show()
            }
            "Another Login" -> {

                stopShimmerEffect()
                WarningDialog.Builder(requireContext(),
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        requireActivity().finish()
                        val intent = Intent(requireContext(),SplashActivity::class.java)
                        context?.startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(requireFragmentManager(), HomeFragment::class.simpleName)
            }
            "Denied" -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    requireActivity().finishAffinity()

                }).show(requireFragmentManager(), HomeFragment::class.simpleName)

            else -> showSnackBar(state.message)

        }
    }

    private fun renderOnSuccessHome(state: HomeViewState.OnSuccessHome) {
        stopShimmerEffect()
        LoadingProgressDialog.hideLoadingProgress()
        val newCategories = state.data.categories.subList(0,8)
        topCategoryAdapter?.setData(newCategories)

        // Handle near restaurants with pagination
        if (state.data.near_restaurant.isNotEmpty()) {
            binding?.tvNearYou?.show()
            nearByRestaurantList = state.data.near_restaurant.toMutableList()
            currentDisplayCount = minOf(5, state.data.near_restaurant.size)
            val initialData = state.data.near_restaurant.subList(0, currentDisplayCount)
            nearByIdRestAdapter?.updateData(initialData.toMutableList(), true)
        } else {
            binding?.tvNearYou?.gone()
            nearByRestaurantList = mutableListOf()
            nearByIdRestAdapter?.updateData(mutableListOf(), true)
        }

        /*if (state.data.recommend_restaurant.isNotEmpty()) {
            binding?.tvYouMightLike?.show()
            recommendedRestaurantAdapter?.setNewData(state.data.recommend_restaurant)

        } else binding?.tvYouMightLike?.gone()

        *//*if (state.data.recommend_restaurant.size > 3) tvRestaurantSeeMore.visibility = View.VISIBLE
        else tvRestaurantSeeMore.visibility = View.GONE*//*

        if (state.data.near_restaurant.isNotEmpty()) {
            binding?.tvNearYou?.show()
            nearByIdRestAdapter?.updateData(state.data.near_restaurant)
        } else binding?.tvNearYou?.gone()

        if (state.data.upanddown_ads.isNotEmpty()) {
            //binding?.adsViewContent?.show()
            binding?.coverViewPager?.visibility = View.VISIBLE
            setUpAdsOneSlider(state.data.upanddown_ads)
        } else {
            binding?.coverViewPager?.visibility = View.GONE
            //binding?.adsViewContent?.gone()
        }*/
        if (state.data.upanddown_ads.isNotEmpty()) {
            binding?.coverViewPager?.visibility = View.VISIBLE
            setUpAdsOneSlider(state.data.upanddown_ads)
        } else {
            binding?.coverViewPager?.visibility = View.GONE
        }

        PreferenceUtils.wishListCount.postValue(state.data.wishlist_count)
        state.data.customer?.let { PreferenceUtils.writeUserVO(it) }

        viewModel.zoneId = state.data.zone_id!!
        /*if (PreferenceUtils.readParcelZoneId() != state.data.zone_id) {
            PreferenceUtils.writeZoneId(viewModel.zoneId)
            //Clear Cache
            PreferenceUtils.clearCartData()
            //Fetch Currency
            viewModel.fetchCurrency()
        }*/
        if (PreferenceUtils.readZoneId() != state.data.zone_id) {
            stopShimmerEffect()
            PreferenceUtils.writeZoneId(viewModel.zoneId)
            //Clear Cache
            PreferenceUtils.clearCartData()
            //Fetch Currency
            viewModel.fetchCurrency()
        } else {
            stopShimmerEffect()
        }
    }
    private fun renderOnFailHome(state: HomeViewState.OnFailHome) {
        when (state.message) {
            "Server Error" -> {
                stopShimmerEffect()
                binding?.layoutNetworkError?.root?.show()
            }
            "Another Login" -> {
                stopShimmerEffect()
                WarningDialog.Builder(requireContext(),
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        requireActivity().finish()
                        val intent = Intent(requireContext(),SplashActivity::class.java)
                        context?.startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(requireFragmentManager(), HomeFragment::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    requireActivity().finishAffinity()

                }).show(requireFragmentManager(), HomeFragment::class.simpleName)

            else ->
            {
                stopShimmerEffect()
                state.message?.let { showSnackBar(it) }
            }
        }
    }

    private fun renderOnLoadingCurrency() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderOnSuccessCurrency(state: HomeViewState.OnSuccessCurrency) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.data.isNotEmpty()) PreferenceUtils.writeCurrencyVO(state.data.data[0])
        showCurrencyDialog(true, state.data.data)
    }

    private fun renderOnFailCurrency(state: HomeViewState.OnFailCurrency) {
        LoadingProgressDialog.hideLoadingProgress()
    }

    private fun renderOnLoadingOperateWishList() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }


    private fun renderOnSuccessOperateWishList(state: HomeViewState.OnSuccessOperateWishList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)
            viewModel.nearRestaurantLiveDataList.value?.forEach { it ->
                if (state.data.data.restaurant_id == it.restaurant_id) {
                    it.is_wish = state.data.message == "successfull customer wishlist create"
                    viewModel.nearRestaurantLiveDataList.observe(
                        viewLifecycleOwner
                    ) { nearByIdRestAdapter?.updateData(it) }
                }
            }
        }
    }

    private fun renderOnFailOperateWishList(state: HomeViewState.OnFailOperateWishList) {
        when (state.message) {
            "Connection Issue" -> {
                if (viewModel.isRefresh) binding?.swipeRefresh?.isRefreshing = false
                LoadingProgressDialog.hideLoadingProgress()
            }
            "Server Issue" -> {
                if (viewModel.isRefresh) binding?.swipeRefresh?.isRefreshing = false
                LoadingProgressDialog.hideLoadingProgress()
            }
            "Another Login" -> {
                if (viewModel.isRefresh) binding?.swipeRefresh?.isRefreshing = false
                LoadingProgressDialog.hideLoadingProgress()
                WarningDialog.Builder(requireContext(),
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        requireActivity().finish()
                        val intent = Intent(requireContext(),SplashActivity::class.java)
                        requireContext().startActivity(intent)
                    }).show(requireFragmentManager(), HomeFragment::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(requireContext(),
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    requireActivity().finishAffinity()

                }).show(requireFragmentManager(), HomeFragment::class.simpleName)

            "Failed" -> {
                showSnackBar("App Error")
            }
            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun setUpAdsOneSlider(data: MutableList<UpAndDownVO>) {
        var homeSlideAdapter : HomeSlideAdapter? = null
        val numberOfScreens = data.size
        homeSlideAdapter = HomeSlideAdapter(requireParentFragment(), numberOfScreens,data){
            //swipePagerWithCoverPopupView()
        }
        binding?.coverViewPager?.adapter = homeSlideAdapter
        binding?.coverViewPager?.isUserInputEnabled = true
    }

    private fun setUpRecommendedRestaurants() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.HORIZONTAL, false)
        binding?.rvRecommendRestaurant?.layoutManager = linearLayoutManager
        binding?.rvRecommendRestaurant?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        binding?.rvRecommendRestaurant?.setHasFixedSize(true)
        binding?.rvRecommendRestaurant?.isNestedScrollingEnabled = true
        recommendedRestaurantAdapter = RecommendedRestaurantAdapter(requireContext()){ data,str,pos ->
            when(str) {
                "root_content" -> {
                    PreferenceUtils.needToShow = false
                    PreferenceUtils.isBackground = false
                    /*requireActivity().startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to data.restaurant_id
                    )*/
                    val intent = Intent(requireContext(),RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant_id)
                    context?.startActivity(intent)
                }

            }
        }
        binding?.rvRecommendRestaurant?.adapter = recommendedRestaurantAdapter
    }

    private var currentDisplayCount = 5
    private val loadMoreThreshold = 5
    private var isLoadingMore = false

    private fun setUpNearByRestaurants() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        binding?.rvNearRestaurant?.layoutManager = linearLayoutManager
        binding?.rvNearRestaurant?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding?.rvNearRestaurant?.setHasFixedSize(true)
        binding?.rvNearRestaurant?.isNestedScrollingEnabled = true
        nearByIdRestAdapter = NearByIdRestAdapter(
            requireContext(),
            mutableListOf()
        ) { data, str, pos ->
            when(str) {
                "cv_rest" -> {
                    PreferenceUtils.needToShow = false
                    PreferenceUtils.isBackground = false
                    /*requireActivity().startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to data.restaurant_id
                    )*/
                    val intent = Intent(requireContext(),RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant_id)
                    context?.startActivity(intent)
                }
                "fav" -> {
                    if (PreferenceUtils.readUserVO()?.customer_id == 0) {
                        SuccessDialog.Builder(
                            requireContext(),
                            requireContext().resources.getString(R.string.login_message),
                            callback = {
                                PreferenceUtils.needToShow = false
                                PreferenceUtils.isBackground = false

                                //requireContext().startActivity<LoginActivity>()
                                val intent = Intent(requireContext(),LoginActivity::class.java)
                                context?.startActivity(intent)
                            })
                            .show(childFragmentManager, HomeFragment::class.simpleName)
                    } else {

                        PreferenceUtils.readUserVO().customer_id?.let {
                            viewModel.operateWishList(it, data.restaurant_id)
                        }
                    }

                }
            }
        }

        // Load more functionality
        binding?.rvNearRestaurant?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMoreData()

                /*if (isLoadingMore || dy <= 0) return

                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem + 5 >= totalItemCount &&
                    currentDisplayCount < (nearByRestaurantList?.size ?: 0)) {
                    loadMoreData()
                }*/
            }
        })

        binding?.rvNearRestaurant?.adapter = nearByIdRestAdapter

    }

    private fun loadMoreData() {
        nearByRestaurantList?.let { fullList ->
            if (currentDisplayCount >= fullList.size) return

            isLoadingMore = true

            // Show loading indicator if needed
            // binding?.progressBar?.visibility = View.VISIBLE

            // Simulate network delay (remove in production)
            Handler(Looper.getMainLooper()).postDelayed({
                val nextDisplayCount = minOf(currentDisplayCount + loadMoreThreshold, fullList.size)
                val newData = fullList.subList(currentDisplayCount, nextDisplayCount)

                nearByIdRestAdapter?.addMoreData(newData)
                currentDisplayCount = nextDisplayCount
                isLoadingMore = false

                // Hide loading indicator
                // binding?.progressBar?.visibility = View.GONE
            }, 1000) // Remove this delay in production
        }
    }



    override fun onResume() {
        super.onResume()
        checkGPS()
    }


    private fun setupConnectionErrorView(e: String) {
        when (e) {
            "Connection Issue" -> {
                binding?.layoutNetworkError?.root?.show()
            }
            "Server Issue" -> binding?.layoutNetworkError?.root?.show()
        }
    }



    //Show Currency Dialog
    @SuppressLint("NotifyDataSetChanged")
    private fun showCurrencyDialog(isCurrency: Boolean, data: MutableList<CurrencyVO>) {
        val dialogBinding = LayoutCurrencyZoneBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(dialogBinding.root)
        alertDialog.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            if (isCurrency) {
                dialogBinding.tvTitleCurrency.text = "Choose Currency"

                if (data.size > 1) {
                    dialogBinding.llLashioView.show()
                    dialogBinding.llMuseView.show()
                    dialogBinding.tvNameLashio.text = data[0].currency_symbol
                    dialogBinding.tvNameMuse.text = data[1].currency_symbol
                } else {
                    dialogBinding.llLashioView.show()
                    dialogBinding.llMuseView.gone()
                    dialogBinding.tvNameLashio.text = data[0].currency_symbol
                }
            } else {
                dialogBinding.tvTitleCurrency.text =  "Choose Region"
            }

            if(PreferenceUtils.readZoneId() == 1) {
                dialogBinding.rbtnLashioCheck.isChecked = true
                dialogBinding.rbtnLashioCheck.isChecked = false
            } else {
                dialogBinding.rbtnLashioCheck.isChecked = false
                dialogBinding.rbtnMuseCheck.isChecked = true
            }


            dialogBinding.rbtnLashioCheck.isChecked = lastSelected == 1
            dialogBinding.rbtnMuseCheck.isChecked = lastSelected == 2

            dialogBinding.btnConfirm.setOnClickListener {
                dismiss()
                viewModel.currencyVO = CurrencyVO(
                    currency_id = data[lastSelected].currency_id,
                    currency_name = data[lastSelected].currency_name,
                    currency_symbol = data[lastSelected].currency_symbol,
                    image = data[lastSelected].image,
                    position = lastSelected
                )
            }
            show()
        }
        /*dialogView = layoutInflater.inflate(R.layout.layout_dialog_parcel_state, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        alertDialog = builder.create().apply {
            if (isCurrency) dialogView?.tvTitle?.text =
                resources.getString(R.string.choose_currency_type)
            else dialogView?.tvTitle?.text = resources.getString(R.string.choose_region)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView?.rvServiceRegion?.bind(
                data, R.layout.layout_item_parcel_state
            ) { state: CurrencyVO, pos: Int ->
                this.radio_parcel_state.text = state.currency_name

                viewModel.currencyVO = CurrencyVO(
                    currency_id = data[lastSelected].currency_id,
                    currency_name = data[lastSelected].currency_name,
                    currency_symbol = data[lastSelected].currency_symbol,
                    image = data[lastSelected].image,
                    position = lastSelected
                )

                this.radio_parcel_state.isChecked = lastSelected == pos

                this.radio_parcel_state.setOnClickListener {
                    lastSelected = pos
                    viewModel.currencyVO = CurrencyVO(
                        currency_id = state.currency_id,
                        currency_name = state.currency_name,
                        currency_symbol = state.currency_symbol,
                        image = state.image,
                        position = lastSelected
                    )
                    dialogView?.rvServiceRegion?.adapter?.notifyDataSetChanged()
                }
            }?.layoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
            dialogView?.btnConfirm?.setOnClickListener {
                lastSelected = 0
                Preference.writeCurrencyId(viewModel.currencyVO)
                BaseActivity.isCurrencyUpdate.postValue(true)
                dismiss()
            }
            show()
        }*/
    }

    private fun setUpTopFoodCategory() {
        val linearLayoutManager =
            GridLayoutManager(FattyApp.getInstance(), 4)
        binding?.rvFoodCategory?.layoutManager = linearLayoutManager
        binding?.rvFoodCategory?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        binding?.rvFoodCategory?.setHasFixedSize(true)
        binding?.rvFoodCategory?.isNestedScrollingEnabled = true
        topCategoryAdapter = TopCategoryAdapter(mutableListOf())
        binding?.rvFoodCategory?.adapter = topCategoryAdapter
    }

    override fun onReceiveMapLatLng(latLng: LatLng) {
        binding?.tvUserAddress?.text = convertLatLangToAddress(
            latLng.latitude,latLng.longitude
        )
        if (PreferenceUtils.readUserVO().customer_id != 0) {
            PreferenceUtils.readUserVO().customer_id?.let { viewModel.updateUserInfo(it,latLng.latitude,latLng.longitude) }
        } else {
            PreferenceUtils.readUserVO().customer_id?.let { viewModel.fetchHome(it,"",latLng.latitude,latLng.longitude) }
        }

    }


}
