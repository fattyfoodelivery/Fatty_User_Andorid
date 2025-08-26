package com.orikino.fatty.ui.views.activities.rest_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.orikino.fatty.R
import com.orikino.fatty.adapters.RestaurantDetailFoodMenuAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityRestaurantDetailViewBinding
import com.orikino.fatty.databinding.LayoutDialogRemoveCartBinding
import com.orikino.fatty.databinding.LayoutDialogRestaurantFoodPhotoBinding
import com.orikino.fatty.databinding.LayoutLoginDialogBinding
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.MenuVO
import com.orikino.fatty.domain.responses.TempRestDetailResponse
import com.orikino.fatty.domain.view_model.RestaurantDetailViewModel
import com.orikino.fatty.domain.viewstates.RestaurantDetailViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.checkout.CheckOutActivity
import com.orikino.fatty.ui.views.activities.restaurant.RestaurantMoreInfoActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.delegate.AddOnDelegate
import com.orikino.fatty.ui.views.dialog.AddOnBottomSheetFragment
import com.orikino.fatty.ui.views.activities.rest_detail.PhotoViewActivity
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultMenuName
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.orikino.fatty.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class RestaurantDetailViewActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener,
    AddOnDelegate {

    private lateinit var binding: ActivityRestaurantDetailViewBinding

    private val viewModel: RestaurantDetailViewModel by viewModels()
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
    private var foodVO = FoodVO()
    private var viewType = ""
    private var menuList: MutableList<MenuVO> = mutableListOf()


    companion object {
        const val RESTAURANT_ID = "restaurant-id"
        const val GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending"
        const val VIEW_TYPE = "viewType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestaurantDetailViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        restaurant_id = intent.getIntExtra(RESTAURANT_ID, 0)

        subscribeUI()
        binding.appbarLayout.addOnOffsetChangedListener(this)

        showRestaurantMoreInfo()
        onBackPress()
        setUpFoodList()

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

    private fun showRestaurantMoreInfo() {
        binding.ivMoreInfo.setOnClickListener {
            if (isPlayStoreInstalled(this))
            /*startActivity<RestaurantMoreInfoActivity>(
                RestaurantMoreInfoActivity.RESTAURANT to Gson().toJson(
                    viewModel.foodMenuByRestaurantLiveDataList.value
                )
            )*/ {
                val intent = Intent(this, RestaurantMoreInfoActivity::class.java)
                intent.putExtra(
                    RestaurantMoreInfoActivity.RESTAURANT,
                    Gson().toJson(viewModel.foodMenuByRestaurantLiveDataList.value)
                )
                startActivity(intent)

            } else {
                val intent = Intent(this, RestaurantMoreInfoActivity::class.java)
                intent.putExtra(
                    RestaurantMoreInfoActivity.RESTAURANT,
                    Gson().toJson(viewModel.foodMenuByRestaurantLiveDataList.value)
                )
                startActivity(intent)
            }/*startActivity<RestaurantMoreInfoActivity>(
                RestaurantMoreInfoActivity.RESTAURANT to Gson().toJson(
                    viewModel.foodMenuByRestaurantLiveDataList.value
                ))*/

        }
        /*constraint_restaurant.setOnClickListener {
            if (isPlayStoreInstalled(this))
                startActivity<RestaurantMoreInfoActivity>(
                    RestaurantMoreInfoActivity.RESTAURANT to Gson().toJson(
                        viewModel.foodMenuByRestaurantLiveDataList.value
                    )
                )
            else startActivity<RestaurantMoreInfoMapBoxActivity>(
                RestaurantMoreInfoActivity.RESTAURANT to Gson().toJson(
                    viewModel.foodMenuByRestaurantLiveDataList.value
                )
            )
        }*/
    }

    private fun favourite(data: FoodMenuByRestaurantVO) {
        binding.imvFav.setOnClickListener {
            if (PreferenceUtils.readUserVO().customer_id == 0) {
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
                if (!data.is_wish) binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                else binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                viewModel.fetchOperateWishList(data.restaurant_id)
            }
        }
    }

    private fun showToolbar() {
        binding.tvTitleRestName.show()
        binding.tvTitleDistance.show()
    }

    private fun hideToolbar() {
        binding.tvTitleRestName.gone()
        binding.tvTitleDistance.gone()
    }

    private fun subscribeUI() {
        viewModel.fetchFoodRestMenuByRestId(restaurant_id)
        viewModel.viewState.observe(this) { render(it) }
        viewModel.foodMenuByRestaurantLiveDataList.observe(
            this
        ) {
            try {
                if (it.is_wish) binding.imvFav.setImageResource(R.drawable.ic_fav_filled_32dp)
                else binding.imvFav.setImageResource(R.drawable.ic_favorite_white)
                if (it.rating != 0.0) {
                    binding.imageView4.show()
                    binding.tvRatingCount.text = it.rating.toString()
                } else {
                    binding.imageView4.gone()
                    binding.tvRatingCount.text = resources.getString(R.string.no_review)
                }
                favourite(it)
                binding.tvTitleRestName.text = it.toDefaultRestaurantName()
                binding.tvTitleDistance.text = "${it.average_time} Min"
                binding.tvRestaurantName.text = it.toDefaultRestaurantName()
                binding.tvRestaurantAddress.text = it.restaurant_address
                binding.tvDurationDistance.text = "${it.average_time} Min"
                binding.imvRestaurant.load(
                    PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(it.restaurant_image)
                )
                restaurantInfO = FoodMenuByRestaurantVO(
                    restaurant_id = it.restaurant_id,
                    restaurant_name_mm = it.toDefaultRestaurantName()
                        ?: PreferenceUtils.readRestaurant()
                        !!.toDefaultRestaurantName(),
                    restaurant_address_mm = it.restaurant_address,//.toDefaultRestaurantAddress() ?: PreferenceUtils.readRestaurant()
                    //!!.toDefaultAddress(),
                    restaurant_image = it.restaurant_image
                        ?: PreferenceUtils.readRestaurant()?.restaurant_image
                )

            } catch (e: Exception) {
            }

            foodMenuAdapter?.setNewData(it.menu)
            //rvFoodMenu.update(it.menu)
        }

        viewModel.isEmptyCart.observe(this) {
            PreferenceUtils.writeAddToCart(it)
            if (PreferenceUtils.readAddToCart() == true) PreferenceUtils.readFoodOrderList()
                ?.let { it1 -> bindCartData(it1) }
            else {
                binding.flPlayNow.gone()
                //ll_view_cart.visibility = View.GONE
                //lottie.visibility = View.GONE
            }

        }

    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
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
                // TODO
                binding.flPlayNow.gone()
                //ll_view_cart.visibility = View.GONE
                //lottie.visibility = View.GONE
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun bindCartData(data: MutableList<CreateFoodVO>) {
        var totalPrice = 0.0
        if (data.isNullOrEmpty()) {
            binding.flPlayNow.gone()
            //lottie.visibility = View.GONE
        } else {
            data.forEach { totalPrice += it.food_price }
            binding.tvItemQty.text = "${data.size} ${resources.getString(R.string.item)}"
            binding.tvTotalAmt.text =
                "${totalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            binding.flPlayNow.show()
            //lottie.visibility = View.VISIBLE
            viewModel.isAnimate = true
            binding.flPlayNow.setOnClickListener {
                /*startActivity<CheckOutActivity>(
                    CheckOutActivity.LAT to PreferenceUtils.readUserVO().latitude,
                    CheckOutActivity.LNG to PreferenceUtils.readUserVO().longitude,
                    CheckOutActivity.ADDRESS_TYPE to ""
                )*/
                val intent = Intent(this, CheckOutActivity::class.java)
                intent.putExtra(CheckOutActivity.LAT, PreferenceUtils.readUserVO().latitude)
                intent.putExtra(CheckOutActivity.LNG, PreferenceUtils.readUserVO().longitude)
                intent.putExtra(CheckOutActivity.ADDRESS_TYPE, "")
                startActivity(intent)
            }
        }

    }

    private fun render(state: RestaurantDetailViewState) {
        when (state) {
            is RestaurantDetailViewState.OnLoadingRestFoodMenuByRestID -> renderOnLoadingFoodMenuByRestaurant()
            is RestaurantDetailViewState.OnSuccessRestFoodMenuByRestID -> renderOnSuccessFoodMenuByRestaurant(
                state
            )

            is RestaurantDetailViewState.OnFailRestFoodMenuByRestID -> renderOnFailFoodMenuByRestaurant(
                state
            )


            is RestaurantDetailViewState.OnLoadingOperateWishList -> renderOnLoadingOperateWishList()
            is RestaurantDetailViewState.OnSuccessOperateList -> renderOnSuccessOperateWishList(
                state
            )

            is RestaurantDetailViewState.OnFailOperateList -> renderOnFailOperateWishList(
                state
            )

            is RestaurantDetailViewState.OnLoadingFetchWishList -> renderOnLoadingFetchWishList()
            is RestaurantDetailViewState.OnSuccessFetchList -> renderOnSuccessFetchList(
                state
            )

            is RestaurantDetailViewState.OnFailFetchWishList -> renderOnFailFetchWishList(
                state
            )

            else -> {}
        }
    }

    private fun renderOnFailFetchWishList(state: RestaurantDetailViewState.OnFailFetchWishList) {
        CustomToast(this, state.message, true).createCustomToast()
    }

    private fun renderOnLoadingFetchWishList() {}

    private fun renderOnSuccessFetchList(state: RestaurantDetailViewState.OnSuccessFetchList) {
        if (state.data.success) {
            CustomToast(this, state.data.message, true).createCustomToast()
        }
    }

    private fun renderOnLoadingFoodMenuByRestaurant() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun startCartAnimation() {
        /*cart_lottie.playAnimation()
        Handler().postDelayed({
            cart_lottie.cancelAnimation()
        }, 1000)*/

    }

    private fun renderOnSuccessFoodMenuByRestaurant(state: RestaurantDetailViewState.OnSuccessRestFoodMenuByRestID) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            try {
                viewModel.foodMenuByRestaurantLiveDataList.postValue(state.data.data[0])
                state.data.data[0].menu.let { initTabLayout(it) }
                state.data.data[0].menu.let { initMediator(it) }
            }catch (e : Exception){
                e.printStackTrace()
            }

        }


    }

    private fun renderOnFailFoodMenuByRestaurant(state: RestaurantDetailViewState.OnFailRestFoodMenuByRestID) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Issue" -> showSnackBar("Server Error! ${state.message}")
            "Another Login" ->
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)
                    })
                    .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)

            "Denied" -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)

            else -> {
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.alert),
                    state.message!!,
                    "OK",
                    callback = { finish() })
                    .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)
            }

        }
    }

    private fun renderOnLoadingOperateWishList() {}

    private fun renderOnSuccessOperateWishList(state: RestaurantDetailViewState.OnSuccessOperateList) {
        if (state.data.success) {
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)
            PreferenceUtils.wishListRestaurant.postValue(
                Pair(
                    state.data.data.restaurant_id,
                    state.data.data.is_wish
                )
            )
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchWishList(
                    it,
                    PreferenceUtils.readUserVO().latitude ?: 0.0,
                    PreferenceUtils.readUserVO().longitude ?: 0.0
                )
            }
        }
    }

    private fun renderOnFailOperateWishList(state: RestaurantDetailViewState.OnFailOperateList) {
        when (state.message) {
            "Another Login" ->
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)
                    })
                    .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)

            "Denied" -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)

            else -> WarningDialog.Builder(
                this,
                resources.getString(R.string.alert),
                state.message,
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)

        }
    }

    private fun setUpFoodList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        binding.rvFoodMenu.layoutManager = linearLayoutManager
        binding.rvFoodMenu.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding.rvFoodMenu.setHasFixedSize(true)
        binding.rvFoodMenu.isNestedScrollingEnabled = true
        foodMenuAdapter = RestaurantDetailFoodMenuAdapter(this) { data, str, pos ->
            if (str == "image"){
                data.food_image?.let { imageUrl ->
                    if (imageUrl.isNotEmpty()) {
                        val photoViewActivity = PhotoViewActivity.newInstance(imageUrl)
                        startActivity(photoViewActivity)
                    } else {
                        Toast.makeText(this, "No image available for this item.", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "Image not specified for this item.", Toast.LENGTH_SHORT).show()
                }
            }else{
                addFoodToCart(data)
            }
        }

        binding.rvFoodMenu.adapter = foodMenuAdapter
    }

    private fun addFoodToCart(data: FoodVO) {

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
                .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)
        } else {
            when {
                viewModel.foodMenuByRestaurantLiveDataList.value?.distance!! > viewModel.foodMenuByRestaurantLiveDataList.value?.limit_distance!! -> {
                    showSnackBar(resources.getString(R.string.out_of_service))
                }

                viewModel.foodMenuByRestaurantLiveDataList.value?.distance!! <= viewModel.foodMenuByRestaurantLiveDataList.value?.limit_distance!! -> {
                    if (viewModel.foodMenuByRestaurantLiveDataList.value?.restaurant_emergency_status == 1) {
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
    }

    private fun setUpFoodByMenuRecyclerView() {
        /*rvFoodMenu.bind(
            listOf(),
            R.layout.layout_item_food_menu_by_restaurant_view
        ) { data: MenuVO, position: Int ->
            bindUI(data)
        }.layoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))*/
    }

    private fun View.expandOrNot() {
        /*if (this.tv.isExpanded) {
            this.ivToggle.setImageResource(R.drawable.ic_arrow_down_black_24dp)
            rvActiveOrder.visibility = View.GONE
        } else {
            this.ivToggle.setImageResource(R.drawable.ic_arrow_up)
            rvActiveOrder.visibility = View.VISIBLE
        }
        this.tv.toggle()*/
    }

    private fun View.bindUI(data: TempRestDetailResponse.Data.Menu) {
        /*exp_name.text = data.toDefaultMenuName()
        tv.expand()
        rvActiveOrder.bind(
            data.food,
            R.layout.layout_item_food_menu
        ) { data: FoodVO, pos: Int -> bindFood(data) }
        ivToggle.setOnClickListener { expandOrNot() }*/
    }

    @SuppressLint("SetTextI18n")
    private fun View.bindFood(data: FoodVO) {
        /*this.tv_food_prize.text = "${
            data.food_price?.toDouble()?.toThousandSeparator()
        } ${Preference.readCurrencyId().currency_symbol}"
        this.tv_food_name.text = data.toDefaultFoodName()
        this.imv_restaurant_photo.loadSlide(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/food/${data.food_image}" else "${Preference.PRODUCTION_URL}uploads/food/${data.food_image}")
        this.con.setOnClickListener { showRestaurantFoodPhoto(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/food/${data.food_image}" else "${Preference.PRODUCTION_URL}uploads/food/${data.food_image}") }
        if (data.food_emergency_status == 1) {
            this.tv_unavailable.visibility = View.VISIBLE
            btnAddItem.isEnabled = true
        } else {
            this.tv_unavailable.visibility = View.GONE
            btnAddItem.isEnabled = true
        }

        btnAddItem.setOnClickListener {

            if (Preference.readUserVO().customer_id == 0) {
                SuccessDialog.Builder(
                    context,
                    resources.getString(R.string.login_message),
                    callback = {
                        startActivity<LoginActivity>()
                        finish()
                    })
                    .show(supportFragmentManager, RestaurantDetailViewActivity::class.simpleName)
            } else {
                when {
                    viewModel.foodMenuByRestaurantLiveDataList.value?.distance!! > viewModel.foodMenuByRestaurantLiveDataList.value?.limit_distance!! -> {
                        showSnackBar(resources.getString(R.string.out_of_service))

                    }

                    viewModel.foodMenuByRestaurantLiveDataList.value?.distance!! <= viewModel.foodMenuByRestaurantLiveDataList.value?.limit_distance!! -> {
                        if (viewModel.foodMenuByRestaurantLiveDataList.value?.restaurant_emergency_status == 1) showSnackBar(
                            resources.getString(R.string.restaurant_unavailable)
                        )
                        else {
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
                                            })
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
        }*/
    }

    private fun showGameOverDialog(foodList: MutableList<CreateFoodVO>) {
        val dialogBind =
            LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(this@RestaurantDetailViewActivity))//layoutInflater.inflate(R.layout.layout_dialog_remove_cart, null)
        val builder = AlertDialog.Builder(this@RestaurantDetailViewActivity)
        builder.setView(dialogBind.root)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)

            dialogBind.btnClose.setOnClickListener {
                dismiss()
            }
            dialogBind.btnRemove.setOnClickListener {
                PreferenceUtils.writeRestaurant(restaurantInfO)
                PreferenceUtils.writeFoodOrderList(mutableListOf())
                PreferenceUtils.writeFoodOrderList(foodList)
                viewModel.isEmptyCart.postValue(true)
                dismiss()
            }
            show()
        }
    }

    private fun initTabLayout(menus: MutableList<MenuVO>) {
        binding.tabFoodMenu.removeAllTabs()
        for (menu in menus) {
            binding.tabFoodMenu.addTab(
                binding.tabFoodMenu.newTab().setText(menu.toDefaultMenuName())
            )
        }
    }

    private fun initMediator(menus: MutableList<MenuVO>) {
        try {
            menus.indices.toMutableList().let {
                TabbedListMediator(binding.rvFoodMenu, binding.tabFoodMenu, it, false).attach()
            }
            binding.tabFoodMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (menus.indices.toMutableList()
                            .last() == binding.tabFoodMenu.selectedTabPosition
                    ) binding.appbarLayout.setExpanded(false, true)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // TODO("Not yet implemented")
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //TODO("Not yet implemented")
                }
            })
        } catch (e: Exception) {
        }

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

    private fun showRestaurantFoodPhoto(url: String) {
        val dialogRestBind =
            LayoutDialogRestaurantFoodPhotoBinding.inflate(LayoutInflater.from(this@RestaurantDetailViewActivity))//layoutInflater.inflate(R.layout.layout_dialog_restaurant_food_photo, null)
        val builder = AlertDialog.Builder(this@RestaurantDetailViewActivity)
        builder.setView(dialogRestBind.root)
        alertDialogImage = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            dialogRestBind.imvRestaurantCover.load(url) {
                error(R.drawable.restaurant_default_img)
                placeholder(R.drawable.restaurant_default_img)
            }
            if (!isFinishing) show()
        }
    }

    private fun showNoItemDialog(title: String, message: String, delete: String) {
        val emptBind =
            LayoutLoginDialogBinding.inflate(LayoutInflater.from(this@RestaurantDetailViewActivity))//layoutInflater.inflate(R.layout.layout_login_dialog, null)
        val builder = AlertDialog.Builder(this@RestaurantDetailViewActivity)
        builder.setView(emptBind.root)
        alertDialogNotice = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            emptBind.tvTitle.text = title
            emptBind.tvDesc.text = message

            emptBind.btnLogin.text = delete
            emptBind.btnLogin.setOnClickListener {
                dismiss()
            }

            show()
        }
    }

    private fun onBackPress() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onAddToCart() {
        viewModel.isEmptyCart.postValue(PreferenceUtils.readAddToCart())
    }

    override fun onDeleteItem(foodList: MutableList<CreateFoodVO>) {
        showGameOverDialog(foodList)
    }

}