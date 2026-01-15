package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.ServiceCategoryResponse
import com.orikino.fatty.domain.responses.ServiceItemResponse
import com.orikino.fatty.domain.responses.ShopByCategoryResponse
import com.orikino.fatty.domain.responses.ShopWebLinkResponse
import retrofit2.Response

interface ServiceRepository {
    suspend fun fetchServiceItem(
    ) : Response<ServiceItemResponse>

    suspend fun fetchServiceCategory(
        service_item_id : Int
    ) : Response<ServiceCategoryResponse>

    suspend fun fetchShopByCategory(
        serviceItemID : Int,
        sortBy : String,
        latitude : Double,
        longitude : Double,
        serviceCategoryID : Int?,
        searchKey : String?,
        page : Int,
        pageSize : Int
    ) : Response<ShopByCategoryResponse>

    suspend fun fetchShopWebLink(
        store_id : Int,
        customer_id : Int
    ) : Response<ShopWebLinkResponse>
}