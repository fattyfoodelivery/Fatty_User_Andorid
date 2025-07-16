package com.joy.fattyfood.utils

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
import com.joy.fattyfood.R

@RequiresApi(Build.VERSION_CODES.O)
class CustomTabLayout : TabLayout {

    private var titles: List<String>? = null

    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr:Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun coverPixelToDP(dps: Int): Int {
        val scale = this.resources.displayMetrics.density
        return (dps * scale).toInt()
    }



    private fun init(context: Context) {
        titles = arrayListOf()
        setTitle(titles)
        this.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                /**
                 * Set the currently selected Tab to a special highlight style.
                 */
                if (tab != null && tab.customView != null) {
                    val tab_layout_text =
                        tab.customView!!.findViewById<TextView>(R.id.tab_layout_text)
                    tab_layout_text.setTextColor(ContextCompat.getColor(context,R.color.white))
                    tab_layout_text.typeface = ResourcesCompat.getFont(context, R.font.outfit_medium)
                    tab.customView?.setBackgroundResource(R.drawable.tablayout_item_pressed) //= ContextCompat.getDrawable(context,R.drawable.tablayout_item_pressed)
                }
            }

            override fun onTabUnselected(tab: Tab) {
                /**
                 * Reset all unselected Tab colors, fonts, background to normal (unselected state).
                 */
                if (tab != null && tab.customView != null) {
                    val tab_layout_text = tab.customView!!.findViewById<TextView>(R.id.tab_layout_text)
                    tab_layout_text.setTextColor(ContextCompat.getColor(context,R.color.white))
                    tab_layout_text.typeface = ResourcesCompat.getFont(context, R.font.outfit_medium)
                    tab.customView?.setBackgroundResource(R.drawable.bg_tab_unselected)
                    //tab.customView?.background = ContextCompat.getDrawable(context,R.drawable.tablayout_item_pressed)
                }
            }

            override fun onTabReselected(tab: Tab) {}
        })
    }

    fun setTitle(titles: List<String?>?) {
        this.titles = titles as List<String>?
        /**
         * Start adding tabs for switching.
         */
        for (title in this.titles!!) {
            val tab = newTab()
            tab.setCustomView(R.layout.tab_layout_item)
            if (tab.customView != null) {
                val text = tab.customView!!.findViewById<TextView>(R.id.tab_layout_text)
                text.text = title
                text.setTextColor(ContextCompat.getColor(context,R.color.text_primary_01))
                text.typeface = ResourcesCompat.getFont(context, R.font.outfit_medium)
                tab.customView?.background = ContextCompat.getDrawable(context,R.drawable.tablayout_item_pressed)
            }
            this.addTab(tab)
        }
    }


}