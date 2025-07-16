package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.joy.fattyfood.data.repository.TrackFoodOrderRepository
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.viewstates.TrackFoodOrderViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackFoodOrderViewModel @Inject constructor(
    val repository: TrackFoodOrderRepository
) : ViewModel() {


    var viewState: MutableLiveData<TrackFoodOrderViewState> = MutableLiveData()
    var trackOrderLiveData: MutableLiveData<ActiveOrderVO> = MutableLiveData()
    var customer_latlng = LatLng(0.0,0.0)
    var rider_latlng = LatLng(0.0,0.0)
    var restaurant_latlng = LatLng(0.0,0.0)


    fun trackFoodOrder(order_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.trackFoodOrder(order_id)
                .onStart { viewState.postValue(TrackFoodOrderViewState.OnLoadingTrackFoodOrder) }
                .catch {
                    viewState.postValue(
                        TrackFoodOrderViewState.OnFailTrackFoodOrder(
                            it.localizedMessage ?: "Error"
                        )
                    )
                }
                .collect { viewState.postValue(TrackFoodOrderViewState.OnSuccessTrackFoodOrder(it)) }

        }
    }

    fun fetchRiderLocation(rider_id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchRiderLocation(rider_id)
                .catch {
                    viewState.postValue(
                        TrackFoodOrderViewState.OnFaiFetchRiderLocationTrackFoodOrder(
                            it.localizedMessage ?: "Error"
                        )
                    )
                }
                .collect { viewState.postValue(TrackFoodOrderViewState.OnSuccessFetchRiderLocation(it)) }
        }
    }
}