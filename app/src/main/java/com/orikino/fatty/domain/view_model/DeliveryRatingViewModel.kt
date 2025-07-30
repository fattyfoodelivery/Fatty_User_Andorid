package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.DeliveryRatingRepository
import com.orikino.fatty.domain.viewstates.DeliveryRatingViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryRatingViewModel @Inject constructor(
    private val repository: DeliveryRatingRepository
) : ViewModel() {

    var viewState: MutableLiveData<DeliveryRatingViewState> = MutableLiveData()

    /*private val _ratingViewState by lazy {
        MutableLiveData<ViewState<RatingValueResponse>>()
    }

    private val _ratingUpload by lazy {
        MutableLiveData<ViewState<RatingResponse>>()
    }
    val ratingViewState: LiveData<ViewState<RatingValueResponse>>
        get() = _ratingViewState

    val ratingUpload: LiveData<ViewState<RatingResponse>>
        get() = _ratingUpload*/

    var orderId = 0
    var rating = 1
    var description = ""

    fun deliveryRating() {
        /*viewModelScope.launch(Dispatchers.IO) {
            repository.deliveryRating(orderId, rating, description)
                .onStart { viewState.postValue(DeliveryRatingViewState.OnLoadingDeliveryRating) }
                .catch { viewState.postValue(DeliveryRatingViewState.OnFailDeliveryRating(it.localizedMessage)) }
                .collect { viewState.postValue(DeliveryRatingViewState.OnSuccessDeliveryRating(it)) }
        }*/
        viewModelScope.launch {
            val response = repository.deliveryRating(orderId, rating, description)
            viewState.postValue(DeliveryRatingViewState.OnLoadingDeliveryRating)
            try {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(DeliveryRatingViewState.OnSuccessDeliveryRating(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailDeliveryRating("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailDeliveryRating("Denied"))
                        }

                        406 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailDeliveryRating("Another Login"))
                        }
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(DeliveryRatingViewState.OnFailDeliveryRating("Connection Issue"))
            }
        }
    }

    fun fetchRating() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchRating()
            try {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(DeliveryRatingViewState.OnSuccessRatingValue(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailRatingValue("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailRatingValue("Denied"))
                        }

                        406 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailRatingValue("Another Login"))
                        }
                    }
                }
            }catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(DeliveryRatingViewState.OnFailRatingValue("Connection Issue"))
            }
                /*.onStart { _ratingViewState.postValue(ViewState.Loading()) }
                .catch { _ratingViewState.postValue(ViewState.Error(it.localizedMessage)) }
                .collect {
                    _ratingViewState.postValue(ViewState.Success(it))
                }*/
        }
    }

    fun uploadRating(
        rating_type: String,
        order_id: Int,
        rating: String,
        options: String,
        description: String,
        locale: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.rating(rating_type, order_id, rating, options, description, locale)
            viewState.postValue(DeliveryRatingViewState.OnLoadingRating)
            try {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(DeliveryRatingViewState.OnSuccessRating(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailRating("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailRating("Denied"))
                        }

                        406 -> {
                            viewState.postValue(DeliveryRatingViewState.OnFailRating("Another Login"))
                        }
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }

                /*.onStart { _ratingUpload.postValue(ViewState.Loading()) }
                .catch { _ratingUpload.postValue(ViewState.Error(it.localizedMessage)) }
                .collect {
                    _ratingUpload.postValue(ViewState.Success(it))
                }*/
        }
    }
}