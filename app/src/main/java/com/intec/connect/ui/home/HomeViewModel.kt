package com.intec.connect.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.LikeRequest
import com.intec.connect.data.model.Product
import com.intec.connect.data.model.ShoppingCartByUser
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
    private val _shoppingCartByUser = MutableLiveData<Result<ShoppingCartByUser>>()

    val isLoading = MutableLiveData<Boolean>()

    /**
     * Fetches categories and their associated products from the repository.
     *
     * @param tokenModel The authentication token for the API request.
     * @return LiveData holding the [Result] of the operation.
     */
    fun getCategoriesProducts(
        userID: String,
        tokenModel: String
    ): MutableLiveData<Result<CategoriesProducts>> {
        viewModelScope.launch {
            isLoading.postValue(true)

            try {
                val categoriesProduct =
                    repository.getCategoriesProducts(userID, tokenModel)

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
    fun getProducts(userID: String, tokenModel: String): MutableLiveData<Result<List<Product>>> {
        viewModelScope.launch {
            isLoading.postValue(true)

            try {
                val products = repository.getProducts(userID, tokenModel)
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

                refreshProducts(userId, token)
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

                refreshProducts(userId, token)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshProducts(userId: String, token: String) {
        viewModelScope.launch {
            try {
                val categoriesProducts = repository.getCategoriesProducts(userId, token)
                val products = repository.getProducts(userId, token)
                _categoriesProducts.value = Result.success(categoriesProducts)
                _products.value = Result.success(products)
            } catch (e: Exception) {
                _categoriesProducts.value = Result.failure(e)
                _products.value = Result.failure(e)
            } finally {
            }
        }
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
        return _shoppingCartByUser
    }

}