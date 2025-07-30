package com.orikino.fatty.utils.viewpod

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.orikino.fatty.domain.model.LanguageVO
import com.orikino.fatty.domain.model.AcceptData

class SpinnerViewpod : LinearLayout, AcceptData<LanguageVO> {

    private var tvSpinner: TextView? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onFinishInflate() {
        super.onFinishInflate()
        //tvSpinner = findViewById(R.id.tv_spinner)
    }

    override fun bindData(data: LanguageVO) {
        tvSpinner!!.text = data.language
    }
}