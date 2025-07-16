package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class ParcelAvailableRegionDataVO(
    @SerializedName("default_address")
    var default_address : CustomerAddressVO?= null,
    @SerializedName("recent_blocks")
    var recent_blocks : MutableList<ParcelAvailableRegionVO> = mutableListOf(),
    @SerializedName("block_lists")
    var block_lists : MutableList<ParcelAvailableRegionVO> = mutableListOf()
)
