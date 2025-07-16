package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.data.repository.SearchRepository
import com.joy.fattyfood.domain.model.*
import com.joy.fattyfood.domain.viewstates.FilterViewState
import com.joy.fattyfood.domain.viewstates.SearchViewState
import com.joy.fattyfood.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    var viewState: MutableLiveData<SearchViewState> = MutableLiveData()
    var categoryLiveDataList : MutableLiveData<MutableList<SearchFilterCategoryVO>> = MutableLiveData()
    var searchFoodListLiveData : MutableLiveData<MutableList<SearchFoodsVO>> = MutableLiveData()
    var searchRestaurantListLiveData : MutableLiveData<MutableList<RecommendRestaurantVO>> = MutableLiveData()
    var filterRestaurantListLiveData : MutableLiveData<MutableList<RecommendRestaurantVO>> = MutableLiveData()
    var subList = mutableListOf<FilterDishVO>()
    var isRecent = true
    var isFood = false
    var isFilter = false

    fun customerSearch(
        keyword: String,
        customerId: Int = PreferenceUtils.readUserVO().customer_id ?: 0,
        lat: Double = PreferenceUtils.readUserVO().latitude ?: 0.0,
        lng: Double = PreferenceUtils.readUserVO().longitude ?: 0.0
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(SearchViewState.OnLoadingSearchFoodAndRestaurant)
            try {
                val response = searchRepository.customerSearch(keyword,customerId,lat,lng)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(SearchViewState.OnSuccessSearchFoodAndRestaurant(
                            it
                        ))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(SearchViewState.OnFailSearchFoodAndRestaurant("Server Issue")) }
                        403 -> { viewState.postValue(SearchViewState.OnFailSearchFoodAndRestaurant("Denied")) }
                        406 -> { viewState.postValue(SearchViewState.OnFailSearchFoodAndRestaurant("Another Login")) }
                    }
                }

            } catch (e : Exception) {
                viewState.postValue(SearchViewState.OnFailSearchFoodAndRestaurant("Fail"))
            }
        }

    }

    fun fetchCategoryFilterSearch(
        language : String = PreferenceUtils.readLanguage() ?: "en"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var response = searchRepository.fetchCategoryFilterSearch(language)
            try {
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(SearchViewState.OnSuccessCategoryFilter(it)) }

                } else {
                    when(response.code()) {
                        500 -> {}
                        406 -> {}
                        403 -> {}
                    }
                }
            } catch (e : Exception) {
                viewState.postValue(SearchViewState.OnFailCategoryFilter("Connection Issue"))
            }
        }
    }

    fun filterRestaurantRestaurant(categoryList : String,customerId: Int,lat: Double,lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = searchRepository.filterRestaurantRestaurants(categoryList,customerId,lat,lng)
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(SearchViewState.OnSuccessFilterRestaurant(it)) }
                } else {
                    when(response.code()) {
                        500 -> {}
                        406 -> {}
                        403 -> {}
                    }
                }
            } catch (e : Exception) {
                viewState.postValue(SearchViewState.OnFailFilterRestaurant("Connection Issue"))
            }
        }
    }

    fun operateWishList(restaurant_id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var response = searchRepository.operateWishList(PreferenceUtils.readUserVO().customer_id ?: 0,restaurant_id)
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(SearchViewState.OnSuccessOperateWishList(it)) }

                }else {
                    when(response.code()) {
                        500 -> {}
                        406 -> {}
                        403 -> {}
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(SearchViewState.OnFailOperateWishList("Connection Issue"))
            }
        }
    }


    var viewStateFilter : MutableLiveData<FilterViewState> = MutableLiveData()
    fun filterRestaurantWithCateID(categoryList : String,customerId: Int,lat: Double,lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = searchRepository.filterRestaurantRestaurants(categoryList,customerId,lat,lng)
                if (response.isSuccessful) {
                    response.body()?.let { viewStateFilter.postValue(FilterViewState.OnSuccessFilter(it)) }
                } else {
                    when(response.code()) {
                        500 -> {}
                        406 -> {}
                        403 -> {}
                    }
                }
            } catch (e : Exception) {
                viewStateFilter.postValue(FilterViewState.OnFailFilter(e.localizedMessage))
            }
        }
    }

}
