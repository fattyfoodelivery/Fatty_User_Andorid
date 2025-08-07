package com.orikino.fatty.ui.views.base

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.joygroup.fooddeliveryuser.views.base.DiffUtils
import java.util.ArrayList

abstract class BaseSelectableRecyclerAdapter<itemType , viewType : BaseSelectableViewHolder<itemType>>(
    private val context : Context
) :
    RecyclerView.Adapter<viewType>() {
    val inflater : LayoutInflater by lazy { LayoutInflater.from(context)}
    protected var mData: MutableList<itemType>? = null

    private var isClickable = true
    private var allowUnselection: Boolean = false // New flag

    fun setClickable(isClickable : Boolean) {
        this.isClickable = isClickable
    }

    // Public method to control unselection behavior
    fun setAllowUnselection(allow: Boolean) {
        this.allowUnselection = allow
    }

    var selectedPosition: Int = 0
        set(newSelectedPosition) {
            if (this.selectedPosition == newSelectedPosition) {
                if (allowUnselection) { // Only allow unselecting if the flag is true
                    field = -1
                } else {
                    // If unselection is not allowed, and it's the same item, do nothing
                    // The item remains selected.
                    return // Exit early, no change needed
                }
            } else {
                field = newSelectedPosition
            }
        }

    val items: List<itemType>
        get() = if (mData == null) ArrayList() else mData!!

    init {
        mData = ArrayList()
    }

    override fun onBindViewHolder(holder : viewType , position : Int) {
        if (mData!!.isNotEmpty()) {
            holder.setData(mData!![position] , this.selectedPosition == position, isClickable)
        }
    }

    override fun getItemCount(): Int {

        return mData!!.size
    }

    fun getItemAt(position : Int): itemType? {
        return if (position < mData!!.size + 1) mData!![position] else null

    }

    fun addNewData(newItem : itemType , position : Int) {
        if (mData != null) {
            mData!!.add(position , newItem)
            notifyDataSetChanged()
        }
    }

    fun update(newDataList : List<itemType>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtils(newDataList, mData!!))
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    fun appendNewData(newData : List<itemType>) {
        clearData()
        if (mData!!.isEmpty())
            mData!!.addAll(newData)
        else
            update(newData)
        notifyDataSetChanged()
    }

    fun removeData(data : itemType) {
        mData!!.remove(data)
        notifyDataSetChanged()
    }

    fun changeSelectedItem(currentSelectedPosition : Int){
        selectedPosition= currentSelectedPosition
        notifyDataSetChanged()
    }

    fun toggleSelectedItem(currentSelectedPosition : Int){
        // If unselection is not allowed and the item is already selected,
        // we might not need to notifyItemChanged for the old position if it's the same.
        // However, the current logic handles changes correctly.
        val oldSelectedPosition = selectedPosition
        selectedPosition = currentSelectedPosition

        // Only notify if the selection actually changed or if it was unselected (and allowUnselection is true)
        if (oldSelectedPosition != selectedPosition) {
            if (oldSelectedPosition != -1) {
                notifyItemChanged(oldSelectedPosition)
            }
            if (selectedPosition != -1) {
                notifyItemChanged(selectedPosition)
            } else if (allowUnselection && oldSelectedPosition != -1) { // Specifically for unselection
                 notifyItemChanged(oldSelectedPosition)
            }
        } else if (allowUnselection && oldSelectedPosition != -1 && selectedPosition == -1) {
            // Case: Item was selected, allowUnselection is true, and it became unselected.
            notifyItemChanged(oldSelectedPosition)
        }
        // If !allowUnselection and currentSelectedPosition was already selected,
        // selectedPosition setter ensures no change, so no notification spam.
    }

    fun addNewData(data : itemType) {
        mData!!.add(data)
        notifyDataSetChanged()
    }

    fun getSelected() : itemType? {
        return if (selectedPosition == -1) null else items[selectedPosition]
    }

    fun addNewDataList(dataList : List<itemType>) {
        mData!!.addAll(dataList)
        notifyDataSetChanged()
    }

    fun clearData() {
        mData = ArrayList()
        notifyDataSetChanged()
    }
}
