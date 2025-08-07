package com.orikino.fatty.ui.views.delegate

import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.domain.model.OptionVO

interface AddOnItemListDelegate {
    fun onSelectItem(data : OptionVO, subItem : FoodSubItemVO, isRequire : Int)
}