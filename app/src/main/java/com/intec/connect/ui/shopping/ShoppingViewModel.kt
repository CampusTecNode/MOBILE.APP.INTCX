package com.intec.connect.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.intec.connect.data.model.DeleteShoppingCartBody
import com.intec.connect.data.model.ShoppingCartByUser
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _shoppingCartByUser = MutableLiveData<Result<ShoppingCartByUser>>()

    val isLoading = MutableLiveData<Boolean>()

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


    fun deleteShoppingCartItem(
        cartId: Int,
        productId: Int,
        id: String,
        token: String
    ): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        viewModelScope.launch {
            try {
                val request = DeleteShoppingCartBody(cartId, productId)
                repository.deleteShoppingCartItem(id, token, request)
                result.value = Result.success(true) // Success
            } catch (e: Exception) {
                result.value = Result.failure(e) // Failure
            }
        }
        return result
    }


}