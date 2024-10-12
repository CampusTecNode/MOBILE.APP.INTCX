package com.intec.connect.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.Product
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _productDetails = MutableLiveData<Result<Product>>()
    val productDetails: LiveData<Result<Product>> = _productDetails

    private val _categoriesProducts = MutableLiveData<Result<CategoriesProducts>>()
    private val _products = MutableLiveData<Result<List<Product>>>()

    val categoriesProducts: LiveData<Result<CategoriesProducts>> = _categoriesProducts
    val products: LiveData<Result<List<Product>>> = _products


    fun refreshHomeData(userId: String, token: String) {
        viewModelScope.launch {
            try {
                val categoriesProducts = repository.getCategoriesProducts(userId, token)
                _categoriesProducts.value = Result.success(categoriesProducts)
                Log.d("SharedViewModel", "refreshHomeData: categoriesProducts updated")
            } catch (e: Exception) {
                _categoriesProducts.value = Result.failure(e)
                Log.e("SharedViewModel", "Error refreshing categoriesProducts", e)
            }

            try {
                val products = repository.getProducts(userId, token)
                _products.value = Result.success(products)
                Log.d("SharedViewModel", "refreshHomeData: products updated")
            } catch (e: Exception) {
                _products.value = Result.failure(e)
                Log.e("SharedViewModel", "Error refreshing products", e)
            }
        }
    }
}