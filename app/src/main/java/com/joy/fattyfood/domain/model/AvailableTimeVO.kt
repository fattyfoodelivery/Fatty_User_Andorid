package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class AvailableTimeVO(
    @SerializedName("restaurant_available_time_id")
    var restaurant_available_time_id : Int = 0,
    @SerializedName("on_off")
    var on_off : Int = 0,
    @SerializedName("day")
    var day : String? =null,
    @SerializedName("opening_time")
    var opening_time : String? =null,
    @SerializedName("closing_time")
    var closing_time : String? =null,
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0

  /*  "restaurant_available_time_id":134,
"on_off":1,
"day":"Monday",
"opening_time":"09:30:00",
"closing_time":"17:30:00",
"restaurant_id":20,
"created_at":"2022-05-24T11:50:56.000000Z",
"updated_at":"2022-08-16T04:03:46.000000Z"*/
)
