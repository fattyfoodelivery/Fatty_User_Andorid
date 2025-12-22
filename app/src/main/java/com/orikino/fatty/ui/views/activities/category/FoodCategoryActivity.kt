package com.orikino.fatty.ui.views.activities.category

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.orikino.fatty.CategoryViewModel
import com.orikino.fatty.CategoryViewState
import com.orikino.fatty.R
import com.orikino.fatty.adapters.TopFoodCategoryAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityFoodCategoryBinding
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.delegate.ItemIdDelegate
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultCategoryName
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodCategoryActivity : AppCompatActivity() , ItemIdDelegate {

    private lateinit var foodCategoryBinding: ActivityFoodCategoryBinding
    private lateinit var topFoodCategoryAdapter: TopFoodCategoryAdapter

    private val viewModel: CategoryViewModel by viewModels()


    var cat_name = ""

    companion object {
        const val CATG = "catName"
        fun getIntent(param1 : String) : Intent {
            val intent = Intent(FattyApp.getInstance(),FoodCategoryActivity::class.java)
            intent.putExtra(CATG,param1)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        foodCategoryBinding = ActivityFoodCategoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(foodCategoryBinding.root)
        foodCategoryBinding.root.fixCutoutOfEdgeToEdge(foodCategoryBinding.root)
        cat_name = intent.getStringExtra(CATG).toString()
        foodCategoryBinding.tvTitle.text = cat_name


        setUpObserver()
        setUpFoodCategory()
        onBack()
    }

    private fun onBack() {
        foodCategoryBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpObserver() {
        viewModel.fetchCategoryList()
        viewModel.viewState.observe(this, Observer { render(it) })
    }


    private fun render(state : CategoryViewState) {
        when(state) {
            is CategoryViewState.OnLoadingCategoryList -> renderOnLoadingCategoryList()
            is CategoryViewState.OnSuccessCategoryList -> renderOnSuccessCategoryList(state)
            is CategoryViewState.OnFailCategoryList -> renderOnFailCategoryList(state)
            else -> {}

        }
    }

    private fun renderOnLoadingCategoryList() {
        LoadingProgressDialog.showLoadingProgress(this@FoodCategoryActivity)
    }

    private fun renderOnSuccessCategoryList(state: CategoryViewState.OnSuccessCategoryList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            topFoodCategoryAdapter.setNewData(state.data.data)
        }
    }

    private fun renderOnFailCategoryList(state: CategoryViewState.OnFailCategoryList) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            Constants.SERVER_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(state.message)
            }
            Constants.ANOTHER_LOGIN -> {
                WarningDialog.Builder(this@FoodCategoryActivity,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        startActivity(LoginActivity.getIntent("top_related"))
                    }).show(supportFragmentManager, FoodCategoryActivity::class.simpleName)
            }

            Constants.DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, FoodCategoryActivity::class.simpleName)

            else -> showSnackBar(state.message)
        }
    }

    private fun setUpFoodCategory() {
        val linearLayoutManager =
            GridLayoutManager(FattyApp.getInstance(), 4)
        foodCategoryBinding.rvFoodCategory.layoutManager = linearLayoutManager
        foodCategoryBinding.rvFoodCategory.addItemDecoration(
            EqualSpacingItemDecoration(
                spacing = 24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        foodCategoryBinding.rvFoodCategory.setHasFixedSize(true)
        foodCategoryBinding.rvFoodCategory.isNestedScrollingEnabled = true
        topFoodCategoryAdapter = TopFoodCategoryAdapter(FattyApp.getInstance()){
            //it.category_assign_id
            //startActivity(TopRelatedCategoryActivity.getIntent(it.toDefaultCategoryName().toString()))
            /*startActivity<TopRelatedCategoryActivity>(
                TopRelatedCategoryActivity.CATG to ""
            )*/
            startActivity(TopRelatedCategoryActivity.getIntent(it.toDefaultCategoryName().toString(), it.restaurant_category_id))
        }
        foodCategoryBinding.rvFoodCategory.adapter = topFoodCategoryAdapter
    }

    override fun onTapItemID(itemId: Int) {
        //startActivity(TopRelatedCategoryActivity.getIntent(""))
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}