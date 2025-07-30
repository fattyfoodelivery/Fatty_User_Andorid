package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class OptionVO(
    @SerializedName("item_name_mm")
    var item_name_mm : String ?= null,
    @SerializedName("item_name_en")
    var item_name_en : String ?= null,
    @SerializedName("item_name_ch")
    var item_name_ch : String ?= null,
    @SerializedName("food_sub_item_data_id")
    var food_sub_item_data_id : Int = 0,
    @SerializedName("food_sub_item_id")
    var food_sub_item_id : Int = 0,
    @SerializedName("food_sub_item_price")
    var food_sub_item_price : String = "",
    @SerializedName("instock")
    var instock : Int = 0,

    var isSelected : Boolean = false,
    var isSelectedAmount : Int = 0


    /*"food_sub_item_data_id":1346,
"food_sub_item_id":347,
"item_name":null,
"item_name_mm":"\u1001\u103d\u1000\u103a\u101e\u1031\u1038",
"item_name_en":"Small",
"item_name_ch":"\u5c0f\u4efd",
"food_sub_item_price":"0",
"instock":1,
"food_id":386,
"restaurant_id":20,
"created_at":"2022-05-26T15:22:59.000000Z",
"updated_at":"2022-05-26T15:22:59.000000Z"*/

)
