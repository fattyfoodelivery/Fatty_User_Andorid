package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.RecommendRestaurantVO

data class TopRelatedCategoryResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("meta")
    var meta : TopRelatedMeta,
    @SerializedName("data")
    var data : MutableList<RecommendRestaurantVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
)

data class TopRelatedMeta(
    @SerializedName("page")
    var page : Int = 0,
    @SerializedName("pageSize")
    var pageSize : Int = 0,
    @SerializedName("total")
    var total : Int = 0,
    @SerializedName("totalPages")
    var totalPages : Int = 0
)