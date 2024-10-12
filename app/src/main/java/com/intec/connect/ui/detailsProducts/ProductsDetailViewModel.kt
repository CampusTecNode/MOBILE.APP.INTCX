package com.intec.connect.ui.detailsProducts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.intec.connect.data.model.LikeRequest
import com.intec.connect.data.model.Product
import com.intec.connect.data.model.ShoppingCartBody
import com.intec.connect.data.model.ShoppingCartByUser
import com.intec.connect.data.model.UnlikeRequest
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsDetailViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _productsDetail = MutableLiveData<Result<Product>>()
    private val _shoppingCartByUser = MutableLiveData<Result<ShoppingCartByUser>>()

    private val shoppingCartByUser: LiveData<Result<ShoppingCartByUser>> = _shoppingCartByUser
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
            } catch (e: Exception) {
                _productsDetail.value = Result.failure(e)
            } catch (e: ApiException) {
                _productsDetail.value = Result.failure(e)
            } finally {
                isLoading.postValue(false)
            }
        }
        return productsDetail
    }

    fun likeProduct(userId: String, productId: Int, token: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        viewModelScope.launch {
            try {
                val request = LikeRequest(userId, productId.toString())
                repository.likeProduct(request, token)
                result.value = Result.success(true) // Success
            } catch (e: Exception) {
                result.value = Result.failure(e) // Failure
            }
        }
        return result
    }

    fun unlikeProduct(userId: String, productId: Int, token: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        viewModelScope.launch {
            try {
                val request = UnlikeRequest(userId, productId.toString())
                repository.unlikeProduct(request, token)
                result.value = Result.success(true) // Success
            } catch (e: Exception) {
                result.value = Result.failure(e) // Failure
            }
        }
        return result
    }

    fun shoppingCart(
        userId: String,
        productId: Int,
        quantity: Int,
        token: String
    ): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        viewModelScope.launch {
            try {
                val request = ShoppingCartBody(productId, quantity, userId)
                repository.shoppingCart(request, token)
                result.value = Result.success(true) // Success
            } catch (e: Exception) {
                result.value = Result.failure(e) // Failure
            }
        }
        return result
    }

    fun shoppingCartByUser(userId: String, token: String): LiveData<Result<ShoppingCartByUser>> {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val shoppingCartBody = repository.shoppingCartByUser(userId, token)
                _shoppingCartByUser.value = Result.success(shoppingCartBody)
            } catch (e: Exception) {
                _shoppingCartByUser.value = Result.failure(e)
            } catch (e: ApiException) {
                _shoppingCartByUser.value = Result.failure(e)
            } finally {
                isLoading.postValue(false)
            }
        }
        return shoppingCartByUser
    }

}