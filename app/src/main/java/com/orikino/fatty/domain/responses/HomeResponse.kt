package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.CategoryVO
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.model.UpAndDownVO
import com.orikino.fatty.domain.model.ZoneVO

data class HomeResponse(
 @SerializedName("success")
 var success : Boolean = false,

 @SerializedName("message")
 var message : String  = "",

 @SerializedName("wishlist_count")
 var wishlist_count : Int = 0,

 @SerializedName("zone_id")
 var zone_id : Int ?= 1,

 @SerializedName("near_restaurant")
 var near_restaurant : MutableList<NearByRestaurantVO> = mutableListOf(),

 @SerializedName("categories")
 var categories : MutableList<CategoryVO> = mutableListOf(),

 @SerializedName("recommend_restaurant")
 var recommend_restaurant : MutableList<RecommendRestaurantVO> = mutableListOf(),

 @SerializedName("upanddown_ads")
 var upanddown_ads : MutableList<UpAndDownVO> = mutableListOf(),

 @SerializedName("customer")
 var customer : CustomerVO?= null,


 @SerializedName("zone")
 var zone : ZoneVO? = null
)

