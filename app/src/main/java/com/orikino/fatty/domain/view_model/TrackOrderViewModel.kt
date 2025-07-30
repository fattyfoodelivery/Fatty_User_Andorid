package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.TrackOrderRepository
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.viewstates.OrderDetailWithRatingViewState
import com.orikino.fatty.domain.viewstates.TrackOrderViewState
import com.orikino.fatty.domain.viewstates.TrackParcelViewState
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TrackOrderViewModel @Inject constructor(
    private val repository: TrackOrderRepository
) : ViewModel() {

    var viewState: MutableLiveData<TrackOrderViewState> = MutableLiveData()
    var viewStateOrderDetail : MutableLiveData<OrderDetailWithRatingViewState> = MutableLiveData()
    var foodMenuByRestaurantLiveDataList: MutableLiveData<FoodMenuByRestaurantVO> = MutableLiveData()
    var isEmptyCart : MutableLiveData<Boolean> = MutableLiveData(PreferenceUtils.readAddToCart())
    var isAnimate = false

    /*fun trackFood(orderId : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =  repository.trackOrderFood(orderId)
                viewState.postValue(TrackOrderViewState.OnLoadingTrackFoodOrder)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(TrackOrderViewState.OnSuccessTrackFoodOrder(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> { viewState.postValue(TrackOrderViewState.OnFailTrackFoodOrder(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(TrackOrderViewState.OnFailTrackFoodOrder(Constants.DENIED)) }
                        406 -> { viewState.postValue(TrackOrderViewState.OnFailTrackFoodOrder(Constants.ANOTHER_LOGIN)) }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(TrackOrderViewState.OnFailTrackFoodOrder(Constants.CONNECTION_ISSUE))
            }
        }
    }*/

    var viewStateParcel : MutableLiveData<TrackParcelViewState> = MutableLiveData()
    fun trackParcel(orderId : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            viewStateParcel.postValue(TrackParcelViewState.OnLoadingTrackParcelOrder)
            try {
                  val response =  repository.trackOrderParcel(orderId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewStateParcel.postValue(TrackParcelViewState.OnSuccessTrackParcelOrder(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> { viewStateParcel.postValue(TrackParcelViewState.OnFailTrackParcelOrder("Server Issue")) }
                        403 -> { viewStateParcel.postValue(TrackParcelViewState.OnFailTrackParcelOrder("Connection Issue")) }
                        406 -> { viewStateParcel.postValue(TrackParcelViewState.OnFailTrackParcelOrder("Another Login")) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewStateParcel.postValue(TrackParcelViewState.OnFailTrackParcelOrder(e.localizedMessage))
            }
        }
    }

    fun fetchRiderLocation(rider_id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =  repository.fetchRiderLocation(rider_id)
                viewState.postValue(TrackOrderViewState.OnLoadingRiderLocation)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(TrackOrderViewState.OnSuccessRiderLocation(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> { viewState.postValue(TrackOrderViewState.OnFailRiderLocation(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(TrackOrderViewState.OnFailRiderLocation(Constants.DENIED)) }
                        406 -> { viewState.postValue(TrackOrderViewState.OnFailRiderLocation(Constants.ANOTHER_LOGIN)) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState.postValue(TrackOrderViewState.OnFailRiderLocation(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun orderDetailWithRating(orderId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnLoadingOrderDetailDetailWithRating)
            try {
                val response =  repository.trackOrderDetailWithRating(orderId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> { viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnFailOrderDetailWithRating("Server Issue")) }
                        403 -> { viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnFailOrderDetailWithRating("Denied")) }
                        406 -> { viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnFailOrderDetailWithRating("Another Login")) }
                        else -> { viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnFailOrderDetailWithRating("Failed")) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewStateOrderDetail.postValue(OrderDetailWithRatingViewState.OnFailOrderDetailWithRating(e.localizedMessage))
            }
        }
    }

    fun checkParcel(userId : Int,parcel_zone_id : Int) {

        viewModelScope.launch {
            viewStateParcel.postValue(TrackParcelViewState.OnLoadingCheckParcel)
            try {
                val response = repository.parcelOderCheck(userId, parcel_zone_id)

                if (response.isSuccessful) {
                    response.body()?.let {
                        viewStateParcel.postValue(TrackParcelViewState.OnSuccessCheckParcel(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewStateParcel.postValue(
                                TrackParcelViewState.OnFailCheckParcel("Server Issue")
                            )
                        }
                        403 -> { viewStateParcel.postValue(TrackParcelViewState.OnFailCheckParcel("Denied")) }

                        406 -> {
                            viewStateParcel.postValue(
                                TrackParcelViewState.OnFailCheckParcel("Another Login")
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewStateParcel.postValue(TrackParcelViewState.OnFailCheckParcel(e.localizedMessage))
            }
        }
    }
}