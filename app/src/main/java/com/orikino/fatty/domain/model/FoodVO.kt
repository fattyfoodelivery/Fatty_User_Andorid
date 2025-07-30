package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class FoodVO(

    @SerializedName("food_id")
    var food_id : Int = 0,
    @SerializedName("food_name_mm")
    var food_name_mm : String ?= null,
    @SerializedName("food_name_en")
    var food_name_en : String ?= null,
    @SerializedName("food_name_ch")
    var food_name_ch : String ?= null,
    @SerializedName("food_price")
    var food_price : String ?= null,
    @SerializedName("food_qty")
    var food_qty : Int = 0,
    @SerializedName("food_note")
    var food_note : String ="",
    @SerializedName("food_menu_id")
    var food_menu_id : Int = 0,
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("food_image")
    var food_image : String ?= null ,
    @SerializedName("food_emergency_status")
    var food_emergency_status : Int = 0,
    @SerializedName("sub_item")
    var sub_item : MutableList<FoodSubItemVO> = mutableListOf(),
    @SerializedName("is_cancel")
    var is_cancel : Int = 0
/*
    "food_id":385,
"food_name_mm":"Thai Milk Tea",
"food_name_en":"Thai Milk Tea",
"food_name_ch":"\u6cf0\u5f0f\u5976\u8336",
"food_menu_id":104,
"restaurant_id":20,
"food_price":"2100",
"food_image":"1653553905.jpg",
"food_emergency_status":0,
"food_recommend_status":0,
"sub_item":[]*/
)
data class CreateFoodVO(
    var local_food_id : Int =0,
    var restaurant_id : Int =0,
    var food_id: Int = 0,
    var food_name : String = "",
    var initial_price : Double = 0.0,
    var food_qty: Int = 0,
    var food_note: String? = null,
    var food_price: Double = 0.0,
    var sub_item: MutableList<CreateFoodSubItem> = mutableListOf()
)

data class CreateFoodOrderVO(
    var food_id: Int = 0,
    var food_qty: Int = 0,
    var food_note: String? = null,
    var food_price: Double = 0.0,
    var sub_item: MutableList<CreateFoodSubItem> = mutableListOf()
)

data class CreateFoodSubItem(
    var required_type: Int = 0,
    var food_sub_item_id: Int = 0,
    var option: MutableList<CreateFoodOption> = mutableListOf(),
)

data class CreateFoodOption(
    var food_sub_item_data_id : Int = 0,
    var food_sub_item_price : Double = 0.0,
    var item_name : String = ""
)

data class FoodDeliveryFeeVO(
    @SerializedName("delivery_fee_origin")
    var delivery_fee_origin: Double = 0.0,
    @SerializedName("delivery_fee")
    var delivery_fee: Double = 0.0,
    @SerializedName("restaurant_delivery_fee")
    var restaurant_delivery_fee: Double = 0.0,
    @SerializedName("define_amount")
    var define_amount: Double = 0.0,
    @SerializedName("system_deli_distance")
    var system_deli_distance: Double = 0.0,

    @SerializedName("abnormal_fee")
    var abnormal_fee: Double = 0.0
)