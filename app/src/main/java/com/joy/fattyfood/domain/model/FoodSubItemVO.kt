package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class FoodSubItemVO(
    @SerializedName("food_sub_item_id")
    var food_sub_item_id : Int = 0,
    @SerializedName("section_name_mm")
    var section_name_mm : String ?= null,
    @SerializedName("section_name_en")
    var section_name_en : String ?= null,
    @SerializedName("section_name_ch")
    var section_name_ch : String ?= null,
    @SerializedName("item_name")
    var item_name : String = "",
    @SerializedName("food_sub_item_price")
    var food_sub_item_price : Int = 0,
    @SerializedName("required_type")
    var required_type : Int = 0,
    @SerializedName("option")
    var option : MutableList<OptionVO> = mutableListOf()

    /*"required_type":1,
"food_id":385,
"food_sub_item_id":343,
"section_name_mm":"\u1006\u102d\u102f\u1012\u103a\u101b\u103d\u1031\u1038\u1001\u103b\u101a\u103a\u101b\u1014\u103a",
"section_name_en":"Choice of size",
"section_name_ch":"\u9009\u62e9\u5206\u91cf",*/


)
