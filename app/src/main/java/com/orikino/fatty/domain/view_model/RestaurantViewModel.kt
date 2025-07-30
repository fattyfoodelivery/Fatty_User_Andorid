package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.domain.viewstates.RestaurantViewState
import com.orikino.fatty.data.repository.RestaurantRepository
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val repository: RestaurantRepository
) : ViewModel() {

    var viewState: MutableLiveData<RestaurantViewState> = MutableLiveData()
    var foodMenuByRestaurantLiveDataList: MutableLiveData<FoodMenuByRestaurantVO> = MutableLiveData()
    var isEmptyCart: MutableLiveData<Boolean> = MutableLiveData(PreferenceUtils.readAddToCart())
    var isAnimate = false

    fun fetchFoodMenuByRestaurant(restaurantId: Int,customer_id : Int,latitude : Double,longitude : Double) {

        viewModelScope.launch (Dispatchers.IO){
            viewState.postValue(RestaurantViewState.OnLoadingFoodMenuByRestaurant)
            try {
                val response = repository.fetchFoodMenuByRestaurant(restaurantId, customer_id,latitude,longitude)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(RestaurantViewState.OnSuccessFoodMenuByRestaurant(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(RestaurantViewState.OnFailFoodMenuByRestaurant("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(RestaurantViewState.OnFailFoodMenuByRestaurant("Denied"))
                        }

                        406 -> {
                            viewState.postValue(RestaurantViewState.OnFailFoodMenuByRestaurant("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState.postValue(RestaurantViewState.OnFailFoodMenuByRestaurant(e.localizedMessage))
            }
        }

    }

}
