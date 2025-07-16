package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.data.repository.OrderRepository
import com.joy.fattyfood.domain.model.*
import com.joy.fattyfood.domain.viewstates.OrderViewState
import com.joy.fattyfood.utils.Constants
import com.joy.fattyfood.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel(){

    var cartList: MutableLiveData<MutableList<CreateFoodVO>> =
        MutableLiveData(PreferenceUtils.readFoodOrderList())
    var manageAddressLiveDataList: MutableLiveData<MutableList<CustomerAddressVO>> =
        MutableLiveData()
    var paymentMethodID: Int = 1
    var isActivate = false
    var isLoading = false
    var deliveryFeeOrigin = 0.0
    var deliveryFee = 0.0
    var lat = 0.0
    var lng = 0.0
    var address = ""
    var addressID = 0
    var customerAddressPhone: String? = null
    var shouldFetchData = true
    var foodAddOn = ""
    var fooAddOnList = mutableListOf<String>()

    var plus: Int = 0
    var minus: Int = 0
    var price = 0.0
    var originalPrice = 0.0
    var qty :MutableLiveData<Int> ?= MutableLiveData()
    var foodOrderList = mutableListOf<CreateFoodVO>()
    var subItemTotalPrice = 0.0
    var updateSubItemTotalPrice : MutableLiveData<Boolean> = MutableLiveData(true)

    var trackOrderLiveData: MutableLiveData<ActiveOrderVO> = MutableLiveData()

    var viewState: MutableLiveData<OrderViewState> = MutableLiveData()

    var type = "food"
    var date = ""
    var currentPage = 1
    var lastPage = 1

    init {
        getCurrentDate()
    }

    fun getCurrentDate() {
        date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    }

    fun fetchOrderDetailForReviews(order_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(OrderViewState.OnLoadingOrderDetailForReview)
            try {
                val response = orderRepository.orderDetailReview(order_id)
                if (response.isSuccessful) {

                    response.body()?.let {
                        viewState.postValue(OrderViewState.OnSuccessOrderDetailForReview(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(OrderViewState.OnFailOrderDetailForReview("Server Issue")) }
                        403 -> { viewState.postValue(OrderViewState.OnFailOrderDetailForReview("Denied")) }
                        406 -> { viewState.postValue(OrderViewState.OnFailOrderDetailForReview("Another Login")) }
                        else -> {
                            viewState.postValue(OrderViewState.OnFailOrderDetailForReview("Failed"))
                        }
                    }
                }
            }catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailOrderDetailForReview(e.localizedMessage))
            }
        }
    }

    /*fun fetchOrderHistory(customer_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                orderRepository.fetchOrderHistoryList(customer_id,"food","28-03-2024",1)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }*/

    fun fetchFoodParcelOrderHistory(customer_id: Int,) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(OrderViewState.OnLoadingMyOrder)
            try {
                val response = orderRepository.myOrderHistory(
                    customer_id = customer_id,
                    date = date,
                    page = currentPage
                )
                if (response.isSuccessful) {

                    response.body()?.let {
                        viewState.postValue(OrderViewState.OnSuccessMyOrder(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(OrderViewState.OnFailMyOrder("Server Issue")) }
                        403 -> { viewState.postValue(OrderViewState.OnFailMyOrder("Denied")) }
                        406 -> { viewState.postValue(OrderViewState.OnFailMyOrder("Another Login")) }
                        else -> { viewState.postValue(OrderViewState.OnFailMyOrder("Failed"))}
                    }
                }
            }catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailMyOrder(e.localizedMessage))
            }
        }
    }


    fun fetchPaymentList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var response  = orderRepository.fetchPaymentMethods()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(OrderViewState.OnSuccessPaymentMethod(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(OrderViewState.OnFailPaymentMethod("Server Issue")) }
                        403 -> { viewState.postValue(OrderViewState.OnFailPaymentMethod("Denied")) }
                        406 -> { viewState.postValue(OrderViewState.OnFailPaymentMethod("Another Login")) }
                        else -> { viewState.postValue(OrderViewState.OnFailPaymentMethod("Failed")) }
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailPaymentMethod(e.localizedMessage))
            }

        }
    }

    fun createFoodOrder(
        customer_id: Int,
        restaurant_id : Int,
        order_description : String,
        food_list : String,
        customer_address_id : Int,
        delivery_fee_origin : Double,
        delivery_fee : Double,
        item_total_price : Double,
        bill_total_price : Double,
        customer_address_latitude : Double,
        customer_address_longitude : Double,
        payment_method_id : Int,
        current_address : String,
        customer_address_phone : String,
        abnormal_fee: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(OrderViewState.OnLoadingCreateFoodOrder)
            try {
              var response = orderRepository.createFoodOrder(
                  customer_id, restaurant_id,
                  order_description,
                  food_list,
                  customer_address_id,
                  delivery_fee_origin,
                  delivery_fee,
                  item_total_price,
                  bill_total_price,
                  customer_address_latitude,
                  customer_address_longitude,
                  payment_method_id, current_address, customer_address_phone, abnormal_fee
              )
             if (response.isSuccessful) {
                 response.body()?.let {
                     viewState.postValue(OrderViewState.OnSuccessCreateFoodOrder(it))
                 }
             } else {
                 when(response.code()) {
                     500 -> { viewState.postValue(OrderViewState.OnFailCreateFoodOrder("Server Issue")) }
                     403 -> { viewState.postValue(OrderViewState.OnFailCreateFoodOrder("Denied")) }
                     406 -> { viewState.postValue(OrderViewState.OnFailCreateFoodOrder("Another Login")) }
                     else -> { viewState.postValue(OrderViewState.OnFailCreateFoodOrder("Failed")) }
                 }
             }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailCreateFoodOrder(e.localizedMessage))
            }

        }

    }

    fun foodDeliveryFee(
        latitude : Double,
        longitude : Double,
        restaurant_id : Int,
        item_total_price : Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var response  = orderRepository.foodDeliveryFee(latitude, longitude, restaurant_id, item_total_price)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(OrderViewState.OnSuccessFoodDeliveryFee(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(OrderViewState.OnFailFoodDeliveryFee("Server Issue")) }
                        403 -> { viewState.postValue(OrderViewState.OnFailFoodDeliveryFee("Denied")) }
                        406 -> { viewState.postValue(OrderViewState.OnFailFoodDeliveryFee("Another Login")) }
                        else -> { viewState.postValue(OrderViewState.OnFailFoodDeliveryFee("Failed")) }
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailFoodDeliveryFee(e.localizedMessage))
            }
        }
    }

    fun refreshFoodDeliveryFee(
        latitude : Double,
        longitude : Double,
        restaurant_id : Int,
        item_total_price : Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var response  = orderRepository.foodDeliveryFee(latitude, longitude, restaurant_id, item_total_price)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(OrderViewState.OnSuccessRefreshFoodDeliveryFee(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(OrderViewState.OnFailRefreshFoodDeliveryFee("Server Issue")) }
                        403 -> { viewState.postValue(OrderViewState.OnFailRefreshFoodDeliveryFee("Denied")) }
                        406 -> { viewState.postValue(OrderViewState.OnFailRefreshFoodDeliveryFee("Another Login")) }
                        else -> { viewState.postValue(OrderViewState.OnFailRefreshFoodDeliveryFee("Failed"))  }
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailRefreshFoodDeliveryFee(e.localizedMessage))
            }
        }
    }

    fun fetchCustomerAddressList(
        customer_id: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderRepository.fetchCustomerAddressList(customer_id)
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(OrderViewState.OnSuccessManageAddressList(it)) }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(OrderViewState.OnFailManageAddressList("Server Issue")) }
                        403 -> { viewState.postValue(OrderViewState.OnFailManageAddressList("Denied")) }
                        406 -> { viewState.postValue(OrderViewState.OnFailManageAddressList("Another Login")) }
                        else -> { viewState.postValue(OrderViewState.OnFailManageAddressList("Failed")) }
                    }
                }
            }catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(OrderViewState.OnFailManageAddressList(e.localizedMessage))
            }
        }
    }
    fun fetchOrderCancel(order_id: Int) {
            viewModelScope.launch(Dispatchers.IO) {
                viewState.postValue(OrderViewState.OnLoadingTrackFoodOrder)
                try {
                    val response = orderRepository.foodOrderCancel(order_id)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            viewState.postValue(OrderViewState.OnSuccessOrderCancel(it))
                        }
                    } else {
                        when (response.code()) {
                            500 -> {
                                viewState.postValue(OrderViewState.OnFailOrderCancel(Constants.SERVER_ISSUE))
                            }

                            403 -> {
                                viewState.postValue(OrderViewState.OnFailOrderCancel(Constants.DENIED))
                            }

                            406 -> {
                                viewState.postValue(OrderViewState.OnFailOrderCancel(Constants.ANOTHER_LOGIN))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    viewState.postValue(OrderViewState.OnFailOrderCancel(Constants.ANOTHER_LOGIN))
                }
            }
        }

    fun trackFoodOrder(order_id: Int) {
            viewModelScope.launch {
                try {
                    val response = orderRepository.trackFoodOrder(order_id)
                    viewState.postValue(OrderViewState.OnLoadingTrackFoodOrder)
                    if (response.isSuccessful) {
                        response.body()?.let { viewState.postValue(OrderViewState.OnSuccessTrackFoodOrder(it)) }
                    } else {
                        when (response.code()) {
                            500 -> { viewState.postValue(OrderViewState.OnFailTrackFoodOrder("Server Issue")) }
                            403 -> { viewState.postValue(OrderViewState.OnFailTrackFoodOrder("Denied")) }
                            406 -> { viewState.postValue(OrderViewState.OnFailTrackFoodOrder("Another Login")) }
                            else -> { viewState.postValue(OrderViewState.OnFailTrackFoodOrder("Failed")) }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    viewState.postValue(OrderViewState.OnFailTrackFoodOrder(e.localizedMessage))
                }
            }
        }

}