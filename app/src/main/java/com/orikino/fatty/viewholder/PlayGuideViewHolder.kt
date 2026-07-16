package com.orikino.fatty.viewholder

import android.content.Intent
import android.view.View
import coil.load
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ItemAppGuideBinding
import com.orikino.fatty.domain.responses.TutorialVO
import com.orikino.fatty.ui.views.activities.account_setting.play_guide.PlayGuideActivity
import com.orikino.fatty.utils.ImageUrlProvider
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.delegate.ItemStringDelegate

class PlayGuideViewHolder(var binding : ItemAppGuideBinding, val imageUrlProvider: ImageUrlProvider, var delegate: ItemStringDelegate) : BaseViewHolder<TutorialVO>(binding.root)  {

    override fun setData(data: TutorialVO, position: Int) {

        binding.guidePhoto.load(imageUrlProvider.get(("/tutorial_coverphoto/").plus(data.photo)))

        binding.root.setOnClickListener {
            val intent = Intent(FattyApp.getInstance(), PlayGuideActivity::class.java)
            intent.putExtra(PlayGuideActivity.V_URL, imageUrlProvider.get(("/tutorial/").plus(data.video)))
            binding.root.context.startActivity(intent)


        }

    }

    override fun onClick(v: View?) {

    }
}