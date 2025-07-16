package com.joy.fattyfood.ui.views.activities.category
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.joy.fattyfood.HomeViewModel
import com.joy.fattyfood.HomeViewState
import com.joy.fattyfood.R
import com.joy.fattyfood.adapters.TopRelatedCategoryAdapter
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityTopRelatedCategoryBinding
import com.joy.fattyfood.domain.viewstates.WishListViewState
import com.joy.fattyfood.ui.views.activities.auth.login.LoginActivity
import com.joy.fattyfood.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.joy.fattyfood.utils.Constants
import com.joy.fattyfood.utils.EqualSpacingItemDecoration
import com.joy.fattyfood.utils.LoadingProgressDialog
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopRelatedCategoryActivity : AppCompatActivity(){

    private lateinit var _binding : ActivityTopRelatedCategoryBinding

    private val viewModel : HomeViewModel by viewModels()

    private var topRelatedCategoryAdapter : TopRelatedCategoryAdapter? = null

    private var cat_name : String? = ""

    companion object {
        const val CATG = "catName"
        /*fun getIntent(param1 : String) : Intent {
            val intent = Intent(FattyApp.getInstance(),TopRelatedCategoryActivity::class.java)
            intent.putExtra(CATG,param1)
            return intent
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityTopRelatedCategoryBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        cat_name = intent.getStringExtra(CATG)

        cat_name?.let {
            if (it.equals("")) {
                _binding.tvTitle.text = ContextCompat.getString(this,R.string.top_related)
            } else {
                _binding.tvTitle.text = cat_name
            }
        }


        setUpObserver()
        setUpTopRelated()
        onReFresh()
        onBack()


    }


    private fun onReFresh() {
        _binding.swipeRefresh.setOnRefreshListener {
            _binding.swipeRefresh.isRefreshing = true
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchTopRelatedCategory(
                    it,
                    PreferenceUtils.readUserVO().latitude ?: 0.0,
                    PreferenceUtils.readUserVO().latitude ?: 0.0
                )
            }
        }

    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun setUpObserver() {
        PreferenceUtils.readUserVO().customer_id?.let {
            viewModel.fetchTopRelatedCategory(
                it,
                PreferenceUtils.readUserVO().latitude?:0.0,
                PreferenceUtils.readUserVO().longitude?:0.0
            )
        }
        viewModel.viewState.observe(this, Observer {
            render(it)
        })
        viewModel.viewStateWish.observe(this, Observer {
            renderWishList(it)
        })

    }

    private fun render(state : HomeViewState) {
        when(state) {
            is HomeViewState.OnLoadingTopRelated -> renderOnLoadingTopRelated()
            is HomeViewState.OnSuccessTopRelated -> renderOnSuccessTopRelated(state)
            is HomeViewState.OnFailTopRelated -> renderOnFailTopRelated(state)


            else -> {}
        }
    }

    private fun renderWishList(state : WishListViewState) {
        when(state) {
            is WishListViewState.OnLoadingWishList -> renderOnLoadingTopRelated()
            is WishListViewState.OnSuccessWishList -> renderOnSuccessWishList(state)
            is WishListViewState.OnFailWishList -> renderOnFailWishList(state)

            is WishListViewState.OnLoadingOperateWishList -> renderOnLoadingOperateWishList()
            is WishListViewState.OnSuccessOperateWishList -> renderOnSuccessOperateWishList(state)
            is WishListViewState.OnFailOperateWishList -> renderOnFailOperateWishList(state)

            else -> {}
        }
    }


    private fun renderOnFailWishList(state: WishListViewState.OnFailWishList) {
        showSnackBar(state.message)
    }
    private fun renderOnSuccessWishList(state: WishListViewState.OnSuccessWishList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            viewModel.topRelatedRestaurantLiveDataList.observe(this) {
                topRelatedCategoryAdapter?.setNewData(it)
            }

        }
    }
    private fun renderOnLoadingOperateWishList() {
        LoadingProgressDialog.showLoadingProgress(this@TopRelatedCategoryActivity)

    }

    private fun renderOnSuccessOperateWishList(state: WishListViewState.OnSuccessOperateWishList) {
        stopShimmer()
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.readUserVO().customer_id?.let { viewModel.operateWishList(it,state.data.data.restaurant_id) }
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)

            viewModel.nearRestaurantLiveDataList.value?.forEach { restVO ->
                if (state.data.data.restaurant_id == restVO.restaurant_id) {
                    restVO.is_wish = state.data.message == "successfull customer wishlist create"
                    restVO.is_wish = state.data.message == "successfull customer wishlist delete!"
                    viewModel.topRelatedRestaurantLiveDataList.observe(
                        this
                    ) {
                        //rvNearbyRestaurant.update(it)      ----------------------------------
                        topRelatedCategoryAdapter?.setNewData(it)

                    }
                }
            }
            /*CustomToast(
                requireContext(),
                state.data.message,
                true
            ).createCustomToast()*/
        }
    }

    private fun renderOnFailOperateWishList(state: WishListViewState.OnFailOperateWishList) {
        stopShimmer()
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            Constants.SERVER_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.ANOTHER_LOGIN -> {
                WarningDialog.Builder(this@TopRelatedCategoryActivity,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        startActivity(LoginActivity.getIntent("top_related"))
                    }).show(supportFragmentManager, TopRelatedCategoryActivity::class.simpleName)
            }

            Constants.DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, TopRelatedCategoryActivity::class.simpleName)

            else -> showSnackBar(state.message)
        }
    }

    private fun startShimmer() {
        _binding.shimmerRecomRestLayout.show()
        _binding.shimmerRecomRestLayout.startShimmer()
        _binding.rvRelatedCategory.gone()
    }


    private fun stopShimmer() {
        _binding.shimmerRecomRestLayout.stopShimmer()
        _binding.shimmerRecomRestLayout.gone()
        _binding.swipeRefresh.isRefreshing = false
        _binding.rvRelatedCategory.show()
    }

    private fun renderOnLoadingTopRelated() {
        startShimmer()
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnFailTopRelated(state : HomeViewState.OnFailTopRelated) {
        stopShimmer()
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            Constants.SERVER_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.ANOTHER_LOGIN -> {
                WarningDialog.Builder(this@TopRelatedCategoryActivity,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        startActivity(LoginActivity.getIntent("top_related"))
                    }).show(supportFragmentManager, TopRelatedCategoryActivity::class.simpleName)
            }

            Constants.DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, TopRelatedCategoryActivity::class.simpleName)

            else -> showSnackBar(state.message)
        }

    }

    private fun renderOnSuccessTopRelated(state: HomeViewState.OnSuccessTopRelated) {
        stopShimmer()
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            topRelatedCategoryAdapter?.setNewData(state.data.data)
        }
    }

    private fun setUpTopRelated() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        _binding.rvRelatedCategory.layoutManager = linearLayoutManager
        _binding.rvRelatedCategory.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        _binding.rvRelatedCategory.setHasFixedSize(true)
        _binding.rvRelatedCategory.isNestedScrollingEnabled = true
        topRelatedCategoryAdapter = TopRelatedCategoryAdapter(FattyApp.getInstance()) { data, str, pos ->
            when(str) {
                "root" -> {
                    PreferenceUtils.needToShow = false
                    PreferenceUtils.isBackground = false
                    /*startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to data.restaurant_id
                    )*/
                    val intent = Intent(this, RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID, data.restaurant_id)
                    startActivity(intent)
                }
                "fav" -> {
                    PreferenceUtils.readUserVO().customer_id?.let { viewModel.operateWishList(it,data.restaurant_id) }

                }
            }
        }
        _binding.rvRelatedCategory.adapter = topRelatedCategoryAdapter
    }

}