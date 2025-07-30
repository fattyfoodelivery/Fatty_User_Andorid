package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.orikino.fatty.R
import com.orikino.fatty.domain.model.LanguageVO


class LanguageAdapter(var context: Context,var lang : ArrayList<LanguageVO>) : BaseAdapter() {
    override fun getCount(): Int {
        return lang.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rootView = LayoutInflater.from(parent?.context).inflate(R.layout.item_language,parent,false)

        val txtName = rootView.findViewById<AppCompatTextView>(R.id.tv_lang)
        //val image = rootView.findViewById<AppCompatImageView>(R.id.img_lang)

        txtName.text = lang[position].language

        return rootView
    }


}