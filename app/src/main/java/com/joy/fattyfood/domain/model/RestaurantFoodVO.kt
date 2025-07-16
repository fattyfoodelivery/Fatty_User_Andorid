package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class RestaurantFoodVO(
    @SerializedName("food_id")
    var food_id : Int = 0,
    @SerializedName("food_name")
    var food_name : String ="",
    @SerializedName("food_price")
    var food_price : String  ="",
    @SerializedName("food_image")
    var food_image : String ="",
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("sub_item")
    var sub_item : MutableList<FoodSubItemVO> = mutableListOf()
)
