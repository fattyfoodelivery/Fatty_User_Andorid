package com.orikino.fatty.domain.model

import java.io.File

data class ParcelInfoVO(
    val parcel_id: Int = 0,
    val parcel_type: String = "",
    val weight: Double = 0.0,
    val item_qty: Int = 0,
    val payment_method_id: Int = 0,
    val ExtraCoverVO: ExtraCoverVO = ExtraCoverVO(),
    var pictureList: MutableList<File> = mutableListOf<File>(),
    val message: String = "",
)
