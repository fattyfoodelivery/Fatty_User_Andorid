package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class CustomerWishListVO(
    @SerializedName("customer_wishlist_id")
    var customer_wishlist_id : Int =0,
    @SerializedName("restaurant_id")
    var restaurant_id : Int =0,
    @SerializedName("is_wish")
    var is_wish : Boolean = false,
    @SerializedName("wishlist_count")
    var wishlist_count : Int = 0
)
