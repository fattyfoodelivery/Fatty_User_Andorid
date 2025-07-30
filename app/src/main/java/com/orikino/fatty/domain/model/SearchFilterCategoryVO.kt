package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class SearchFilterCategoryVO(
    @SerializedName("main_category")
    var main_category : String  = "",
    @SerializedName("sub_category")
    var sub_category : MutableList<SearchFilterSubCategoryVO>  = mutableListOf(),
)
