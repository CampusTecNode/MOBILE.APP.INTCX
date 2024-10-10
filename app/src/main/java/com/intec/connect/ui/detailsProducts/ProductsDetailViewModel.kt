package com.intec.connect.ui.detailsProducts

import android.net.http.NetworkException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.intec.connect.data.model.Product
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsDetailViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _productsDetail = MutableLiveData<Result<Product>>()
    private val productsDetail: LiveData<Result<Product>> = _productsDetail
    val isLoading = MutableLiveData<Boolean>()

    fun getProductsDetail(
        productId: Int,
        userId: String,
        tokenModel: String
    ): LiveData<Result<Product>> {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val product = repository.getProductsDetail(productId, userId, tokenModel)
                _productsDetail.value = Result.success(product)
            } catch (e: NetworkException) {
                _productsDetail.value = Result.failure(e)
            } catch (e: ApiException) {
                _productsDetail.value = Result.failure(e)
            } catch (e: Exception) {
                _productsDetail.value = Result.failure(e)
            } finally {
                isLoading.postValue(false)
            }
        }
        return productsDetail
    }
}