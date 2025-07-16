package com.joy.fattyfood.ui.views.activities.wish_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.joy.fattyfood.R
import com.joy.fattyfood.adapters.WishListAdapter
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityWishListBinding
import com.joy.fattyfood.domain.view_model.WishListViewModel
import com.joy.fattyfood.ui.views.activities.auth.login.LoginActivity
import com.joy.fattyfood.domain.viewstates.WishListViewState
import com.joy.fattyfood.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.joy.fattyfood.utils.Constants
import com.joy.fattyfood.utils.EqualSpacingItemDecoration
import com.joy.fattyfood.utils.GpsTracker
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WishListActivity : AppCompatActivity()  {

    lateinit var binding : ActivityWishListBinding

    private lateinit var wishListAdapter: WishListAdapter

    private val viewModel : WishListViewModel by viewModels()

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),WishListActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWishListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecommendedRestaurantRecycler()
        subscribeUI()
        //setupEmptyView()
        binding.swipeRefresh.setOnRefreshListener {

        PreferenceUtils.readUserVO()?.customer_id?.let { viewModel.fetchWishList(it) }

        }
        onBackPress()
    }

    override fun onStart() {
        super.onStart()
        PreferenceUtils.readUserVO()?.customer_id?.let { viewModel.fetchWishList(it) }
    }

    override fun onResume() {
        super.onResume()
        checkGPS()
    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
    }

    private fun subscribeUI() {

        viewModel.viewState.observe(this, { render(it) })
    }

    private fun render(state : WishListViewState) {
        when (state) {
            is WishListViewState.OnLoadingWishList -> renderOnLoadingWishList()
            is WishListViewState.OnSuccessWishList -> renderOnSuccessWishList(state)
            is WishListViewState.OnFailWishList -> renderOnFailWishList(state)

            is WishListViewState.OnLoadingOperateWishList -> renderOnLoadingOperateWishList()
            is WishListViewState.OnSuccessOperateWishList -> renderOnSuccessOperateWishList(state)
            is WishListViewState.OnFailOperateWishList -> renderOnFailOperateWishList(state)
        }
    }

    private fun renderOnSuccessWishList(state: WishListViewState.OnSuccessWishList) {
        binding.swipeRefresh.isRefreshing = false
        if (state.data.success) {
            state.data.data
            binding.tvTitle.text = "My Wishlist( ${state.data.data.size} )"
            wishListAdapter.setNewData(state.data.data)
        }
    }

    private fun renderOnLoadingWishList() {
        binding.swipeRefresh.isRefreshing = true
    }

    private fun renderOnSuccessOperateWishList(state: WishListViewState.OnSuccessOperateWishList) {
        binding.swipeRefresh.isRefreshing = false
        if (state.data.success) {
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)
            PreferenceUtils.wishListRestaurant.postValue(
                Pair(
                    state.data.data.restaurant_id,
                    state.data.data.is_wish
                )
            )
            PreferenceUtils.readUserVO().customer_id?.let { viewModel.fetchWishList(it) }
        }
    }

    private fun renderOnLoadingOperateWishList() {
        binding.swipeRefresh.isRefreshing = true
    }

    private fun renderOnFailOperateWishList(state: WishListViewState.OnFailOperateWishList) {

    }

    private fun renderOnFailWishList(state: WishListViewState.OnFailWishList) {
        when (state.message) {

            Constants.SERVER_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.ANOTHER_LOGIN -> {
                WarningDialog.Builder(this@WishListActivity,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        startActivity(LoginActivity.getIntent("wish_list"))
                    }).show(supportFragmentManager, WishListActivity::class.simpleName)
            }

            Constants.DENIED -> WarningDialog.Builder(this@WishListActivity,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, WishListActivity::class.simpleName)

            else -> showSnackBar(state.message)
        }
    }

    private fun onBackPress() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    private fun setUpRecommendedRestaurantRecycler() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        binding.rvWishList.layoutManager = linearLayoutManager
        binding.rvWishList.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding.rvWishList.setHasFixedSize(true)
        binding.rvWishList.isNestedScrollingEnabled = true
        wishListAdapter = WishListAdapter(this@WishListActivity) { data,str,pos ->
            when(str) {
                "root" -> {
                    PreferenceUtils.needToShow = false
                    PreferenceUtils.isBackground = false
                    /*startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to data.restaurant_id,
                        RestaurantDetailViewActivity.VIEW_TYPE to "wish_list"
                    )*/
                    val intent = Intent(this@WishListActivity, RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID, data.restaurant_id)
                    intent.putExtra(RestaurantDetailViewActivity.VIEW_TYPE, "wish_list")

                }
                "fav" -> {
                    PreferenceUtils.readUserVO().customer_id?.let { viewModel.operateWishList(it,data.restaurant_id) }
                }
            }
        }
        binding.rvWishList.adapter = wishListAdapter
    }




}