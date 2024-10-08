package com.intec.connect.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intec.connect.data.model.LoginModel
import com.intec.connect.data.model.TokenModel
import com.intec.connect.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: RetrofitRepository) :
    ViewModel() {

    private val _loginResult = MutableLiveData<Result<TokenModel>>()
    val loginResult: LiveData<Result<TokenModel>> = _loginResult

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
}