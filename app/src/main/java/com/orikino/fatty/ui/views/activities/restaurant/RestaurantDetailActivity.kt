package com.orikino.fatty.ui.views.activities.restaurant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.orikino.fatty.R
import com.orikino.fatty.adapters.RestaurantDetailFoodMenuAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityRestaurantDetailBinding
import com.orikino.fatty.databinding.LayoutDialogRemoveCartBinding
import com.orikino.fatty.databinding.LayoutDialogRestaurantFoodPhotoBinding
import com.orikino.fatty.databinding.LayoutLoginDialogBinding
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.MenuVO
import com.orikino.fatty.domain.view_model.RestaurantViewModel
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.ui.views.fragments.rest_more_info.FoodAddOnBottomSheetFragment
import com.orikino.fatty.domain.viewstates.RestaurantViewState
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.delegate.AddOnDelegate
import com.orikino.fatty.ui.views.dialog.AddOnBottomSheetFragment
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.delegate.OnTapItemIdAndString
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultMenuName
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class RestaurantDetailActivity : AppCompatActivity(), OnTapItemIdAndString, AppBarLayout.OnOffsetChangedListener,
    AddOnDelegate {

    private lateinit var _binding: ActivityRestaurantDetailBinding

    private var foodMenuAdapter: RestaurantDetailFoodMenuAdapter? = null

    private var restaurant_id: Int = 0
    private var dialogView: View? = null
    private var alertDialog: AlertDialog? = null
    private var dialogViewNotice: View? = null
    private var alertDialogNotice: AlertDialog? = null
    private var restaurantInfO = FoodMenuByRestaurantVO()
    private var dialogViewImage: View? = null
    private var alertDialogImage: AlertDialog? = null
    private var verticalOffsets: Int = 0
    private val viewModel: RestaurantViewModel by viewModels()

    var foodVO = FoodVO()

    private var viewType = ""

    companion object {

        const val RESTAURANT_ID = "restaurant-id"
        const val GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending"
        const val VIEW_TYPE = "viewType"

        fun getIntent(restId: Int, viewType: String): Intent {
            val intent = Intent(FattyApp.getInstance(), RestaurantDetailActivity::class.java)
            intent.putExtra(RESTAURANT_ID, restId)
            intent.putExtra(VIEW_TYPE, viewType)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //window.statusBarColor = ContextCompat.getColor(this, R.color.fattyPrimary)

        _binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        restaurant_id = intent.getIntExtra(RESTAURANT_ID, 0)

        viewType = intent.getStringExtra(VIEW_TYPE).toString()

        //Toast.makeText(this, "reach", Toast.LENGTH_SHORT).show()

        setUpObserver()
        setUpFoodByMenuRecyclerView()
        _binding.appbarLayout.addOnOffsetChangedListener(this)
        showRestaurantMoreInfo()
        onBackPress()
    }

    override fun onResume() {
        super.onResume()
        checkGPS()
        viewModel.isAnimate = false
        viewModel.isEmptyCart.observe(this) {
            PreferenceUtils.writeAddToCart(it)
            if (PreferenceUtils.readAddToCart() == true) {
                if (viewModel.isAnimate) startCartAnimation()
                PreferenceUtils.readFoodOrderList()?.let { it1 -> bindCartData(it1) }
            } else {
                //_binding.flPlayNow.gone()
                // lottie.visibility = View.GONE
            }
        }
    }

    private fun startCartAnimation() {
    }

    private fun bindCartData(data: MutableList<CreateFoodVO>) {
        var totalPrice = 0.0
        if (data.isNullOrEmpty()) {
            //_binding.flPlayNow.gone()
            // lottie.visibility = View.GONE
        } else {
            /*data.forEach { totalPrice += it.food_price }
            _binding.tvItemQty.text = "${data.size} tems"
            _binding.tvTotalAmt.text = "${totalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrencyId()?.currency_symbol}"
            _binding.flPlayNow.show()
            // lottie.visibility = View.VISIBLE
            viewModel.isAnimate = true
            _binding.btnPlayNow.setOnClickListener {
                startActivity(CheckOutActivity.getIntent(0.0, 0.0, ""))
                *//*startActivity<CartActivity>(
                    CartActivity.LAT to Preference.readUserVO().latitude,
                    CartActivity.LNG to Preference.readUserVO().longitude,
                    CartActivity.ADDRESS_TYPE to ""
                )*//*
            }*/
        }
    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
    }

    private fun showRestaurantMoreInfo() {
       /* _binding.ivMoreInfo.setOnClickListener {
            startActivity<RestaurantMoreInfoActivity>(
                *//*RestaurantMoreInfoActivity.RESTAURANT to Gson().toJson(
                    viewModel.foodMenuByRestaurantLiveDataList.value
                )*//*
            )
        }*/
    }

    private fun showToolbar() {
        //_binding.tvTitleRestName.gone()
        //_binding.tvTitleDistance.gone()
    }

    private fun hideToolbar() {
        //_binding.tvTitleRestName.gone()
        //_binding.tvTitleDistance.gone()
    }
    private fun onBackPress() {
        //_binding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (viewType == "splash") {
            startActivity(MainActivity.getIntent(this))
        } else if (viewType == "home") {
            finish()
        }
    }

    private fun setUpObserver() {

        PreferenceUtils.readUserVO().customer_id?.let {
            viewModel.fetchFoodMenuByRestaurant(restaurantInfO.restaurant_id,it,PreferenceUtils.readUserVO().latitude?:0.0,PreferenceUtils.readUserVO().longitude?:0.0)
        }
        viewModel.viewState.observe(
            this,
            Observer {
                render(it)
            }
        )

        viewModel.isEmptyCart.observe(this) {
            PreferenceUtils.writeAddToCart(it)
            if (PreferenceUtils.readAddToCart() == true) {
                PreferenceUtils.readFoodOrderList()
                    ?.let { it1 -> bindCartData(it1) }
            } else {
               // _binding.flPlayNow.gone()
                // lottie.visibility = View.GONE
            }
        }

        /*viewModel.foodMenuByRestaurantLiveDataList.observe(this) {
            try {
                if (it.is_wish) {
                    _binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                } else {
                    _binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                }
                favourite(it)
                // tb_restaurant_name.text = it.toDefaultRestaurantName()
                // tb_delivery_time.text = "${it.average_time} Min"
                _binding.tvTitleRestName.text = it.toDefaultRestaurantName()
                _binding.tvTitleDistance.text = it.average_time.toString()
                    .plus("mins ・ ").plus(it.distance).plus("km")
                // TODO required api response
                _binding.tvRatingCount.text = it.rating.toString()
                _binding.tvRestaurantName.text = it.toDefaultRestaurantName()
                _binding.tvRestaurantAddress.text = it.restaurant_address_en
                _binding.tvDurationDistance.text = it.average_time.toString()
                    .plus("mins ・ ").plus(it.distance).plus("km")
                _binding.imvRestaurant.load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(it.restaurant_image)) {
                    error(R.drawable.restaurant_default_img)
                    placeholder(R.drawable.restaurant_default_img)
                }
                restaurantInfO = FoodMenuByRestaurantVO(
                    restaurant_id = it.restaurant_id,
                    restaurant_name_mm = it.toDefaultRestaurantName() ?: PreferenceUtils.readRestaurant()
                        ?.toDefaultRestaurantName(),
                    restaurant_address_mm = it.toDefaultAddress() ?: PreferenceUtils.readRestaurant()
                        ?.toDefaultAddress(),
                    restaurant_image = it.restaurant_image
                        ?: PreferenceUtils.readRestaurant()?.restaurant_image
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (it.menu != null) {
                foodMenuAdapter?.setNewData(it.menu)
            }
        }*/
    }

    private fun render(state: RestaurantViewState) {
        when (state) {
            is RestaurantViewState.OnLoadingFoodMenuByRestaurant -> renderOnLoadingFoodMenuByRestaurant()
            is RestaurantViewState.OnSuccessFoodMenuByRestaurant -> renderSuccessFoodMenuByRestaurant(state)
            is RestaurantViewState.OnFailFoodMenuByRestaurant -> renderOnFailFoodMenuByRestaurant(state)
        }
    }



    private fun renderOnLoadingFoodMenuByRestaurant() {

    }
    private fun renderOnFailFoodMenuByRestaurant(state: RestaurantViewState.OnFailFoodMenuByRestaurant) {
        when (state.message) {

            "Another Login" -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        /*startActivity<LoginActivity>(
                            LoginActivity.FROM to "rest_detail"
                        )*/
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("from", "rest_detail")
                        startActivity(intent)

                    }).show(supportFragmentManager, HomeFragment::class.simpleName)
            }

            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                   finishAffinity()

                }).show(supportFragmentManager, HomeFragment::class.simpleName)

           "Failed" -> {
                showSnackBar(state.message)
            }
            else -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.alert),
                    resources.getString(R.string.maintain_msg),
                    "OK",
                    callback = {
                        finish()

                    }).show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)
            }
        }
    }
    private fun renderSuccessFoodMenuByRestaurant(state: RestaurantViewState.OnSuccessFoodMenuByRestaurant) {
        println("reach success")
        if (state.data.success) {
            println("reach this two")
            //viewModel.foodMenuByRestaurantLiveDataList.postValue(state.data.data)
            Toast.makeText(this, "${state.data.message}", Toast.LENGTH_SHORT).show()
            //state.data.data?.let { bindRestaurantInfo(it) }
            println("resdetailll datatatat $restaurantInfO")

            //state.data.data?.menu?.let { initTabLayout(it) }
            //tate.data.data?.menu?.let { initMediator(it) }
        }
    }

    private fun bindRestaurantInfo(it: FoodMenuByRestaurantVO) {
        try {
            _binding.tvRatingCount.text = it.rating.toString()
            _binding.tvRestaurantName.text = it.toDefaultRestaurantName()
            _binding.tvRestaurantAddress.text = it.restaurant_address_en
        }catch (e : Exception) {
            e.printStackTrace()
        }
        /*try {
            if (it.is_wish) {
                _binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
            } else {
                _binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
            }
            favourite(it)
            // tb_restaurant_name.text = it.toDefaultRestaurantName()
            // tb_delivery_time.text = "${it.average_time} Min"
            _binding.tvTitleRestName.text = it.toDefaultRestaurantName()
            _binding.tvTitleDistance.text = it.average_time.toString()
                .plus("mins ・ ").plus(it.distance).plus("km")
            // TODO required api response
            _binding.tvRatingCount.text = it.rating.toString()
            _binding.tvRestaurantName.text = it.toDefaultRestaurantName()
            _binding.tvRestaurantAddress.text = it.restaurant_address_en
            _binding.tvDurationDistance.text = it.average_time.toString()
                .plus("mins ・ ").plus(it.distance).plus("km")
            _binding.imvRestaurant.load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(it.restaurant_image)) {
                error(R.drawable.restaurant_default_img)
                placeholder(R.drawable.restaurant_default_img)
            }
            restaurantInfO = FoodMenuByRestaurantVO(
                restaurant_id = it.restaurant_id,
                restaurant_name_mm = it.toDefaultRestaurantName() ?: PreferenceUtils.readRestaurant()
                    ?.toDefaultRestaurantName(),
                restaurant_address_mm = it.toDefaultAddress() ?: PreferenceUtils.readRestaurant()
                    ?.toDefaultAddress(),
                restaurant_image = it.restaurant_image
                    ?: PreferenceUtils.readRestaurant()?.restaurant_image
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (it.menu != null) {
            foodMenuAdapter?.setNewData(it.menu)
        }*/
    }

    private fun initTabLayout(menus: MutableList<MenuVO>) {
        _binding.tabFoodMenu.removeAllTabs()
        for (menu in menus) {
            _binding.tabFoodMenu.addTab(_binding.tabFoodMenu.newTab().setText(menu.toDefaultMenuName()))
        }
    }

    private fun initMediator(menus: MutableList<MenuVO>) {
        try {
            menus.indices.toMutableList().let {
                TabbedListMediator(_binding.rvFoodMenu, _binding.tabFoodMenu, it, false).attach()
            }
            _binding.tabFoodMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (menus.indices.toMutableList()
                            .last() == _binding.tabFoodMenu.selectedTabPosition

                    ) {
                        _binding.appbarLayout.setExpanded(false, true)
                    }
                    _binding.tabFoodMenu.background = ContextCompat.getDrawable(baseContext, R.drawable.tab_background_selector)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun favourite(data: FoodMenuByRestaurantVO) {
       /* _binding.imvFav.setOnClickListener {
            if (PreferenceUtils.readUserVO()?.customer_id == 0) {
                SuccessDialog.Builder(
                    applicationContext,
                    resources.getString(R.string.login_message),
                    callback = {
                        startActivity(LoginActivity.getIntent("rest_detail"))
                        finish()
                    }
                )
                    .show(supportFragmentManager, RestaurantDetailActivity::class.simpleName)
            } else {
                if (!data.is_wish) {
                   // _binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                } else {
                   // _binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                }
                // TODO
                // viewModel.operateWishList(data.restaurant_id)
            }
        }*/
    }

    private fun exitCartRemoveConfirm() {
        ConfirmDialog.Builder(
            this,
            "Adding this menu will empty your cart. Add anyway?",
            "You have menu from another restaurant that haven’t been checked out.",
            "Add Anyway",
            callback = {
                // addFoodToCart()
            }
        ).show(supportFragmentManager, RestaurantDetailActivity::javaClass.name)
    }

    /*private fun setUpFoodList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        _binding.rvFoodCategory.layoutManager = linearLayoutManager
        _binding.rvFoodCategory.addItemDecoration(
            EqualSpacingItemDecoration(
                spacing = 24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        _binding.rvFoodCategory.setHasFixedSize(true)
        _binding.rvFoodCategory.isNestedScrollingEnabled = true
        restaurantDetailFoodAdapter = RestaurantDetailFoodAdapter(FattyApp.getInstance()) {

        }
        _binding.rvFoodCategory.adapter = restaurantDetailFoodAdapter
    }*/

    private fun addFoodToCart(data: FoodVO) {
        if (PreferenceUtils.readUserVO()?.customer_id == 0) {
            SuccessDialog.Builder(
                this,
                resources.getString(R.string.login_message),
                callback = {
                    startActivity(LoginActivity.getIntent("rest_detail"))
                    finish()
                }
            )
                .show(supportFragmentManager, RestaurantDetailActivity::class.simpleName)
        } else {
            when {
                restaurantInfO?.distance!! > restaurantInfO?.limit_distance!! -> {
                    showSnackBar(resources.getString(R.string.out_of_service))
                }

                restaurantInfO?.distance!! <= restaurantInfO?.limit_distance!! -> {
                    println("foodemergencystatattata ${restaurantInfO?.restaurant_emergency_status}")
                    if (restaurantInfO?.restaurant_emergency_status == 1) {
                        showSnackBar(
                            resources.getString(R.string.restaurant_unavailable)
                        )
                    } else {
                        when (data.food_emergency_status) {
                            1 -> {
                                showNoItemDialog(
                                    resources.getString(R.string.notice),
                                    resources.getString(R.string.notice_message),
                                    "OK"
                                )
                            }
                            else -> {
                                val bottomSheetFragment =
                                    AddOnBottomSheetFragment.newInstance(
                                        false,
                                        restaurantInfO,
                                        data,
                                        data.sub_item,
                                       this
                                    )
                                bottomSheetFragment.show(
                                    supportFragmentManager,
                                    bottomSheetFragment.tag
                                )
                            }
                        }
                    }
                }
            }
        }

        /*val bottomSheetFragment =
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
                }
            )
        bottomSheetFragment.show(
            supportFragmentManager,
            bottomSheetFragment.tag
        )*/
    }

    private fun showRestaurantFoodPhoto(url: String) {
        val binding = LayoutDialogRestaurantFoodPhotoBinding.inflate(
            LayoutInflater.from(this)
        ) // layoutInflater.inflate(R.layout.layout_dialog_restaurant_food_photo, null)
        val builder = AlertDialog.Builder(this@RestaurantDetailActivity)
        builder.setView(binding.root)
        alertDialogImage = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            binding?.imvRestaurantCover?.load(url)
            if (!isFinishing) show()
        }
    }

    private fun showNoItemDialog(title: String, message: String, delete: String) {
        val binding = LayoutLoginDialogBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this@RestaurantDetailActivity)
        builder.setView(binding.root)
        alertDialogNotice = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            binding?.tvTitle?.text = title
            binding?.tvDesc?.text = message

            binding?.btnLogin?.text = delete
            binding?.btnLogin?.setOnClickListener {
                dismiss()
            }

            show()
        }
    }
    private fun setUpFoodByMenuRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        _binding.rvFoodMenu.layoutManager = linearLayoutManager
        _binding.rvFoodMenu.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        _binding.rvFoodMenu.setHasFixedSize(true)
        _binding.rvFoodMenu.isNestedScrollingEnabled = true
        foodMenuAdapter = RestaurantDetailFoodMenuAdapter(this) {_,_,_->
            //addFoodToCart(it)
        }

        _binding.rvFoodMenu.adapter = foodMenuAdapter
    }

    private fun showGameOverDialog(foodList: MutableList<CreateFoodVO>) {
        val binding = LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this@RestaurantDetailActivity)
        builder.setView(binding.root)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)

            binding?.btnClose?.setOnClickListener {
                dismiss()
            }
            binding?.btnRemove?.setOnClickListener {
                PreferenceUtils.writeRestaurant(restaurantInfO)
                PreferenceUtils.writeFoodOrderList(mutableListOf())
                PreferenceUtils.writeFoodOrderList(foodList)
                viewModel.isEmptyCart.postValue(true)
                dismiss()
            }
            show()
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        verticalOffsets = verticalOffset
        when {
            abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                showToolbar()
            } // collapse
            verticalOffset == 0 -> {
                _binding.imvRestaurant.alpha = 1f
                hideToolbar()
            } // fully expand
            else -> {
                _binding.imvRestaurant.alpha = 0.5f
                hideToolbar()
            } // still collapse or expand
        }
    }

    override fun onTapItem(id: Int, str: String) {
        //showRestaurantFoodPhoto(str)
    }

    override fun onAddToCart() {

    }

    override fun onDeleteItem(foodList: MutableList<CreateFoodVO>) {
    }
}
