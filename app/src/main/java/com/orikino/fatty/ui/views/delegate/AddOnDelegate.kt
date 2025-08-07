package com.orikino.fatty.ui.views.delegate

import com.orikino.fatty.domain.model.CreateFoodVO

interface AddOnDelegate {
    fun onAddToCart()
    fun onDeleteItem(foodList: MutableList<CreateFoodVO>)
}