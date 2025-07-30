package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class FoodMenuVO(
    @SerializedName("food_menu_id")
    var food_menu_id : Int  =0,
    @SerializedName("food_menu_name")
    var food_menu_name : String = ""
)
