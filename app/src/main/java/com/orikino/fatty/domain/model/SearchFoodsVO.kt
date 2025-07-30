package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class SearchFoodsVO(
    @SerializedName("food_id")
    var food_id : Int = 0,
    @SerializedName("food_name_mm")
    var food_name_mm : String ?= null,
    @SerializedName("food_name_en")
    var food_name_en : String ?= null,
    @SerializedName("food_name_ch")
    var food_name_ch : String ?= null,
    @SerializedName("food_price")
    var food_price : String = "",
    @SerializedName("food_image")
    var food_image : String ="",
    @SerializedName("food_emergency_status")
    var food_emergency_status : Int =0,
    @SerializedName("food_recommend_status")
    var food_recommend_status : Int =0,
    @SerializedName("currency_type")
    var currency_type : String = "",
    @SerializedName("sub_item")
    var sub_item : MutableList<FoodSubItemVO> = mutableListOf(),
    @SerializedName("restaurant")
    var restaurant : RecommendRestaurantVO = RecommendRestaurantVO()
)
