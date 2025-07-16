package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class RatingDetailVO(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("order_id")
    var order_id: Int = 0,
    @SerializedName("star")
    var star: Double? = 0.0,
    @SerializedName("options")
    var options: List<String>? = null,
    @SerializedName("comment")
    var comment: String = "",
    @SerializedName("review_date")
    var review_date: String = "",
    @SerializedName("customer_name")
    var customer_name: String = "",
    @SerializedName("customer_profile")
    var customer_profile: String? = null
)
