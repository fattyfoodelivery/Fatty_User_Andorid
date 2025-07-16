package com.joy.fattyfood.domain.model

data class CustomerTypeVO(
    val customer : CustomerVO?,
    var is_old : Boolean = false,
    var message : String  = "",
    var code : Int = 0,
    var type : Int = 0
)
