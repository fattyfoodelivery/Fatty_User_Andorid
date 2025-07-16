package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.SearchFilterCategoryVO

data class SearchCategoryFilterResponse(

    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<SearchFilterCategoryVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = ""
)



