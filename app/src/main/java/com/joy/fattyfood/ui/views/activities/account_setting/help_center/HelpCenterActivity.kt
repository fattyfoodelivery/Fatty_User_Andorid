package com.joy.fattyfood.ui.views.activities.account_setting.help_center

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.joy.fattyfood.adapters.HelpCenterAdapter
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityHelpCenterBinding
import com.joy.fattyfood.domain.view_model.AboutViewModel
import com.joy.fattyfood.utils.EqualSpacingItemDecoration
import com.joy.fattyfood.utils.helper.phoneCall
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

    private fun render(state: com.joy.fattyfood.domain.viewstates.AboutViewState) {
        when (state) {
            is com.joy.fattyfood.domain.viewstates.AboutViewState.OnLoadingHelpCenter -> {
                renderLoading()
            }
            is com.joy.fattyfood.domain.viewstates.AboutViewState.OnSuccessHelpCenter -> {
                renderSuccess(state)
            }
            is com.joy.fattyfood.domain.viewstates.AboutViewState.OnFailHelpCenter -> {
                renderError()
            }
            else -> {}
        }
    }

    private fun renderLoading() {
    }

    private fun renderSuccess(state: com.joy.fattyfood.domain.viewstates.AboutViewState.OnSuccessHelpCenter) {
        helpCenterAdapter.setNewData(state.data.data)
    }

    private fun renderError() {
    }
}
