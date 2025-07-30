package com.orikino.fatty.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.orikino.fatty.domain.model.UpAndDownVO
import com.orikino.fatty.ui.views.fragments.home.HomeSlideFragment

class HomeSlideAdapter(fragment: Fragment, private val itemsCount: Int, val dataList  : MutableList<UpAndDownVO>, val onClick :() -> Unit) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = itemsCount

    override fun createFragment(position: Int): Fragment = HomeSlideFragment.newInstance(position, dataList){
        onClick.invoke()
    }
}