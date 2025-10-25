package com.orikino.fatty.ui.views.activities.account_setting.help_center

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.adapters.HelpCenterAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityHelpCenterBinding
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.phoneCall
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpCenterActivity : AppCompatActivity() {

    lateinit var _binding: ActivityHelpCenterBinding
    lateinit var helpCenterAdapter: HelpCenterAdapter

    private val viewModel: AboutViewModel by viewModels()

    companion object {
        fun getIntent(): Intent {
            return Intent(FattyApp.getInstance(), HelpCenterActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        helpObserver()
        setUpHelpCenterRecycler()
        onBack()
    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun setUpHelpCenterRecycler() {
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        _binding.rvHelp.layoutManager = linearLayoutManager
        _binding.rvHelp.addItemDecoration(
            EqualSpacingItemDecoration(
                spacing = 24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        _binding.rvHelp.setHasFixedSize(true)
        _binding.rvHelp.isNestedScrollingEnabled = true
        helpCenterAdapter = HelpCenterAdapter(this) {
            phoneCall(it)
        }
        _binding.rvHelp.adapter = helpCenterAdapter
    }

    private fun helpObserver() {
        viewModel.fetchHelpCenter()
        viewModel.viewState.observe(this) {
            render(it)
        }
    }

    private fun render(state: com.orikino.fatty.domain.viewstates.AboutViewState) {
        when (state) {
            is com.orikino.fatty.domain.viewstates.AboutViewState.OnLoadingHelpCenter -> {
                renderLoading()
            }
            is com.orikino.fatty.domain.viewstates.AboutViewState.OnSuccessHelpCenter -> {
                renderSuccess(state)
            }
            is com.orikino.fatty.domain.viewstates.AboutViewState.OnFailHelpCenter -> {
                renderError()
            }
            else -> {}
        }
    }

    private fun renderLoading() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderSuccess(state: com.orikino.fatty.domain.viewstates.AboutViewState.OnSuccessHelpCenter) {
        LoadingProgressDialog.hideLoadingProgress()
        helpCenterAdapter.setNewData(state.data.data)
    }

    private fun renderError() {
        LoadingProgressDialog.hideLoadingProgress()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}
