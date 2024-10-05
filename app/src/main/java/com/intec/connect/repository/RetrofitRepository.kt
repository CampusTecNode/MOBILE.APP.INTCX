package com.intec.connect.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.intec.connect.api.RetrofitApiClient
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.LoginModel
import com.intec.connect.data.model.TokenModel
import javax.inject.Inject

class RetrofitRepository @Inject constructor(private val userAPI: RetrofitApiClient) {

    suspend fun loginUser(loginModel: LoginModel): TokenModel {
        val response = userAPI.login(loginModel)

        Log.d(TAG, "loginUser: $response")

        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Login failed: ${response.code()} - ${response.message()}")
        }

    }

    suspend fun getCategoriesProducts(tokenModel: String): CategoriesProducts {
        val response = userAPI.categoriesProduct(tokenModel)

        Log.d(TAG, "$response")

        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Login failed: ${response.code()} - ${response.message()}")
        }

    }
}