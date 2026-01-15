package com.orikino.fatty.data.repository

import com.orikino.fatty.data.apiService.ServiceItemService
import com.orikino.fatty.domain.responses.ServiceCategoryResponse
import com.orikino.fatty.domain.responses.ServiceItemResponse
import com.orikino.fatty.domain.responses.ShopByCategoryResponse
import com.orikino.fatty.domain.responses.ShopWebLinkResponse
import retrofit2.Response
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceItemService: ServiceItemService
): ServiceRepository {
    override suspend fun fetchServiceItem(): Response<ServiceItemResponse> {
        return serviceItemService.fetchServiceItem()
    }

    override suspend fun fetchServiceCategory(service_item_id: Int): Response<ServiceCategoryResponse> {
        return serviceItemService.fetchServiceCategory(service_item_id)
    }

    override suspend fun fetchShopByCategory(
        serviceItemID: Int,
        sortBy: String,
        latitude: Double,
        longitude: Double,
        serviceCategoryID: Int?,
        searchKey: String?,
        page: Int,
        pageSize: Int
    ): Response<ShopByCategoryResponse> {
        return serviceItemService.fetchShopsByCategory(serviceItemID, sortBy, latitude, longitude, serviceCategoryID, searchKey, page, pageSize)
    }

    override suspend fun fetchShopWebLink(
        store_id: Int,
        customer_id: Int
    ): Response<ShopWebLinkResponse> {
        return serviceItemService.fetchShopWebLink(store_id, customer_id)
    }
}