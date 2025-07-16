package com.joy.fattyfood.utils.viewpod

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

import com.joy.fattyfood.domain.model.AcceptData
import com.joy.fattyfood.domain.model.StateVO

class StateSpinnerViewpod : LinearLayout, AcceptData<StateVO> {

    private var tvSpinner: TextView? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onFinishInflate() {
        super.onFinishInflate()
        //tvSpinner = findViewById(R.id.tv_state)
    }

    override fun bindData(data: StateVO) {
        tvSpinner!!.text = data.state_name
    }
}