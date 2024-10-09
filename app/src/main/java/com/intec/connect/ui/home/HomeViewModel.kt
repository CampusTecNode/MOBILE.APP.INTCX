package com.intec.connect.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.LikeRequest
import com.intec.connect.data.model.Product
import com.intec.connect.data.model.UnlikeRequest
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _categoriesProducts = MutableLiveData<Result<CategoriesProducts>>()
    private val _products = MutableLiveData<Result<List<Product>>>()
    val isLoading = MutableLiveData<Boolean>()

    /**
     * Fetches categories and their associated products from the repository.
     *
     * @param tokenModel The authentication token for the API request.
     * @return LiveData holding the [Result] of the operation.
     */
    fun getCategoriesProducts(tokenModel: String): MutableLiveData<Result<CategoriesProducts>> {
        viewModelScope.launch {
            isLoading.postValue(true)

            try {
                val categoriesProduct =
                    repository.getCategoriesProducts(tokenModel)

                if (!categoriesProduct.isEmpty()) {
                    _categoriesProducts.value = Result.success(categoriesProduct)
                    isLoading.postValue(false)
                }

            } catch (e: Exception) {
                _categoriesProducts.value = Result.failure(e)
                isLoading.postValue(false)
            }

        }

        return _categoriesProducts
    }

    /**
     * Fetches all products from the repository.
     *
     * @param tokenModel The authentication token for the API request.
     * @return LiveData holding the [Result] of the operation.
     */
    fun getProducts(tokenModel: String): MutableLiveData<Result<List<Product>>> {
        viewModelScope.launch {
            isLoading.postValue(true)

            try {
                val products = repository.getProducts(tokenModel)
                _products.value = Result.success(products)
            } catch (e: Exception) {
                _products.value = Result.failure(e)
            } finally {
                isLoading.postValue(false)
            }
        }

        return _products
    }

    fun likeProduct(userId: String, productId: String, token: String) {
        viewModelScope.launch {
            try {
                val request = LikeRequest(userId, productId)
                repository.likeProduct(request, token)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun unlikeProduct(userId: String, productId: String, token: String) {
        viewModelScope.launch {
            try {
                val request = UnlikeRequest(userId, productId)
                repository.unlikeProduct(request, token)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

}