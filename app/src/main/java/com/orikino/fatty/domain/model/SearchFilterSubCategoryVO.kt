package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class SearchFilterSubCategoryVO(
    @SerializedName("restaurant_category_id")
    var restaurant_category_id : Int  = 0,
    @SerializedName("category_name")
    var category_name : String  = "",
)

data class FilterCategorySearch(
    @SerializedName("restaurant_category_id")
    var restaurant_category_id : Int  = 0,
)

data class FilterDishVO(
    @SerializedName("category_id")
    var category_id : Int = 0
)

