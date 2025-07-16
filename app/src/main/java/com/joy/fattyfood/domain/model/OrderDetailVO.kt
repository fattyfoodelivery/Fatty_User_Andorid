package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class OrderDetailVO(
    @SerializedName("restaurant_review_details")
    var restaurant_review_details: RatingDetailVO = RatingDetailVO(),
    @SerializedName("rider_review_details")
    var rider_review_details: RatingDetailVO = RatingDetailVO(),
)
