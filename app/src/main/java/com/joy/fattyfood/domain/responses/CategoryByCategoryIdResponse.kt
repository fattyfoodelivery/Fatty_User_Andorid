package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.RecommendRestaurantVO
import com.joy.fattyfood.domain.model.RestaurantFoodVO

data class CategoryByCategoryIdResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<RecommendRestaurantVO> = mutableListOf(),   // RecommendRestaurantVO to NearByRestaurantVO
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
