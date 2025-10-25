package com.orikino.fatty.ui.views.activities.restaurant

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.orikino.fatty.R
import com.orikino.fatty.adapters.RestAviableTimeAdapterder
import com.orikino.fatty.adapters.RestDetailReviewListAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityRestaurantMoreInfoBinding
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.responses.RestDetailReviewListResponse
import com.orikino.fatty.domain.view_model.RestaurantDetailViewModel
import com.orikino.fatty.domain.viewstates.RestaurantDetailViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.FattyMap
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultRestaurantAddress
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.orikino.fatty.utils.helper.toHourMinuteString
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import java.lang.Exception
import java.util.Locale


@AndroidEntryPoint
class RestaurantMoreInfoActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    private val viewModel : RestaurantDetailViewModel by viewModels()
    private lateinit var _binding : ActivityRestaurantMoreInfoBinding

    private var restAvailableTimeAdapter : RestAviableTimeAdapterder? = null

    private var restDetailReviewListAdapter: RestDetailReviewListAdapter? = null
    companion object {
        const val RESTAURANT_ID = "restaurant_id"
        const val RESTAURANT = "restaurant"
    }

    private lateinit var fattyMap: FattyMap
    private var restaurant = FoodMenuByRestaurantVO()
    private var restaurantGson = ""
    private var isCheckAbout = true
    private var restaurant_id : Int = 0
    private var page : Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRestaurantMoreInfoBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        restaurant_id = intent.getIntExtra(RESTAURANT_ID,0)
        restaurantGson = intent.getStringExtra(RESTAURANT).toString()
        restaurant = Gson().fromJson(
            restaurantGson,
            object : TypeToken<FoodMenuByRestaurantVO>() {}.type
        )

        _binding.appBarLayout.addOnOffsetChangedListener(this)
        setUpMap()
        convertLatLangToAddress()

        onBackPress()

        setUpAboutReview()
        bindRestaurantInfo()
        setUpAvailableTimeRecycler()
        restaurant.available_time.let { restAvailableTimeAdapter?.setNewData(it) }
        setUpReviewList()
        _binding.rbtAbout.isChecked = true
        checkAboutView()

        setUpObserver()
    }


    private fun setUpAboutReview() {
        _binding.rbgStateCheck.setOnCheckedChangeListener { radioGroup, id ->
            if (id == R.id.rbt_about) {
                _binding.rbtAbout.isChecked = true
                _binding.rbtReview.isChecked = false
                _binding.rlReview.gone()
                checkAboutView()
            } else {
                _binding.rlReview.show()
                _binding.rbtAbout.isChecked = false
                _binding.rbtReview.isChecked = true
                viewModel.fetchRestReviewList(restaurant.restaurant_id,1)
                checkReview()
            }
        }
    }

    private fun checkAboutView() {
        _binding.rbtAbout.background = ContextCompat.getDrawable(this,R.drawable.radio_selected_bg)
        _binding.rbtAbout.setTextColor(ContextCompat.getColor(this,R.color.white))
        _binding.rbtReview.background = ContextCompat.getDrawable(this,R.drawable.bg_tab_unselected)
        _binding.rbtReview.setTextColor(ContextCompat.getColor(this,R.color.text_primary_01))
        showAboutUpdateUI()
    }

    private fun checkReview() {
        _binding.rbtAbout.background = ContextCompat.getDrawable(this,R.drawable.bg_tab_unselected)
        _binding.rbtAbout.setTextColor(ContextCompat.getColor(this,R.color.text_primary_01))
        _binding.rbtReview.background = ContextCompat.getDrawable(this,R.drawable.radio_selected_bg)
        _binding.rbtReview.setTextColor(ContextCompat.getColor(this,R.color.white))
        showReviewUpdateUI()
    }

    private fun showAboutUpdateUI() {
        _binding.rlAbout.show()
        _binding.rlReview.gone()
    }

    private fun showReviewUpdateUI() {
        _binding.rlAbout.gone()
        _binding.rlReview.show()
        viewModel.fetchRestReviewList(restaurant.restaurant_id,1)
    }
    private fun setUpObserver() {
        viewModel.viewState.observe(this) { render(it) }
    }

    private fun render(state : RestaurantDetailViewState) {
        when(state) {
            is RestaurantDetailViewState.OnSuccessRestReviewList -> renderOnSuccessRestaurantList(state)
            is RestaurantDetailViewState.OnFailRestRestReviewList -> renderOnFailRestaurantList(state)

            is RestaurantDetailViewState.OnLoadingOperateWishList -> renderOnLoadingOperateWishList()
            is RestaurantDetailViewState.OnSuccessOperateList -> renderOnSuccessOperateWishList(
                state
            )
            is RestaurantDetailViewState.OnFailOperateList -> renderOnFailOperateWishList(
                state
            )
            else -> {}
        }
    }

    private fun renderOnLoadingOperateWishList() {
        LoadingProgressDialog.showLoadingProgress(this)
    }
    private fun renderOnFailOperateWishList(state: RestaurantDetailViewState.OnFailOperateList) {
        LoadingProgressDialog.hideLoadingProgress()
        showSnackBar(state.message)
    }
    private fun renderOnSuccessOperateWishList(state: RestaurantDetailViewState.OnSuccessOperateList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)
            PreferenceUtils.wishListRestaurant.postValue(
                Pair(
                    state.data.data.restaurant_id,
                    state.data.data.is_wish
                )
            )
            viewModel.fetchFoodRestMenuByRestId(restaurant_id)
        }
    }

    private fun renderOnFailRestaurantList(state: RestaurantDetailViewState.OnFailRestRestReviewList) {
        when (state.message) {
            "Server Issue" -> showSnackBar("Server Error! ${state.message}")
            "Another Login" ->
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    .show(supportFragmentManager, RestaurantMoreInfoActivity::class.simpleName)

            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, RestaurantMoreInfoActivity::class.simpleName)

            else -> {
                showSnackBar(state.message!!)
            }

        }

    }


    private fun renderOnSuccessRestaurantList(state: RestaurantDetailViewState.OnSuccessRestReviewList) {
        _binding.rlReview.show()
        if ( state.data.success == true) {
            _binding.rbtReview.isChecked = true
            if (state.data.data?.reviews?.data.isNullOrEmpty()) {
                _binding.llContentReview.visibility = View.GONE
                _binding.llNoReview.show()

            } else {
                _binding.llNoReview.gone()
                _binding.llContentReview.visibility = View.VISIBLE
                state.data.data?.let { bindReviewUI(it) }

            }

        }
    }

    private fun bindReviewUI(data : RestDetailReviewListResponse.Data) {
        _binding.tvReviewCount.text = "${data.rating}"
        _binding.tvTitleCount.text = "${"Ratings $ Reviews"} ${data.rating_count}"
        //_binding.ratingBar.rating = data.rating.toFloat()
        data.reviews?.data?.let { restDetailReviewListAdapter?.setNewData(it) }
    }
    private fun setUpReviewList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        _binding.rvReview.layoutManager = linearLayoutManager
        _binding.rvReview.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        _binding.rvReview.setHasFixedSize(true)
        _binding.rvReview.isNestedScrollingEnabled = true
        restDetailReviewListAdapter = RestDetailReviewListAdapter(this)

        _binding.rvReview.adapter = restDetailReviewListAdapter
    }


    override fun onResume() {
        super.onResume()
        checkGPS()
    }

    private fun checkGPS(){
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        } else {
        }
    }

    private fun onBackPress() {
        _binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun bindRestaurantInfo() {
        try {
            if (restaurant.is_wish) _binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            else _binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            favourite()
            //tb_restaurant_name.text = it.toDefaultRestaurantName()
            //tb_delivery_time.text = "${it.average_time} Min"
            _binding.tvAddress.text = restaurant.toDefaultRestaurantAddress()
            _binding.tvTitleRestName.text = restaurant.toDefaultRestaurantName()
            _binding.tvTitleDistance.text = restaurant.average_time.toString()
                .plus("mins ・ ").plus(restaurant.distance).plus("km")
            // TODO required api response
            _binding.tvRatingCount.text = restaurant.rating.toString()
            _binding.tvRestaurantName.text = restaurant.toDefaultRestaurantName()
            _binding.tvRestaurantAddress.text = restaurant.restaurant_address_en
            _binding.tvDurationDistance.text = "${restaurant.distance_time.toHourMinuteString()}・${restaurant.distance} km"
//                restaurant.average_time.toString()
//                .plus("mins ・ ").plus(restaurant.distance).plus("km")
            _binding.imvRestaurant.load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(restaurant.restaurant_image))

            //restAvailableTimeAdapter?.setNewData(restaurant.available_time)

            if (restaurant.rating == 0.0) {
                _binding.llNoReview.show()
            } else {
                _binding.llNoReview.gone()
                _binding.tvTitleCount.text = "Ratings & Reviews ( ${restaurant.rating} )"
                _binding.tvReviewCount.text = "${restaurant.rating} "
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //foodMenuAdapter?.setNewData(restaurant.menu)
    }

    private fun favourite() {
        _binding.imvFav.setOnClickListener {
            if (PreferenceUtils.readUserVO()?.customer_id == 0) {
                SuccessDialog.Builder(
                    applicationContext,
                    resources.getString(R.string.login_message),
                    callback = {
                        //startActivity<LoginActivity>()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)
            } else {
                if (!restaurant.is_wish) _binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                else _binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                viewModel.fetchOperateWishList(restaurant_id)
            }
        }
    }
    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            fattyMap = FattyMap(this, it)
            fattyMap.drawMaker(
                LatLng(restaurant.restaurant_latitude, restaurant.restaurant_longitude),
                R.layout.item_restaurant_pinpoint
            )
            fattyMap.animateCamera(
                LatLng(
                    restaurant.restaurant_latitude,
                    restaurant.restaurant_longitude
                )
            )
        }
    }

    private fun convertLatLangToAddress() {
        var addresses: List<Address> = listOf()
        val locations = Location("")
        locations.latitude = restaurant.restaurant_latitude
        locations.longitude = restaurant.restaurant_longitude
        var ss = ""
        try {
            val geocoder = Geocoder(this, Locale.US)
            addresses = geocoder.getFromLocation(locations.latitude, locations.longitude, 1)!!
            ss = addresses[0].getAddressLine(0)
            try {
                showAddress(ss)

            } catch (e: kotlin.Exception) {
            }
        } catch (e: kotlin.Exception) {
        }

    }

    private fun showAddress(address: String) {
        try {
            _binding.tvAddress.text = address
        } catch (e: Exception) {
        }
    }

    private fun setUpAvailableTimeRecycler() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        _binding.rvRestAvailableTime.layoutManager = linearLayoutManager
        _binding.rvRestAvailableTime.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        _binding.rvRestAvailableTime.setHasFixedSize(true)
        _binding.rvRestAvailableTime.isNestedScrollingEnabled = true
        restAvailableTimeAdapter = RestAviableTimeAdapterder(this) { data,str,pos ->
        }

        _binding.rvRestAvailableTime.adapter = restAvailableTimeAdapter
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}