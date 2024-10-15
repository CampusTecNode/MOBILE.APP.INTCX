package com.intec.connect.ui.spaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.SpacesItem
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _spaces = MutableLiveData<Result<List<SpacesItem>>>()
    val spacesItem: LiveData<Result<List<SpacesItem>>> = _spaces

    val isLoading = MutableLiveData<Boolean>()

    fun getSpaces(token: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = repository.getSpaces(token)
                _spaces.value = Result.success(result)
            } catch (e: Exception) {
                _spaces.value = Result.failure(e)
            } finally {
                isLoading.value = false
            }
        }
    }


    fun getSpacesList(token: String): LiveData<Result<List<SpacesItem>>> {
        val liveData = MutableLiveData<Result<List<SpacesItem>>>()
        isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getSpaces(token)
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