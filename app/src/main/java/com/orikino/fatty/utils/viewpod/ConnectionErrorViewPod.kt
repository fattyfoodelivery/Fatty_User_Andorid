package com.orikino.fatty.utils.viewpod

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.orikino.fatty.databinding.LayoutConnectionNoDataBinding


class ConnectionErrorViewPod @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding : LayoutConnectionNoDataBinding? = null

    private var mDelegate: ConnectionErrorViewPodDelegate? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        setUpListener()
    }

    fun setDelegate (delegate: ConnectionErrorViewPodDelegate) {
        mDelegate = delegate
    }

    fun setEmptyData(title:String,message : String,resource : Drawable) {
        binding?.apply {
            tvErrorTitle.text = title
            tvErrorMessage.text = message
            imvError.setImageDrawable(resource)
        }
    }

    private fun setUpListener () {
        binding?.btnTryAgain?.setOnClickListener {
            mDelegate?.onTapTryAgain()
        }
    }
}

interface ConnectionErrorViewPodDelegate{
    fun onTapTryAgain()
}