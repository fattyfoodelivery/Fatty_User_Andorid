package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.joy.fattyfood.data.repository.TrackParcelRepository
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.viewstates.TrackParcelOrderViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackParcelOrderViewModel @Inject constructor(
    private val repository : TrackParcelRepository
) : ViewModel() {


    var viewState: MutableLiveData<TrackParcelOrderViewState> = MutableLiveData()
    var trackParcelOrderLiveData: MutableLiveData<ActiveOrderVO> = MutableLiveData()
    var customer_latlng = LatLng(0.0,0.0)
    var rider_latlng = LatLng(0.0,0.0)
    var destination_latlng = LatLng(0.0,0.0)


    fun trackParcelOrder(order_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.trackParcelOrder(order_id)
                .onStart { viewState.postValue(TrackParcelOrderViewState.OnLoadingTrackParcelOrder) }
                .catch {
                    viewState.postValue(
                        TrackParcelOrderViewState.OnFailTrackParcelOrder(
                            it.localizedMessage ?: "Error"
                        )
                    )
                }
                .collect { viewState.postValue(TrackParcelOrderViewState.OnSuccessTrackParcelOrder(it)) }

        }
    }

    fun fetchRiderLocation(rider_id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchRiderLocation(rider_id)
                .catch {
                    viewState.postValue(
                        TrackParcelOrderViewState.OnFaiFetchRiderLocation(
                            it.localizedMessage ?: "Error"
                        )
                    )
                }
                .collect { viewState.postValue(TrackParcelOrderViewState.OnSuccessFetchRiderLocation(it)) }
        }
    }
}