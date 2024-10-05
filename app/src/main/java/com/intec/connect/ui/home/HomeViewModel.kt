package com.intec.connect.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val categoriesProductsRepository: RetrofitRepository) :
    ViewModel() {

    private val _categoriesProducts = MutableLiveData<Result<CategoriesProducts>>()
    val isLoading = MutableLiveData<Boolean>()

    fun getCategoriesProducts(tokenModel: String): MutableLiveData<Result<CategoriesProducts>> {
        viewModelScope.launch {
            isLoading.postValue(true)

            try {
                val categoriesProduct =
                    categoriesProductsRepository.getCategoriesProducts(tokenModel)

                if (!categoriesProduct.isEmpty()) {
                    _categoriesProducts.value = Result.success(categoriesProduct)
                    isLoading.postValue(false)
                }

            } catch (e: Exception) {
                _categoriesProducts.value = Result.failure(e)
            }

        }

        return _categoriesProducts
    }

}