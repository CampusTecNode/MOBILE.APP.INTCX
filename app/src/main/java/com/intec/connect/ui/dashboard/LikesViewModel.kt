package com.intec.connect.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.Product
import com.intec.connect.data.model.UnlikeRequest
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikesViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    val isLoading = MutableLiveData<Boolean>()

    fun unlikeProduct(userId: String, productId: String, token: String) {
        viewModelScope.launch {
            try {
                val request = UnlikeRequest(userId, productId)
                repository.unlikeProduct(request, token)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getLikedProducts(userId: String, token: String): LiveData<Result<List<Product>>> {
        val liveData = MutableLiveData<Result<List<Product>>>()
        isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getLikedProducts(userId, token)
                liveData.value = Result.success(result)
            } catch (e: Exception) {
                liveData.value = Result.failure(e)
            } finally {
                isLoading.value = false
            }
        }
        return liveData
    }
}