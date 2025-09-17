package com.orikino.fatty.ui.views.activities.category
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.HomeViewModel
import com.orikino.fatty.HomeViewState
import com.orikino.fatty.R
import com.orikino.fatty.adapters.NearByIdRestAdapter
import com.orikino.fatty.adapters.RecommendedRestaurantAdapter
import com.orikino.fatty.adapters.TopRelatedCategoryAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityTopRelatedCategoryBinding
import com.orikino.fatty.domain.viewstates.WishListViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.activities.webview.WebviewActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopRelatedCategoryActivity : AppCompatActivity(){

    private lateinit var _binding : ActivityTopRelatedCategoryBinding

    private val viewModel : HomeViewModel by viewModels()

    private var topRelatedCategoryAdapter : TopRelatedCategoryAdapter? = null
    private var recommendedRestaurantAdapter: NearByIdRestAdapter? = null

    private var cat_name : String? = ""
    private var cat_id : Int? = null

    companion object {
        const val CATG = "catName"
        const val CATG_ID = "catID"
        fun getIntent(param1 : String, categoryID : Int) : Intent {
            val intent = Intent(FattyApp.getInstance(),TopRelatedCategoryActivity::class.java)
            intent.putExtra(CATG,param1)
            intent.putExtra(CATG_ID, categoryID)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityTopRelatedCategoryBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        cat_name = intent.getStringExtra(CATG)
        cat_id = intent.getIntExtra(CATG_ID, 0)

        cat_name?.let {
            if (it == "Top-Rated") {
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
        _binding.swipeRefresh.isRefreshing = true
        _binding.swipeRefresh.setOnRefreshListener {
            if (cat_id == null || cat_id == 0){
                PreferenceUtils.readUserVO().customer_id?.let {
                    viewModel.fetchTopRelatedCategory(
                        it,
                        PreferenceUtils.readUserVO().latitude ?: 0.0,
                        PreferenceUtils.readUserVO().latitude ?: 0.0
                    )
                }
            }else{
                PreferenceUtils.readUserVO().customer_id?.let {
                    viewModel.fetchRestaurantByCategory(
                        cat_id!!,
                        it,
                        PreferenceUtils.readUserVO().latitude ?: 0.0,
                        PreferenceUtils.readUserVO().latitude ?: 0.0
                    )
                }
            }

        }

    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun setUpObserver() {
        if (cat_id == null || cat_id == 0){
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchTopRelatedCategory(
                    it,
                    PreferenceUtils.readUserVO().latitude?:0.0,
                    PreferenceUtils.readUserVO().longitude?:0.0
                )
            }
        }else{
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchRestaurantByCategory(
                    cat_id!!,
                    it,
                    PreferenceUtils.readUserVO().latitude ?: 0.0,
                    PreferenceUtils.readUserVO().longitude ?: 0.0
                )
            }
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

            is HomeViewState.OnLoadingRestaurantByCategory -> renderOnLoadingRestaurantByCategory()
            is HomeViewState.OnSuccessRestaurantByCategory -> renderOnSuccessRestaurantByCategory(state)
            is HomeViewState.OnFailRestaurantByCategory -> renderOnFailRestaurantByCategory(state)

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

    private fun renderOnLoadingRestaurantByCategory() {
        startShimmer()
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnLoadingTopRelated() {
        startShimmer()
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnFailRestaurantByCategory(state : HomeViewState.OnFailRestaurantByCategory) {
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

    private fun renderOnSuccessRestaurantByCategory(state: HomeViewState.OnSuccessRestaurantByCategory) {
        stopShimmer()
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            recommendedRestaurantAdapter?.submitList(state.data.data)
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
        if (cat_id == null || cat_id == 0){
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
        }else{
            recommendedRestaurantAdapter = NearByIdRestAdapter(
                this
                // Removed mutableListOf() argument
            ) { data, str, pos ->
                when(str) {
                    "ads" -> {
                        when(data.display_type_id){
                            1 -> {
                                PreferenceUtils.needToShow = false
                                PreferenceUtils.isBackground = false
                                val intent = Intent(this,RestaurantDetailViewActivity::class.java)
                                intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant_id)
                                startActivity(intent)
                            }
                            2 -> {
                                val intent = WebviewActivity.getIntent(this,data.toDefaultRestaurantName().toString(),data.display_type_description)
                                startActivity(intent)
                            }
                            3 -> {
                                val url = data.display_type_description
                                if (url.isNotEmpty()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(this, "Cannot open link: No browser found.", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) { // Catch other potential exceptions like UriParseException
                                        Toast.makeText(this, "Cannot open link: Invalid URL.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Optionally handle the case where the URL is null or empty
                                    Toast.makeText(this, "No URL provided for this item.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> {
                                //do nothing
                            }
                        }
                    }
                    "cv_rest" -> {
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,RestaurantDetailViewActivity::class.java)
                        intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant_id)
                        startActivity(intent)
                    }
                    "fav" -> {
                        if (PreferenceUtils.readUserVO()?.customer_id == 0) {
                            SuccessDialog.Builder(
                                this,
                                resources.getString(R.string.login_message),
                                callback = {
                                    PreferenceUtils.needToShow = false
                                    PreferenceUtils.isBackground = false
                                    val intent = Intent(this,LoginActivity::class.java)
                                    startActivity(intent)
                                })
                                .show(supportFragmentManager, HomeFragment::class.simpleName)
                        } else {
                            PreferenceUtils.readUserVO().customer_id?.let {
                                viewModel.operateWishList(it, data.restaurant_id)
                            }
                        }

                    }
                }
            }
            _binding.rvRelatedCategory.adapter = recommendedRestaurantAdapter
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