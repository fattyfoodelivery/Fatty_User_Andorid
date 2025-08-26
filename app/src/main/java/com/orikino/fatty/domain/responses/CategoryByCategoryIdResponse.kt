package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.model.RestaurantFoodVO

data class CategoryByCategoryIdResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<NearByRestaurantVO> = mutableListOf(),   // RecommendRestaurantVO to NearByRestaurantVO
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class RecommendedRestaurantListResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<RecommendRestaurantVO> = mutableListOf(),   // RecommendRestaurantVO to NewRecommendRestaurantVO
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class RestaurantFoodResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<RestaurantFoodVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
