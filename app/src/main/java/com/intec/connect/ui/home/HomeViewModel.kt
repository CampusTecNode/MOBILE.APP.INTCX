package com.intec.connect.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.ProductModel
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val productList = MutableLiveData<List<ProductModel>>()


    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}