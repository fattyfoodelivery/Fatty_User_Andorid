package com.orikino.fatty.utils.viewpod

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.orikino.fatty.databinding.LayoutEmptyViewBinding
import com.orikino.fatty.utils.helper.gone

class EmptyViewPod @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mDelegate: EmptyViewPodDelegate? = null

    private var emptyViewBinding: LayoutEmptyViewBinding? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setUpListener()
    }

    fun setDelegate (delegate: EmptyViewPodDelegate) {
        mDelegate = delegate
    }

    fun setEmptyData(message : String,des : String,resource : Drawable) {
        emptyViewBinding?.emptyMessage?.text = message
        emptyViewBinding?.emptyMessageDes?.text = des
        emptyViewBinding?.emptyImage?.setImageDrawable(resource)
    }

    fun hideEmptyBtn() {
        emptyViewBinding?.btnExpore?.gone()
    }

    private fun setUpListener () {
        emptyViewBinding?.btnExpore?.setOnClickListener {
            mDelegate?.onTapTryAgain()
        }
    }
}

interface EmptyViewPodDelegate{
    fun onTapTryAgain()
}