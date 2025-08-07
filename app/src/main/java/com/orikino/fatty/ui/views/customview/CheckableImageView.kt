package com.orikino.fatty.ui.views.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import com.google.android.material.imageview.ShapeableImageView
import com.orikino.fatty.R

class CheckableImageView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr), Checkable {

    init {
        // Optional: Set default background for checked state
        setBackgroundResource(R.color.transparent)
    }

    private var checked = false
    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return checked
    }

    override fun toggle() {
        isChecked = !checked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }
}