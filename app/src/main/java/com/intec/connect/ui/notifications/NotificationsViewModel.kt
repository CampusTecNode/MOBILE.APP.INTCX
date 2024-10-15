package com.intec.connect.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.intec.connect.data.model.Notification
import com.intec.connect.data.model.NotificationBody
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(private val repository: RetrofitRepository) :
    ViewModel() {

    private val _notifications = MutableLiveData<Result<List<Notification>>>()
    val notifications: LiveData<Result<List<Notification>>> = _notifications

    val isLoading = MutableLiveData<Boolean>()

    fun saveNotification(
        title: String,
        message: String,
        userId: String,
        type: String,
        token: String
    ) {
        viewModelScope.launch {
            try {
                val request = NotificationBody(
                    title = title,
                    message = message,
                    userId = userId,
                    type = type
                )
                repository.saveNotification(request, token)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getNotifications(userId: String, tokenModel: String): LiveData<Result<List<Notification>>> {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val notification = repository.getNotifications(userId, tokenModel)
                _notifications.value = Result.success(notification)
            } catch (e: Exception) {
                _notifications.value = Result.failure(e)
            } catch (e: ApiException) {
                _notifications.value = Result.failure(e)
            } finally {
                isLoading.postValue(false)
            }
        }
        return notifications
    }

}