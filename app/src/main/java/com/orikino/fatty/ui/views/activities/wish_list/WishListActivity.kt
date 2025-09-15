package com.orikino.fatty.ui.views.activities.wish_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.R
import com.orikino.fatty.adapters.WishListAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityWishListBinding
import com.orikino.fatty.domain.view_model.WishListViewModel
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.domain.viewstates.WishListViewState
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
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

        binding.emptyView.emptyMessage.text = getString(R.string.no_data_available)
        binding.emptyView.emptyMessageDes.text = ""

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
            wishListAdapter.submitList(state.data.data)
            if (state.data.data.isEmpty()){
                binding.emptyView.root.visibility = View.VISIBLE
            }else{
                binding.emptyView.root.visibility = View.GONE
            }
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
                    startActivity(intent)
                }
                "fav" -> {
                    PreferenceUtils.readUserVO().customer_id?.let { viewModel.operateWishList(it,data.restaurant_id) }
                }
            }
        }
        binding.rvWishList.adapter = wishListAdapter
    }




}