package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class NotiTypeVO(
    @SerializedName("menu")
    var menu : String,
    @SerializedName("noti_menu_key")
    var noti_menu_key : Int
)
