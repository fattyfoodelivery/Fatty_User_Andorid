package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.OrderDetailVO

data class OrderDetailResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: OrderDetailVO = OrderDetailVO(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)

/*data class OrderDetail(
    @SerializedName("restaurant_review_details")
    var restaurant_review_details: RatingDetail = RatingDetail(),
    @SerializedName("rider_review_details")
    var rider_review_details: RatingDetail = RatingDetail(),
)*/

/*data class RatingDetail(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("order_id")
    var order_id: Int = 0,
    @SerializedName("star")
    var star: String = "*",
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
)*/
