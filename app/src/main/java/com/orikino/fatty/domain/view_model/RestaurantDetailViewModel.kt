package com.orikino.fatty.domain.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.RestaurantDetailRepository
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.viewstates.RestaurantDetailViewState
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val repository : RestaurantDetailRepository
) : ViewModel(){

    var foodMenuByRestaurantLiveDataList: MutableLiveData<FoodMenuByRestaurantVO> = MutableLiveData()
    var isEmptyCart: MutableLiveData<Boolean> = MutableLiveData(PreferenceUtils.readAddToCart())
    var isAnimate = false
    var viewState : MutableLiveData<RestaurantDetailViewState> = MutableLiveData()

    fun fetchFoodRestMenuByRestId(restId : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                PreferenceUtils.readUserVO().customer_id.let { }
                val response = repository.fetchFoodByRestaurantByMenuById(
                    restId,
                    PreferenceUtils.readUserVO().customer_id ?: 0,
                    PreferenceUtils.readUserVO().latitude ?: 0.0,
                    PreferenceUtils.readUserVO()?.longitude ?: 0.0,
                    )
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(RestaurantDetailViewState.OnSuccessRestFoodMenuByRestID(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestFoodMenuByRestID(
                            "Server Issue")) }
                        403 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestFoodMenuByRestID(
                            "Denied")) }
                        406 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestFoodMenuByRestID(
                            "Another Login")) }
                    }
                }
            }catch (e : Exception) {
                Log.d("TAG", "fetchFoodRestMenuByRestId: ${e.localizedMessage} ANNDN ${e.message}")
                viewState.postValue(RestaurantDetailViewState.OnFailRestFoodMenuByRestID(
                    e.localizedMessage))
            }
        }
    }

    fun fetchRestReviewList(restId : Int,page : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.fetchRestDetailReviewList(restId,page)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(RestaurantDetailViewState.OnSuccessRestReviewList(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestRestReviewList(
                            "Server Issue")) }
                        403 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestRestReviewList(
                            "Denied")) }
                        406 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestRestReviewList(
                            "Another Login")) }
                        404 -> { viewState.postValue(RestaurantDetailViewState.OnFailRestRestReviewList("not"))}
                    }
                }
            }catch (e : Exception) {
                Log.d("TAG", "fetchFoodRestMenuByRestId: ${e.localizedMessage} ANNDN ${e.message}")
                e.printStackTrace()
                viewState.postValue(RestaurantDetailViewState.OnFailRestRestReviewList(
                    e.localizedMessage))
            }
        }
    }

    fun fetchOperateWishList(restId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val response =  repository.fetchOperateWishList(PreferenceUtils.readUserVO()?.customer_id ?: 0,restId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(RestaurantDetailViewState.OnSuccessOperateList(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(RestaurantDetailViewState.OnFailOperateList(
                            Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(RestaurantDetailViewState.OnFailOperateList(
                            Constants.DENIED)) }
                        406 -> { viewState.postValue(RestaurantDetailViewState.OnFailOperateList(
                            Constants.ANOTHER_LOGIN)) }
                        404 -> { viewState.postValue(RestaurantDetailViewState.OnFailOperateList(""))}
                    }
                }
            }catch (e : Exception) {
                Log.d("TAG", "fetchFoodRestMenuByRestId: ${e.localizedMessage} ANNDN ${e.message}")
                viewState.postValue(RestaurantDetailViewState.OnFailOperateList(
                    Constants.CONNECTION_ISSUE))
            }
        }
    }
    fun fetchWishList(customerId: Int,latitude : Double,longitude : Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =  repository.fetchWishList(
                    customerId,latitude,longitude
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(RestaurantDetailViewState.OnSuccessFetchList(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(RestaurantDetailViewState.OnFailFetchWishList(
                            Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(RestaurantDetailViewState.OnFailFetchWishList(
                            Constants.DENIED)) }
                        406 -> { viewState.postValue(RestaurantDetailViewState.OnFailFetchWishList(
                            Constants.ANOTHER_LOGIN)) }
                        404 -> { viewState.postValue(RestaurantDetailViewState.OnFailFetchWishList(""))}
                    }
                }
            }catch (e : Exception) {
                Log.d("TAG", "fetchFoodRestMenuByRestId: ${e.localizedMessage} ANNDN ${e.message}")
                viewState.postValue(RestaurantDetailViewState.OnFailFetchWishList(
                    Constants.CONNECTION_ISSUE))
            }
        }
    }

}