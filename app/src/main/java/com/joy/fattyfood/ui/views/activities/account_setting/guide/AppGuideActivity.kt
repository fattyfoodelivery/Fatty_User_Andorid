package com.joy.fattyfood.ui.views.activities.account_setting.guide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.joy.fattyfood.adapters.PlayerGuideAdapter
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityAppGuideBinding
import com.joy.fattyfood.domain.view_model.AboutViewModel
import com.joy.fattyfood.domain.viewstates.AboutViewState
import com.joy.fattyfood.ui.views.activities.account_setting.play_guide.PlayGuideActivity
import com.joy.fattyfood.utils.delegate.ItemStringDelegate
import com.joy.fattyfood.utils.EqualSpacingItemDecoration
import com.joy.fattyfood.utils.helper.showSnackBar
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
}