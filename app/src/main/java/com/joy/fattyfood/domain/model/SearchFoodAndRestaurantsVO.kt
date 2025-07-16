package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class SearchFoodAndRestaurantsVO(
    @SerializedName("food")
    var food : MutableList<SearchFoodsVO> = mutableListOf(),
    @SerializedName("restaurant")
    var restaurant : MutableList<RecommendRestaurantVO> = mutableListOf(),
)
