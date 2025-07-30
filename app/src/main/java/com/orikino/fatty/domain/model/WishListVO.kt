package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class WishListVO(
    @SerializedName("customer_wishlist_id")
    var customer_wishlist_id: Int = 0,
    @SerializedName("restaurant")
    var restaurant: RecommendRestaurantVO? = null
)
