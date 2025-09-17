package com.orikino.fatty.ui.views.activities.account_setting.guide

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.orikino.fatty.adapters.PlayerGuideAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityAppGuideBinding
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.domain.viewstates.AboutViewState
import com.orikino.fatty.ui.views.activities.account_setting.play_guide.PlayGuideActivity
import com.orikino.fatty.utils.delegate.ItemStringDelegate
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppGuideActivity : AppCompatActivity() , ItemStringDelegate {

    private lateinit var appGuideBinding: ActivityAppGuideBinding

    private lateinit var playerGuideAdapter: PlayerGuideAdapter

    private val viewModel : AboutViewModel by viewModels()

    companion object {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appGuideBinding = ActivityAppGuideBinding.inflate(layoutInflater)
        setContentView(appGuideBinding.root)


        appGuideBinding.ivBack.setOnClickListener { finish() }
        setUpSubscribe()
        setUpGuideList()
    }


    private fun setUpSubscribe() {
        viewModel.fetchTutorialApp()
        viewModel.viewState.observe(this) {
            when(it) {
                is AboutViewState.OnLoadingTutorial -> renderOnLoadingTutorial()
                is AboutViewState.OnSuccessTutorial -> renderOnSuccessTutorial(it)
                is AboutViewState.OnFailTutorial -> renderOnFailTutorial(it)
                else -> {}
            }
        }

    }


    private fun renderOnLoadingTutorial() {}
    private fun renderOnFailTutorial(state: AboutViewState.OnFailTutorial) {
        showSnackBar(state.message!!)
    }

    private fun renderOnSuccessTutorial(state: AboutViewState.OnSuccessTutorial) {
        if (state.data.success) {
            playerGuideAdapter.setNewData(state.data.data)
        }
    }
    private fun setUpGuideList() {
        val linearLayoutManager =
            GridLayoutManager(FattyApp.getInstance(), 2)
        appGuideBinding.rvPlayList.layoutManager = linearLayoutManager
        appGuideBinding.rvPlayList.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        appGuideBinding.rvPlayList.setHasFixedSize(true)
        appGuideBinding.rvPlayList.isNestedScrollingEnabled = true
        playerGuideAdapter = PlayerGuideAdapter(FattyApp.getInstance(), this)
        //playerGuideAdapter.setNewData(tutorial)
        appGuideBinding.rvPlayList.adapter = playerGuideAdapter
    }

    override fun onTapItemString(itemString: String) {
        /*startActivity<PlayGuideActivity>(
            PlayGuideActivity.V_URL to itemString
        )*/
        val intent = Intent(FattyApp.getInstance(), PlayGuideActivity::class.java)
        intent.putExtra(PlayGuideActivity.V_URL, itemString)
        startActivity(intent)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}