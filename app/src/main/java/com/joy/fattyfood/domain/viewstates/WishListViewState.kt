package com.joy.fattyfood.domain.viewstates

import  com.joy.fattyfood.domain.responses.*

sealed class WishListViewState{
    object OnLoadingWishList :  com.joy.fattyfood.domain.viewstates.WishListViewState()
    data class OnSuccessWishList(val data : WishListResponse) : com.joy.fattyfood.domain.viewstates.WishListViewState()
    data class OnFailWishList(val message: String) : com.joy.fattyfood.domain.viewstates.WishListViewState()

    object OnLoadingOperateWishList :  com.joy.fattyfood.domain.viewstates.WishListViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : com.joy.fattyfood.domain.viewstates.WishListViewState()
    data class OnFailOperateWishList(val message: String) : com.joy.fattyfood.domain.viewstates.WishListViewState()
}
