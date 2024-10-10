package com.intec.connect.ui.detailsProducts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.Product
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsDetailViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _productsDetail = MutableLiveData<Result<Product>>()
    val isLoading = MutableLiveData<Boolean>()

    fun getProductsDetail(productId: Int, tokenModel: String): MutableLiveData<Result<Product>> {
        viewModelScope.launch {
            isLoading.postValue(true)

            try {
                val product = repository.getProductsDetail(productId, tokenModel)
                _productsDetail.value = Result.success(product)
            } catch (e: Exception) {
                _productsDetail.value = Result.failure(e)
            } finally {
                isLoading.postValue(false)
            }
        }
        return _productsDetail
    }
}