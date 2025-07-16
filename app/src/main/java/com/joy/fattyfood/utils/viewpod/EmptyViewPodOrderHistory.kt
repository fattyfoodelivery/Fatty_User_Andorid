package com.joy.fattyfood.utils.viewpod

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class EmptyViewPodOrderHistory @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
    }


    fun setEmptyData(message : String) {
        //emptyMessage.text = message
    }
}