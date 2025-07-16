package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.domain.viewstates.WishListViewState
import com.joy.fattyfood.data.repository.WishListRepository
import com.joy.fattyfood.domain.model.WishListVO
import com.joy.fattyfood.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val wishListRepository: WishListRepository
) : ViewModel (){

    var viewState: MutableLiveData<WishListViewState> = MutableLiveData()
    var wishListLiveDataList : MutableLiveData<MutableList<WishListVO>> = MutableLiveData()

    fun fetchWishList(customer_id : Int) {
        viewModelScope.launch {
            try {
                val response = wishListRepository.fetchWishList(
                    customer_id,
                    latitude = 22.960968004630196,
                    longitude = 97.75544039905071
                )
                viewState.postValue(WishListViewState.OnLoadingWishList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(WishListViewState.OnSuccessWishList(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(WishListViewState.OnFailWishList(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(WishListViewState.OnFailWishList(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(WishListViewState.OnFailWishList(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(WishListViewState.OnFailWishList(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun operateWishList(customer_id: Int,restaurant_id: Int) {
        viewModelScope.launch {
            try {
                val response = wishListRepository.operateWishList(customer_id, restaurant_id)
                viewState.postValue(WishListViewState.OnLoadingOperateWishList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(WishListViewState.OnSuccessOperateWishList(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(WishListViewState.OnFailOperateWishList(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(WishListViewState.OnFailOperateWishList(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(WishListViewState.OnFailOperateWishList(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(WishListViewState.OnFailOperateWishList(Constants.CONNECTION_ISSUE))
            }
        }
    }

}