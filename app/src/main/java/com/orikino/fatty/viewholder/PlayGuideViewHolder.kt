package com.orikino.fatty.viewholder

import android.content.Intent
import android.view.View
import coil.load
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ItemAppGuideBinding
import com.orikino.fatty.domain.responses.TutorialVO
import com.orikino.fatty.ui.views.activities.account_setting.play_guide.PlayGuideActivity
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.delegate.ItemStringDelegate

class PlayGuideViewHolder(var binding : ItemAppGuideBinding, var delegate: ItemStringDelegate) : BaseViewHolder<TutorialVO>(binding.root)  {

    override fun setData(data: TutorialVO, position: Int) {

        val url = PreferenceUtils.IMAGE_URL.plus("/tutorial_coverphoto/").plus(data.video)
        /*binding.webview.settings.javaScriptEnabled = true
        binding.webview.webChromeClient = object : WebChromeClient() {
        }
        binding.webview.loadData(PreferenceUtils.IMAGE_URL.plus("/tutorial_coverphoto/").plus(data.video),"text/html", "utf-8")*/

        binding.guidePhoto.load(PreferenceUtils.IMAGE_URL.plus("/tutorial_coverphoto/").plus(data.photo))

        binding.root.setOnClickListener {
            val intent = Intent(FattyApp.getInstance(), PlayGuideActivity::class.java)
            intent.putExtra(PlayGuideActivity.V_URL, PreferenceUtils.IMAGE_URL.plus("/tutorial/").plus(data.video))
            binding.root.context.startActivity(intent)
            /*binding.root.context.startActivity<PlayGuideActivity>(
                PlayGuideActivity.V_URL to PreferenceUtils.IMAGE_URL.plus("/tutorial/").plus(data.video)
            )*/

        }

    }

    override fun onClick(v: View?) {

    }
}