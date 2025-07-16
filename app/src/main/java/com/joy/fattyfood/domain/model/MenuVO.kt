package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class MenuVO(
    @SerializedName("food_menu_id")
    var food_menu_id: Int = 0,
    @SerializedName("food_menu_name_mm")
    var food_menu_name_mm: String ?= null,
    @SerializedName("food_menu_name_en")
    var food_menu_name_en: String ?= null,
    @SerializedName("food_menu_name_ch")
    var food_menu_name_ch: String ?= null,
    @SerializedName("food")
    var food: MutableList<FoodVO> = mutableListOf()
    /*@SerializedName("food_menu_id")
    var food_menu_id: Int = 0,
    @SerializedName("food_menu_name")
    var food_menu_name: String ?= null,
    @SerializedName("food_menu_name_mm")
    var food_menu_name_mm: String ?= null,
    @SerializedName("food_menu_name_en")
    var food_menu_name_en: String ?= null,
    @SerializedName("food_menu_name_ch")
    var food_menu_name_ch: String ?= null,
    @SerializedName("restaurant_id")
    var restaurant_id: Int = 0,
    @SerializedName("food")
    var food: MutableList<FoodVO> = mutableListOf()*/

   /* "food_menu_id":104,
"food_menu_name":null,
"food_menu_name_mm":"Bubble Milk Tea",
"food_menu_name_en":"Bubble Milk Tea",
"food_menu_name_ch":"Bubble Milk Tea",
"restaurant_id":20,
"created_at":"2022-05-24T11:52:36.000000Z",
"updated_at":"2022-07-19T17:52:26.000000Z",
"food":[}*/


)
