package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.FoodMenuByRestaurantVO
import com.joy.fattyfood.domain.model.FoodMenuVO
import com.joy.fattyfood.domain.model.RecommendRestaurantVO

data class RestaurantDetailResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("restaurant")
    var restaurant : RecommendRestaurantVO = RecommendRestaurantVO(),
    @SerializedName("food_menu")
    var food_menu : MutableList<FoodMenuVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)


data class FoodMenuByRestaurantResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<FoodMenuByRestaurantVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)


