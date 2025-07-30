package com.orikino.fatty.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.orikino.fatty.ui.views.fragments.IntroSlideFragment

class IntroSlideAdapter(activity: AppCompatActivity, private val itemsCount: Int) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = itemsCount

    override fun createFragment(position: Int): Fragment = IntroSlideFragment.newInstance(position)

}