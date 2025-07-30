package com.orikino.fatty.domain.model

data class RestaurantsVO(
    var restId : Int =0,
    var restName : String ="",
    var restCover : Int =0
)



data class MenuByRestaurantVO(
    var menuId : Int,
    var menuName : String
)






data class PicturesVO(
    val uri: Any
)
