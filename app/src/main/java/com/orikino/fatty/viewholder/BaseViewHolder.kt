package com.orikino.fatty.viewholder

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.utils.TweakableOutlineProvider

abstract class BaseViewHolder<W>(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private lateinit var outlineProvider: TweakableOutlineProvider
    var mData: W? = null

    init {
        itemView.setOnClickListener(this)
    }


    //abstract fun bindData(data: W)

    abstract fun setData(data: W, position : Int)

    fun getData() : W{
        return mData!!
    }

    protected fun setUpButton(button : Button, onClickListener: View.OnClickListener){
        button.setOnClickListener(onClickListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    protected fun setUpButton(button : View, isShadow : Boolean){
        val cornerRadius = FattyApp.getInstance().resources.getDimensionPixelSize(R.dimen.control_corner_material).toFloat()
        outlineProvider = if (isShadow){
            TweakableOutlineProvider(cornerRadius = cornerRadius, scaleX =.5f, scaleY = .5f, yShift = 40)
        }else{
            TweakableOutlineProvider(cornerRadius = cornerRadius, scaleX =0f, scaleY = 0f, yShift = 0)
        }
        button.outlineProvider = outlineProvider
        var rect : Rect? = null
        var isCancel : Boolean = false
        button.setOnTouchListener { v, event ->
            when(event!!.action){
                MotionEvent.ACTION_DOWN ->{
                    button.animate()
                        .scaleX(0.95F)
                        .scaleY(0.95F)
                        .duration = 100
                    rect =
                        Rect(v.left, v.top, v.right, v.bottom)
                    isCancel = false
                }
                MotionEvent.ACTION_UP ->{
                    button.animate()
                        .scaleX(1F)
                        .scaleY(1F)
                        .duration = 200
                    if (!isCancel){
                        onClick(button)
                    }
                }

                MotionEvent.ACTION_CANCEL ->{
                    button.animate()
                        .scaleX(1F)
                        .scaleY(1F)
                        .duration = 200
                    isCancel = true
                }
                MotionEvent.ACTION_MOVE ->{
                    if((!rect!!.contains((v.left + event.x).toInt(), (v.top + event.y).toInt())) && !isCancel){
                        button.animate()
                            .scaleX(1F)
                            .scaleY(1F)
                            .duration = 200
                        isCancel = true
                    }
                }
                MotionEvent.ACTION_OUTSIDE ->{
                    button.animate()
                        .scaleX(1F)
                        .scaleY(1F)
                        .duration = 200
                    isCancel = true
                }

            }
            true
        }
    }
}