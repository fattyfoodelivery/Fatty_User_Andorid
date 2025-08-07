package com.orikino.fatty.ui.views.base

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<itemType , viewType : NewBaseViewHolder<itemType>>(context : Context, diffUtil : DiffUtil.ItemCallback<itemType>) :
    ListAdapter<itemType , viewType>(diffUtil) {

    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    protected var updatedIndex: Int = -1
    protected var mCurrentPage: Int = 0

    override fun onBindViewHolder(holder: viewType, position: Int) {
        val currentList = currentList // Access the internal list managed by ListAdapter
        if (currentList.isNotEmpty()) {
            holder.setData(currentList[position], mCurrentPage)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun getItemAt(position: Int): itemType? {
        return if (position < currentList.size - 1) currentList[position] else null
    }

    fun setCurrentPage(page: Int) {
        mCurrentPage = page
    }
}