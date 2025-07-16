package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.RecommendRestaurantVO
import com.joy.fattyfood.domain.model.SearchFoodAndRestaurantsVO
import com.joy.fattyfood.domain.model.SearchFoodsVO

data class SearchFoodResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<SearchFoodsVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class SearchFoodAndRestaurantsResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : SearchFoodAndRestaurantsVO = SearchFoodAndRestaurantsVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class SearchRestaurantResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<RecommendRestaurantVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class FilterRestaurantResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<RecommendRestaurantVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
