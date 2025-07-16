package com.joy.fattyfood

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.data.repository.CategoryRepository
import com.joy.fattyfood.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel(){

    var viewState : MutableLiveData<CategoryViewState> = MutableLiveData()

    fun fetchCategoryList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = categoryRepository.fetchCategoryList()
                viewState.postValue(CategoryViewState.OnLoadingCategoryList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(CategoryViewState.OnSuccessCategoryList(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(CategoryViewState.OnFailCategoryList(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(CategoryViewState.OnFailCategoryList(Constants.DENIED)) }
                        406 -> { viewState.postValue(CategoryViewState.OnFailCategoryList(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(CategoryViewState.OnFailCategoryList(Constants.CONNECTION_ISSUE))
            }
        }
    }
}