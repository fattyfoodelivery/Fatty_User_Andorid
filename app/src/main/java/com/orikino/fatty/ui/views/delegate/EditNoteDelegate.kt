package com.orikino.fatty.ui.views.delegate

import com.orikino.fatty.domain.model.CreateFoodVO

interface EditNoteDelegate {
    fun onEdit(data: CreateFoodVO, pos: Int)
}