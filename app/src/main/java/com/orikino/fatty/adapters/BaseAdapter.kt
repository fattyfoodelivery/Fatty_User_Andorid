package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.orikino.fatty.viewholder.BaseViewHolder
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseAdapter<T, W>(context: Context?) : RecyclerView.Adapter<BaseViewHolder<W>>() {

    // var mData: MutableList<W>? = null
    var holderlist: HashMap<Int, ViewHolder>? = null
    private var mLayoutInflator: LayoutInflater
    private val TYPE_LARGE = 1
    private val TYPE_SMALL = 2
    private val TYPE_ADS = 3
    private var position: Int = 0

    var mData: ArrayList<W> = arrayListOf()

    override fun getItemCount(): Int {
        return mData.count()
    }

    val items: List<W>
        get() {
            val data = mData
            return data
        }

    init {
        mData = ArrayList()
        mLayoutInflator = LayoutInflater.from(context)
        holderlist = HashMap()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<W>, position: Int) {
        holder.setData(mData[position], position)
    }

    /*override fun onBindViewHolder(holder: T, position: Int) {
        holder.bindData(mData[position])
    }*/
    /*override fun onBindViewHolder(holder: BaseViewHolder<W>, @SuppressLint("RecyclerView") position: Int) {
     */
    /*holder.setData(mData!![position],position)
        this.position = position
        if(!holderlist?.containsKey(position)!!){
            holderlist?.put(position,holder)
        }*/
    /*
    }*/

    open fun getViewByPosition(position: Int): ViewHolder? {
        return holderlist!![position]
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_LARGE
        } else {
            TYPE_SMALL
        }
    }

    fun getCurrentPosition(): Int {
        return position
    }

    /*override fun getItemCount(): Int {
        return mData!!.size
    }*/

    fun updatePos(newData: MutableList<W>) {
        mData.addAll(newData)
    }

    fun setNewData(newData: MutableList<W>) {
        mData.clear()
        mData.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(position: Int, data: W) {
        mData.add(position, data)
        notifyItemInserted(position)
    }

    fun appendNewData(newData: List<W>) {
        mData.addAll(newData)
        notifyDataSetChanged()
    }

    fun appendNewDataWithViewMore(newData: List<W>, data: W) {
        mData.addAll(newData)
        mData.add(data)
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): W? {
        return if (position <= mData.size - 1) mData[position] else null
    }

    fun removeData(data: W) {
        mData.remove(data)
        notifyDataSetChanged()
    }

    fun addNewData(data: W) {
        mData.add(data)
        notifyDataSetChanged()
    }

    fun clearData() {
        mData = ArrayList()
        notifyDataSetChanged()
    }

    fun getData(): W {
        return mData[position]
    }

    fun notifyItemChangedAtPosition(position: Int) {

        notifyItemChanged(position)
    }
}
