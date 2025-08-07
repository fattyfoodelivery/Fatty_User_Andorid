package com.orikino.fatty.ui.views.base


import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt


abstract class NewBaseViewHolder<W>(itemView : View) : RecyclerView.ViewHolder(itemView) ,
    View.OnClickListener {


    fun getHeight(context : Context? , text : String? , textSize : Int , deviceWidth : Int) : Int {
        val textView = TextView(context)
        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX , textSize.toFloat())
        val widthMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(deviceWidth , View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec , heightMeasureSpec)
        return (textView.measuredHeight * 2.0).roundToInt()
    }

    /* fun getScreenWidth(context: Context, percentage: Double): Int {
         val displayMetrics = DisplayMetrics()
         (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
         return (displayMetrics.widthPixels * percentage).toInt()
     }
 */
    override fun onClick(v : View) {}

//    abstract fun onSelected(index : Int )

    abstract fun setData(mData : W, currentPage : Int)
}
