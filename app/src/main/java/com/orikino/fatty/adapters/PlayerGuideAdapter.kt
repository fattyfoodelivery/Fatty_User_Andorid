package com.orikino.fatty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.orikino.fatty.databinding.ItemAppGuideBinding
import com.orikino.fatty.domain.responses.TutorialVO
import com.orikino.fatty.utils.delegate.ItemStringDelegate
import com.orikino.fatty.viewholder.BaseViewHolder
import com.orikino.fatty.viewholder.PlayGuideViewHolder

class PlayerGuideAdapter(context: Context, private var delegate: ItemStringDelegate) : BaseAdapter<PlayGuideViewHolder, TutorialVO>(context){
    private lateinit var binding: ItemAppGuideBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TutorialVO> {

        binding = ItemAppGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayGuideViewHolder(binding,delegate)
    }
}