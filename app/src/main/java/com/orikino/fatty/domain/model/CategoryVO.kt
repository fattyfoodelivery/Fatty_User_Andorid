package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class CategoryVO(
    @SerializedName("category_assign_id")
    var category_assign_id : Int = 0 ,
    @SerializedName("restaurant_category_id")
    var restaurant_category_id : Int = 0,
    @SerializedName("restaurant_category_name_mm")
    var restaurant_category_name_mm :String ?= null,
    @SerializedName("restaurant_category_name_en")
    var restaurant_category_name_en : String ?= null,
    @SerializedName("restaurant_category_name_ch")
    var restaurant_category_name_ch : String ?= null,
    @SerializedName("restaurant_category_image")
    var restaurant_category_image : String =""
)