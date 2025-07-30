package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.OrderService
import com.orikino.fatty.domain.responses.FoodOrderCancelResponse
import retrofit2.Response
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderService: OrderService
) : OrderRepository{
    override suspend fun fetchOrderHistoryList(
        customer_id: Int,
        order_type : String,
        date : String,
        page : Int
    ): Response<OrderHistoriesResponse> = orderService.fetchOrderHistories(customer_id,order_type ,date,page)

    override suspend fun foodOrderCancel(order_id: Int):
            Response<FoodOrderCancelResponse> = orderService.foodOrderCancel(order_id)

    override suspend fun myOrderHistory(
        customer_id: Int,
        date: String,
        page: Int
    ): Response<MyOrderHistoryResponse> = orderService.orderHisFoodAndParcel(customer_id,date,page)

    override suspend fun fetchPaymentMethods(): Response<PaymentMethodResponse> = orderService.fetchPaymentMethodList()

    override suspend fun createFoodOrder(
        customer_id: Int,
        restaurant_id: Int,
        order_description: String,
        food_list: String,
        customer_address_id: Int,
        delivery_fee_origin: Double,
        delivery_fee: Double,
        item_total_price: Double,
        bill_total_price: Double,
        customer_address_latitude: Double,
        customer_address_longitude: Double,
        payment_method_id: Int,
        current_address: String,
        customer_address_phone: String,
        abnormal_fee: Double
    ): Response<OrderResponse> =
        orderService.createFoodOrder(
            customer_id,
            restaurant_id,
            order_description,
            food_list,
            customer_address_id,
            delivery_fee_origin,
            delivery_fee,
            item_total_price,
            bill_total_price,
            customer_address_latitude,
            customer_address_longitude,
            payment_method_id,
            current_address,
            customer_address_phone,
            abnormal_fee
        )

    override suspend fun foodDeliveryFee(
        latitude: Double,
        longitude: Double,
        restaurant_id: Int,
        item_total_price: Double
    ): Response<FoodDeliveryFeeResponse> = orderService.foodDeliveryFee(latitude,longitude,restaurant_id, item_total_price)

    override suspend fun fetchCustomerAddressList(customer_id: Int): Response<CustomerAddressListResponse>  =
        orderService.fetchCustomerAddressList(customer_id)

    override suspend fun trackFoodOrder(order_id: Int): Response<TrackFoodOrderResponse> = orderService.trackFoodOrder(order_id)
    override suspend fun orderDetailReview(order_id: Int): Response<OrderDetailResponse> = orderService.orderDetailForReview(order_id)
}