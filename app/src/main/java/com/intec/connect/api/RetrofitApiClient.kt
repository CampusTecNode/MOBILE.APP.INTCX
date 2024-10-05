package com.intec.connect.api

import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.LoginModel
import com.intec.connect.data.model.TokenModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitApiClient {
    @POST("auth/login")
    suspend fun login(@Body loginModel: LoginModel): Response<TokenModel>

    @GET("categories/")
    suspend fun categoriesProduct(@Header("Authorization") authHeader: String): Response<CategoriesProducts>
}