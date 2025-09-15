package com.orikino.fatty.utils

import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import java.lang.reflect.Method

internal class CustomColoredUnderlineSpan(private val mColor : Int,val width:Float) : CharacterStyle() ,
    UpdateAppearance {
    override fun updateDrawState(tp : TextPaint) {
        try {
            val method : Method = TextPaint::class.java.getMethod("setUnderlineText" ,
                Integer.TYPE ,
                java.lang.Float.TYPE)
            method.invoke(tp , mColor , width)
        } catch (e : Exception) {
            tp.isUnderlineText = false
        }
    }
}