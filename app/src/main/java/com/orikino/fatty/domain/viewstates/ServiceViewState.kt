package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.ServiceCategoryResponse
import com.orikino.fatty.domain.responses.ShopByCategoryResponse
import com.orikino.fatty.domain.responses.ShopWebLinkResponse

sealed class ServiceViewState {
    object OnLoadingServiceCategory : ServiceViewState()
    data class OnSuccessServiceCategory(val data : ServiceCategoryResponse) : ServiceViewState()
    data class OnFailServiceCategory(val message : String) : ServiceViewState()

    object OnLoadingShopByCategory : ServiceViewState()
    data class OnSuccessShopByCategory(val data : ShopByCategoryResponse) : ServiceViewState()
    data class OnFailShopByCategory(val message : String) : ServiceViewState()

    object OnListEndReachShop : ServiceViewState()

    object OnLoadingShopWebLink : ServiceViewState()
    data class OnSuccessShopWebLink(val data : ShopWebLinkResponse) : ServiceViewState()
    data class OnFailShopWebLink(val message : String) : ServiceViewState()
}