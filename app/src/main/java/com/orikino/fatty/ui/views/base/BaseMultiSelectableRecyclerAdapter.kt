package com.orikino.fatty.ui.views.base

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.joygroup.fooddeliveryuser.views.base.DiffUtils

abstract class BaseMultiSelectableRecyclerAdapter<itemType, viewType : BaseSelectableViewHolder<itemType>>(
    private val context: Context
) : RecyclerView.Adapter<viewType>() {

    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    protected var mData: MutableList<itemType> = ArrayList()

    private var isClickable = true

    fun setClickable(isClickable: Boolean) {
        this.isClickable = isClickable
    }

    private val selectedPositions: MutableSet<Int> = mutableSetOf()

    val items: List<itemType>
        get() = mData

    override fun onBindViewHolder(holder: viewType, position: Int) {
        if (mData.isNotEmpty()) {
            holder.setData(mData[position], selectedPositions.contains(position), isClickable)
        }
    }

    override fun getItemCount(): Int = mData.size

    fun getItemAt(position: Int): itemType? {
        return if (position in 0 until mData.size) mData[position] else null
    }

    fun toggleSelectedItem(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        notifyItemChanged(position)
    }

    fun isSelected(position: Int): Boolean {
        return selectedPositions.contains(position)
    }

    fun clearSelection() {
        val previousSelected = selectedPositions.toList()
        selectedPositions.clear()
        previousSelected.forEach { notifyItemChanged(it) }
    }

    fun getSelectedItems(): List<itemType> {
        return selectedPositions.mapNotNull { getItemAt(it) }
    }

    fun addNewData(newItem: itemType, position: Int) {
        mData.add(position, newItem)
        notifyDataSetChanged()
    }

    fun update(newDataList: List<itemType>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtils(newDataList, mData))
        mData.clear()
        mData.addAll(newDataList)
        selectedPositions.clear()
        diffResult.dispatchUpdatesTo(this)
    }

    fun appendNewData(newData: List<itemType>) {
        clearData()
        if (mData.isEmpty())
            mData.addAll(newData)
        else
            update(newData)
        notifyDataSetChanged()
    }

    fun removeData(data: itemType) {
        val index = mData.indexOf(data)
        if (index != -1) {
            mData.removeAt(index)
            selectedPositions.remove(index)
            notifyDataSetChanged()
        }
    }

    fun addNewData(data: itemType) {
        mData.add(data)
        notifyDataSetChanged()
    }

    fun addNewDataList(dataList: List<itemType>) {
        mData.addAll(dataList)
        notifyDataSetChanged()
    }

    fun clearData() {
        mData.clear()
        selectedPositions.clear()
        notifyDataSetChanged()
    }
}
