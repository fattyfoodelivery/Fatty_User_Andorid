package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.RecommendRestaurantVO

data class TopRelatedCategoryResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<RecommendRestaurantVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
)