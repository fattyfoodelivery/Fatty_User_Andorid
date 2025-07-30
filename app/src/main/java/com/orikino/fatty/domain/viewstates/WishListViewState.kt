package com.orikino.fatty.domain.viewstates

import  com.orikino.fatty.domain.responses.*

sealed class WishListViewState{
    object OnLoadingWishList :  com.orikino.fatty.domain.viewstates.WishListViewState()
    data class OnSuccessWishList(val data : WishListResponse) : com.orikino.fatty.domain.viewstates.WishListViewState()
    data class OnFailWishList(val message: String) : com.orikino.fatty.domain.viewstates.WishListViewState()

    object OnLoadingOperateWishList :  com.orikino.fatty.domain.viewstates.WishListViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : com.orikino.fatty.domain.viewstates.WishListViewState()
    data class OnFailOperateWishList(val message: String) : com.orikino.fatty.domain.viewstates.WishListViewState()
}
