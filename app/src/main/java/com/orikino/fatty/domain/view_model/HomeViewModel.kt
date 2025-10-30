package com.orikino.fatty.domain.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.HomeViewState
import com.orikino.fatty.data.repository.CategoryRepository
import com.orikino.fatty.data.repository.HomeRepository
import com.orikino.fatty.domain.model.CurrencyVO
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.viewstates.WishListViewState
import com.orikino.fatty.ui.views.activities.category.TopRelatedPagingState
import com.orikino.fatty.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val categoryRepository: CategoryRepository,
    private val listPagingState: TopRelatedPagingState,
) : ViewModel() {

    var viewState: MutableLiveData<HomeViewState> = MutableLiveData()
    var nearRestaurantLiveDataList: MutableLiveData<MutableList<NearByRestaurantVO>> =
        MutableLiveData()
    var topRelatedRestaurantLiveDataList: MutableLiveData<MutableList<RecommendRestaurantVO>> =
        MutableLiveData()
    var maxPage : MutableLiveData<Int> = MutableLiveData()
    var zoneId: Int = 0
    var currencyVO = CurrencyVO()
    var stateName: String = ""

    //var address: String? = null
    var isLoading = false
    var isRefresh = false

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    fun setAddress(manageAddress: String) {
        _address.value = manageAddress
    }

    fun fetchHome(
        customer_id: Int,
        stateName: String = "",
        latitude: Double,
        longitude: Double
    ) {
        viewState.postValue(HomeViewState.OnLoadingHome)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = homeRepository.fetchHome(
                    customer_id = customer_id,
                    stateName = stateName,
                    latitude = latitude,
                    longitude = longitude
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(HomeViewState.OnSuccessHome(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(HomeViewState.OnFailHome("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(HomeViewState.OnFailHome("Denied"))
                        }

                        406 -> {
                            viewState.postValue(HomeViewState.OnFailHome("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState.postValue(HomeViewState.OnFailHome(e.localizedMessage))
            }
        }
    }

    fun updateUserInfo(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            val response = homeRepository.updateUserInfo(customer_id, latitude, longitude)
            viewState.postValue(HomeViewState.OnLoadingUpdateUserInfo)
            try {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(HomeViewState.OnSuccessUpdateUserInfo(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(HomeViewState.OnFailUpdateUserInfo("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(HomeViewState.OnFailUpdateUserInfo("Denied"))
                        }

                        406 -> {
                            viewState.postValue(HomeViewState.OnFailUpdateUserInfo("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(HomeViewState.OnFailUpdateUserInfo("Connection Issue"))
            }
        }
    }

    fun fetchRestaurantByCategory(
        category_id: Int,
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch() {
            viewState.postValue(HomeViewState.OnLoadingRestaurantByCategory)
            val response = categoryRepository.fetchCategoryByCategoryId(
                category_id,
                customer_id,
                latitude,
                longitude
            )
            try {
                if (response.isSuccessful) {

                    response.body()
                        ?.let { viewState.postValue(HomeViewState.OnSuccessRestaurantByCategory(it)) }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(HomeViewState.OnFailRestaurantByCategory(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(HomeViewState.OnFailRestaurantByCategory(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(HomeViewState.OnFailRestaurantByCategory(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(HomeViewState.OnFailRestaurantByCategory(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchTopRelatedCategory(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch() {
            viewState.postValue(HomeViewState.OnLoadingTopRelated)
            listPagingState.resetListPage()
            val response =
                homeRepository.fetchTopRelatedCategory(customer_id, latitude, longitude, 1, 10)
            try {
                if (response.isSuccessful) {
                    listPagingState.listMaxPage = response.body()?.meta?.totalPages ?: 0
                    maxPage.postValue(response.body()?.meta?.total)
                    listPagingState.increaseListCurrentPage()
                    response.body()
                        ?.let { viewState.postValue(HomeViewState.OnSuccessTopRelated(it)) }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(HomeViewState.OnFailTopRelated(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(HomeViewState.OnFailTopRelated(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(HomeViewState.OnFailTopRelated(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(HomeViewState.OnFailTopRelated(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun getTopRelatedCurrentPage() = listPagingState.listCurrentPage

    fun onListEndReachTopRelated(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            viewState.postValue(HomeViewState.OnLoadingTopRelated)
            if (listPagingState.shouldNextPageList()) {
                val response =
                    homeRepository.fetchTopRelatedCategory(customer_id, latitude, longitude, listPagingState.listCurrentPage, 10)
                try {
                    if (response.isSuccessful) {
                        listPagingState.increaseListCurrentPage()
                        response.body()
                            ?.let { viewState.postValue(HomeViewState.OnSuccessTopRelated(it)) }
                    } else {
                        when (response.code()) {
                            500 -> {
                                viewState.postValue(HomeViewState.OnFailTopRelated(Constants.SERVER_ISSUE))
                            }

                            403 -> {
                                viewState.postValue(HomeViewState.OnFailTopRelated(Constants.DENIED))
                            }

                            406 -> {
                                viewState.postValue(HomeViewState.OnFailTopRelated(Constants.ANOTHER_LOGIN))
                            }
                        }
                    }
                } catch (e: Exception) {
                    viewState.postValue(HomeViewState.OnFailTopRelated(Constants.CONNECTION_ISSUE))
                }
            } else {
                viewState.postValue(HomeViewState.OnListEndReachTopRelated)
            }
        }
    }

    fun fetchCurrency() {
        viewModelScope.launch {
            try {
                val response = homeRepository.fetchCurrency()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(HomeViewState.OnSuccessCurrency(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(HomeViewState.OnFailCurrency("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(HomeViewState.OnFailCurrency("Denied"))
                        }

                        406 -> {
                            viewState.postValue(HomeViewState.OnFailCurrency("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(HomeViewState.OnFailCurrency("Connection Issue"))
            }
        }
    }


    var viewStateWish: MutableLiveData<WishListViewState> = MutableLiveData()
    fun operateWishList(customer_id: Int, restaurant_id: Int) {
        viewModelScope.launch {
            try {
                val response =
                    homeRepository.operateWishList(customer_id = customer_id, restaurant_id)
                viewStateWish.postValue(WishListViewState.OnLoadingOperateWishList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewStateWish.postValue(WishListViewState.OnSuccessOperateWishList(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewStateWish.postValue(WishListViewState.OnFailOperateWishList("Server Issue"))
                        }

                        403 -> {
                            viewStateWish.postValue(WishListViewState.OnFailOperateWishList("Denied"))
                        }

                        406 -> {
                            viewStateWish.postValue(WishListViewState.OnFailOperateWishList("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                viewStateWish.postValue(WishListViewState.OnFailOperateWishList("Connection Issue"))
            }
        }
    }

    fun fetchWishList(customer_id: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = homeRepository.fetchWishList(customer_id, latitude, longitude)
                viewStateWish.postValue(WishListViewState.OnLoadingWishList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewStateWish.postValue(WishListViewState.OnSuccessWishList(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewStateWish.postValue(WishListViewState.OnFailWishList("Server Issue"))
                        }

                        403 -> {
                            viewStateWish.postValue(WishListViewState.OnFailWishList("Denied"))
                        }

                        406 -> {
                            viewStateWish.postValue(WishListViewState.OnFailWishList("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                viewStateWish.postValue(WishListViewState.OnFailWishList("Connection Issue"))
            }
        }
    }

}
