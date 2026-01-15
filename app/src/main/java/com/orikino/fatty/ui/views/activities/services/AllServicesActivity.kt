package com.orikino.fatty.ui.views.activities.services

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.orikino.fatty.R
import com.orikino.fatty.adapters.ServicesAdapter
import com.orikino.fatty.databinding.ActivityAllServicesBinding
import com.orikino.fatty.domain.view_model.HomeViewModel
import com.orikino.fatty.domain.viewstates.HomeViewState
import com.orikino.fatty.ui.views.activities.account_setting.help_center.HelpCenterActivity
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.parcel.BookingOrderActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.components.GridSpacingItemDecoration
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.AccountRestrictedDialog
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.dpToPx
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllServicesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllServicesBinding
    private val viewModel : HomeViewModel by viewModels()
    private var serviceAdapter : ServicesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllServicesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fixCutoutOfEdgeToEdge(binding.root)
        binding.ivBack.setOnClickListener {
            finish()
        }
        setUpRecyclerView()
        viewModel.fetchServiceItem()
        observers()
    }

    private fun observers() {
        viewModel.viewState.observe(this) { render(it) }
    }

    private fun render(state: HomeViewState) {
        when (state) {
            is HomeViewState.OnLoadingServiceItem -> {renderOnLoadingServiceItem()}
            is HomeViewState.OnSuccessServiceItem -> {renderOnSuccessServiceItem(state)}
            is HomeViewState.OnFailServiceItem -> {renderOnFailServiceItem(state)}
            else -> {}
        }
    }

    private fun renderOnSuccessServiceItem(state: HomeViewState.OnSuccessServiceItem){
        LoadingProgressDialog.hideLoadingProgress()
        serviceAdapter?.submitList(state.data.data)
    }

    private fun renderOnFailServiceItem(state: HomeViewState.OnFailServiceItem) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Error" -> {
                binding.layoutNetworkError.root.show()
            }
            "Another Login" -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        val intent = Intent(this,SplashActivity::class.java)
                        startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(supportFragmentManager, AllServicesActivity::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, HomeFragment::class.simpleName)

            else ->
            {
                showSnackBar(state.message)
            }
        }
    }

    private fun renderOnLoadingServiceItem(){
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun setUpRecyclerView(){
        serviceAdapter = ServicesAdapter(this, { data ->
            if (PreferenceUtils.readUserVO().customer_id == 0) {
                ConfirmDialog.Builder(
                    this,
                    resources.getString(R.string.hello),
                    resources.getString(R.string.login_message),
                    resources.getString(R.string.login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    })
                    .show(
                        supportFragmentManager,
                        HomeFragment::class.simpleName
                    )
            } else if (PreferenceUtils.readUserVO().is_restricted == 1) {
                AccountRestrictedDialog.Builder(
                    this,
                    callback = {
                        val intent = Intent(this, HelpCenterActivity::class.java)
                        startActivity(intent)
                    })
                    .show(
                        supportFragmentManager,
                        HomeFragment::class.simpleName
                    )
            } else {
                if (data.name == "ပါဆယ်" || data.name == "Parcel"){
                    val intent = Intent(this, BookingOrderActivity::class.java)
                    intent.putExtra(BookingOrderActivity.IS_EDIT, false)
                    startActivity(intent)
                }else{
                    startActivity(ServiceDetailActivity.getIntent(this, data.service_item_id, data.name, data.sub_title, data.cover_image ?: ""))
                }
            }
        })
        val linearLayoutManager =
            GridLayoutManager(this, 2)
        binding.rvServices.layoutManager = linearLayoutManager
        binding.rvServices.addItemDecoration(
            GridSpacingItemDecoration(
                5,
                dpToPx(8f, this),
                false
            )
        )

        binding.rvServices.adapter = serviceAdapter
    }
}