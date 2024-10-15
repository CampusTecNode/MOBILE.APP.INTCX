package com.intec.connect.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.AuthResponse
import com.intec.connect.data.model.LoginModel
import com.intec.connect.data.model.SendResetPassword
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: RetrofitRepository) :
    ViewModel() {

    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    private val _resetPasswordResult = MutableLiveData<Result<Unit>>()
    val resetPasswordResult: LiveData<Result<Unit>> = _resetPasswordResult


    fun loginUser(loginModel: LoginModel) {
        viewModelScope.launch {
            try {
                val tokenModel = authRepository.loginUser(loginModel)
                _loginResult.value = Result.success(tokenModel)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun sendResetPasswordRequest(sendResetPassword: SendResetPassword) {
        viewModelScope.launch {
            try {
                authRepository.sendResetPasswordRequest(sendResetPassword)
                _resetPasswordResult.value = Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ResetPassword", "An unexpected error occurred: ${e.message}", e)
                _resetPasswordResult.value = Result.failure(e)
            }
        }
    }
}