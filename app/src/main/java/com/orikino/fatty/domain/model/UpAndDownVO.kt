package com.orikino.fatty.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpAndDownVO(
    @SerializedName("merchant_ads_id")
    val merchant_ads_id : Int = 0,
    @SerializedName("restaurant_id")
    val restaurant_id : Int = 0,
    @SerializedName("image")
    val image : String = "",
    @SerializedName("display_type_id")
    val display_type_id : Int = 0 ,
    @SerializedName("display_type_image")
    val display_type_image : String = "",
    @SerializedName("display_type_description")
    val display_type_description : String = "",
    @SerializedName("restaurant_name")
    var restaurant_name : String = ""
) : Parcelable
