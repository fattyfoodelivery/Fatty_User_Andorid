package com.joy.fattyfood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.joy.fattyfood.databinding.ItemAppGuideBinding
import com.joy.fattyfood.domain.responses.TutorialVO
import com.joy.fattyfood.utils.delegate.ItemStringDelegate
import com.joy.fattyfood.viewholder.BaseViewHolder
import com.joy.fattyfood.viewholder.PlayGuideViewHolder

class PlayerGuideAdapter(context: Context, private var delegate: ItemStringDelegate) : BaseAdapter<PlayGuideViewHolder, TutorialVO>(context){
    private lateinit var binding: ItemAppGuideBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TutorialVO> {

        binding = ItemAppGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayGuideViewHolder(binding,delegate)
    }
}