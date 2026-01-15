package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class ShopByCategoryResponse(
    @SerializedName("success")
    var success : Boolean = false,

    @SerializedName("message")
    var message : String  = "",

    @SerializedName("meta")
    var meta : MetaData,

    @SerializedName("data")
    var data : List<ShopData>? = null
)

data class MetaData(
    @SerializedName("page")
    var page : Int = 0,

    @SerializedName("page_size")
    var page_size : Int = 0,

    @SerializedName("total")
    var total : Int = 0,

    @SerializedName("total_pages")
    var total_pages : Int = 0
)

data class ShopData(
    @SerializedName("store_id")
    var store_id : Int = 0,

    @SerializedName("listing_type")
    var listing_type : Int? = 0,

    @SerializedName("name")
    var name : String = "",

    @SerializedName("domain")
    var domain : String = "",

    @SerializedName("open_time")
    var open_time : String = "",

    @SerializedName("close_time")
    var close_time : String = "",

    @SerializedName("image")
    var image : String = "",

    @SerializedName("latitude")
    var latitude : Double = 0.0,

    @SerializedName("longitude")
    var longitude : Double = 0.0,

    @SerializedName("address")
    var address : String = "",

    @SerializedName("distance")
    var distance : Double = 0.0,

    @SerializedName("service_category")
    var service_category : ServiceCategoryItem = ServiceCategoryItem(),

    //ads part
    @SerializedName("display_type_id")
    var display_type_id : Int = 0,

    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,

    @SerializedName("display_type_description")
    var display_type_description : String = "",

    @SerializedName("restaurant_name")
    var restaurant_name : String = "",

    @SerializedName("display_type_image")
    var display_type_image : String = "",

    //for loading view
    var isLoadingView : Boolean = false
)