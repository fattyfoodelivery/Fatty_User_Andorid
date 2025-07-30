package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.domain.model.*
import com.orikino.fatty.data.repository.ParcelRepository
import com.orikino.fatty.domain.viewstates.ParcelViewState
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ParcelViewModel @Inject constructor(
    private val repository: ParcelRepository
) : ViewModel() {

    var viewState: MutableLiveData<ParcelViewState> = MutableLiveData()
    var currencyVO = CurrencyVO()
    var parcelTypeListLiveData = MutableLiveData<MutableList<ParcelTypeVO>>()
    var addExtraCoverListLiveData = MutableLiveData<MutableList<AddExtraCoverCostVO>>()
    var ParcelInfoLiveData = MutableLiveData(PreferenceUtils.readParcelInfo())
    var deliveryFeeLivieData = MutableLiveData(EstimateCostVO())
    var parcel_id = 0
    var parcel_type = ""
    var extra_cost_id = 0
    var isActivate = false
    var paymentMethodID = 1
    var item_qty = ""
    var extraCover = ExtraCoverVO()
    var weight = 0.0
    var image = mutableListOf<MultipartBody.Part>()
    var imageList = mutableListOf<PicturesVO>()
    var note = ""
    var zoneId = 0
    var tempPart = mutableListOf<PicturesVO>()
    var part = mutableListOf<MultipartBody.Part>()
    var billTotalCost = 0.0
    var delivery_fee = 0.0
    var deliveryFeeCurrency = 0.0
    var extraCoverage = 0.0
    var extraCoverageCurrency = 0.0
    var totalEstimatedCurrency = 0.0
    var exchangeRate = 0.0
    var abnormal_fee = 0.0
    var extraCheck = MutableLiveData<Boolean>()

    fun parcelDetailById(parcel_zone_id : Int) {

        viewModelScope.launch {
            try {
               val response =  PreferenceUtils.readUserVO()?.customer_id.let {
                  repository.parcelOrderById(it!!, parcel_zone_id)
                }
                viewState.postValue(ParcelViewState.OnLoadingParcelOrderDetail)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(ParcelViewState.OnSuccessParcelOrderDetail(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(
                               ParcelViewState.OnFailParcelParcelOrderDetail(Constants.SERVER_ISSUE)
                            )
                        }
                        403 -> { viewState.postValue(ParcelViewState.OnFailParcelParcelOrderDetail(Constants.DENIED)) }
                        406 -> {
                            viewState.postValue(
                                ParcelViewState.OnFailParcelParcelOrderDetail(Constants.ANOTHER_LOGIN)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(ParcelViewState.OnFailParcelParcelOrderDetail(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchParcelTrack() {
        viewModelScope.launch {
            try {
                val response = repository.fetchCurrency()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(ParcelViewState.OnSuccessCurrency(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> { viewState.postValue(ParcelViewState.OnFailCurrency(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(ParcelViewState.OnFailCurrency(Constants.DENIED)) }
                        406 -> { viewState.postValue(ParcelViewState.OnFailCurrency(Constants.ANOTHER_LOGIN)) }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(ParcelViewState.OnFailCurrency(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchCurrency() {
        viewModelScope.launch {
            try {
                val response = repository.fetchCurrency()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(ParcelViewState.OnSuccessCurrency(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(ParcelViewState.OnFailCurrency(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(ParcelViewState.OnFailCurrency(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(ParcelViewState.OnFailCurrency(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(ParcelViewState.OnFailCurrency(Constants.CONNECTION_ISSUE))
            }
        }
    }
}
