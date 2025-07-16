package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class OnBoardAdsVO(
    @SerializedName("merchant_ads_id")
    var merchant_ads_id : Int = 0,
    @SerializedName("display_type_id")
    var display_type_id : Int = 0,
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0 ,
    @SerializedName("display_type_image")
    var display_type_image : String = "",
    @SerializedName("display_type_description")
    var display_type_description : String = "",
    @SerializedName("ads_type_id")
    var ads_type_id : Int = 0,
    @SerializedName("image")
    var image : String = "",
    @SerializedName("restaurant_name")
    var restaurant_name : String = ""


  /*  "display_type_id": 4,
"image": "https://fatty-spaces.sgp1.cdn.digitaloceanspaces.com/testing/merchant_ads//1704523096.jpeg",
"merchant_ads_id": null,
"restaurant_id": null,
"display_type_image": null,
"display_type_description": null,
"ads_type_id": null,
"restaurant_name": null*/
)
