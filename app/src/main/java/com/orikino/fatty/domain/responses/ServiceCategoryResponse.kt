package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class ServiceCategoryResponse(
    @SerializedName("success")
    var success : Boolean = false,

    @SerializedName("message")
    var message : String  = "",

    @SerializedName("data")
    var data : ServiceCategory? = null
)

data class ServiceCategory(
    @SerializedName("service_item")
    var service_item : ServiceItem,
    @SerializedName("service_categories")
    var service_categories : List<ServiceCategoryItem>
)

data class ServiceCategoryItem(
    @SerializedName("service_category_id")
    var service_category_id : Int = 0,

    @SerializedName("name")
    var name : String = "",

    @SerializedName("image")
    var image : String? = null
)