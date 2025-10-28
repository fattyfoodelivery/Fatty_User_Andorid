package com.orikino.fatty.ui.views.activities.category
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.domain.view_model.HomeViewModel
import com.orikino.fatty.HomeViewState
import com.orikino.fatty.R
import com.orikino.fatty.adapters.NearByIdRestAdapter
import com.orikino.fatty.adapters.TopRelatedCategoryAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityTopRelatedCategoryBinding
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.viewstates.WishListViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.activities.webview.WebviewActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SmartScrollListener
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class TopRelatedCategoryActivity : AppCompatActivity(), SmartScrollListener.OnSmartScrollListener {

    private lateinit var _binding : ActivityTopRelatedCategoryBinding

    private val viewModel : HomeViewModel by viewModels()

    private var topRelatedCategoryAdapter : TopRelatedCategoryAdapter? = null
    private var recommendedRestaurantAdapter: NearByIdRestAdapter? = null
    private var titleName : String = ""
    private val TEMP_HTML_FILENAME = "temp_ad_content.html"

    private var cat_name : String? = ""
    private var cat_id : Int? = null



    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val returnedID = result.data?.getIntExtra("WISHED_LISTED_ID", 0) ?: 0
            val isWished = result.data?.getBooleanExtra("IS_WISHED", false) ?: false
            if (returnedID != 0){
                val temp = topRelatedCategoryAdapter?.currentList?.toMutableList() ?: mutableListOf()
                val editedIndex = temp.indexOfFirst { it.restaurant_id == returnedID }
                if (editedIndex != -1) {
                    val originalItem = temp[editedIndex]
                    val updatedItem = originalItem.copy(is_wish = isWished)
                    temp[editedIndex] = updatedItem
                    //showSnackBar(temp[editedIndex].is_wish.toString())
                    topRelatedCategoryAdapter?.submitList(temp)
                }
            }
        }
    }

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
                titleName = getString(R.string.txt_top_rated_restaurants)
                _binding.tvTitle.text = titleName
            } else {
                titleName = cat_name ?: ""
                _binding.tvTitle.text = cat_name
            }
        }
        _binding.emptyView.emptyMessage.text = getString(R.string.no_data_available)
        _binding.emptyView.emptyMessageDes.text = ""

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
        viewModel.maxPage.observe(this, Observer {
            if (it != 0)
                _binding.tvTitle.text = "$titleName ($it)"
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
                topRelatedCategoryAdapter?.submitList(it)
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
            //PreferenceUtils.readUserVO().customer_id?.let { viewModel.operateWishList(it,state.data.data.restaurant_id) }
            PreferenceUtils.wishListCount.postValue(state.data.data.wishlist_count)

            viewModel.nearRestaurantLiveDataList.value?.forEach { restVO ->
                if (state.data.data.restaurant_id == restVO.restaurant_id) {
                    restVO.is_wish = state.data.message == "successfull customer wishlist create"
                    restVO.is_wish = state.data.message == "successfull customer wishlist delete!"
                    viewModel.topRelatedRestaurantLiveDataList.observe(
                        this
                    ) {
                        //rvNearbyRestaurant.update(it)      ----------------------------------
                        topRelatedCategoryAdapter?.submitList(it)

                    }
                }
            }
            if (state.data.data.is_wish){
                CustomToast(this, getString(R.string.added_to_favourite_item), true).createCustomToast()
            }else{
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
        if (viewModel.getTopRelatedCurrentPage() == 1){
            startShimmer()
            LoadingProgressDialog.showLoadingProgress(this)
        }else{
        }

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
        val currentList = topRelatedCategoryAdapter?.currentList?.toMutableList() ?: mutableListOf()
        if (currentList.isNotEmpty() && currentList[currentList.size-1].isLoadingView){
            currentList.removeAt(currentList.size-1)
            topRelatedCategoryAdapter?.submitList(currentList)
        }

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
        _binding.tvTitle.text = "$titleName (${state.data.data.count { it.listing_type != 2 }})"
        if (state.data.success) {
            if (state.data.data.isEmpty()){
                _binding.emptyView.rootView.visibility = View.VISIBLE
            }else{
                _binding.emptyView.rootView.visibility = View.GONE
                recommendedRestaurantAdapter?.submitList(state.data.data)
            }
        }
    }

    private fun renderOnSuccessTopRelated(state: HomeViewState.OnSuccessTopRelated) {
        stopShimmer()
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            if (state.data.data.isEmpty()){
                _binding.emptyView.rootView.visibility = View.VISIBLE
            }else{
                _binding.emptyView.rootView.visibility = View.GONE
                val currentList = topRelatedCategoryAdapter?.currentList?.toMutableList() ?: mutableListOf()
                if (currentList.isNotEmpty() && currentList[currentList.size-1].isLoadingView){
                    currentList.removeAt(currentList.size-1)
                }
                currentList.addAll(state.data.data)
                topRelatedCategoryAdapter?.submitList(currentList)
            }

        }
    }

    private fun saveHtmlToFile(context: Context, htmlContent: String, filename: String): String? {
        return try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use {
                it.write(htmlContent.toByteArray(Charsets.UTF_8))
            }
            file.absolutePath
        } catch (e: IOException) {
            Log.e("SplashActivity", "Error saving HTML to file: $filename", e)
            Toast.makeText(context, "Error preparing content for display.", Toast.LENGTH_SHORT).show()
            null
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
                        val intent = Intent(this, RestaurantDetailViewActivity::class.java)
                        intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID, data.restaurant_id)
                        startForResult.launch(intent)
                    }
                    "fav" -> {
                        PreferenceUtils.readUserVO().customer_id?.let { viewModel.operateWishList(it,data.restaurant_id) }
                    }
                }
            }
            _binding.rvRelatedCategory.adapter = topRelatedCategoryAdapter
            _binding.rvRelatedCategory.addOnScrollListener(
                SmartScrollListener(
                    this,
                    _binding.swipeRefresh
                )
            )
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
                                val intent = Intent(this, RestaurantDetailViewActivity::class.java)
                                intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID, data.restaurant_id)
                                startForResult.launch(intent)
                            }
                            2 -> {
                                val htmlContent = data.display_type_description
                                val title = data.restaurant_name
                                if (htmlContent.isNotEmpty()) {
                                    val filePath = saveHtmlToFile(this, htmlContent, TEMP_HTML_FILENAME)
                                    if (filePath != null) {
                                        val intent = WebviewActivity.getIntentWithFilePath(this, title, filePath)
                                        startActivity(intent)
                                    }
                                }
//                                val intent = WebviewActivity.getIntent(this,data.toDefaultRestaurantName().toString(),data.display_type_description)
//                                startActivity(intent)
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
                        val intent = Intent(this, RestaurantDetailViewActivity::class.java)
                        intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID, data.restaurant_id)
                        startForResult.launch(intent)
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

    override fun onListEndReach() {
        val currentList = topRelatedCategoryAdapter?.currentList?.toMutableList() ?: mutableListOf()
        currentList.add(RecommendRestaurantVO(isLoadingView = true))
        topRelatedCategoryAdapter?.submitList(currentList)
        PreferenceUtils.readUserVO().customer_id?.let {
            viewModel.onListEndReachTopRelated(
                it,
                PreferenceUtils.readUserVO().latitude?:0.0,
                PreferenceUtils.readUserVO().longitude?:0.0
            )
        }
    }

}