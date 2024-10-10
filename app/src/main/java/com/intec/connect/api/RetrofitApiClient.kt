package com.intec.connect.api

import com.intec.connect.data.model.AuthResponse
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.LikeRequest
import com.intec.connect.data.model.LoginModel
import com.intec.connect.data.model.Product
import com.intec.connect.data.model.UnlikeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitApiClient {
    @POST("auth/login")
    suspend fun login(@Body loginModel: LoginModel): Response<AuthResponse>

    @GET("/categories/")
    suspend fun categoriesProduct(
        @Query("userID") userId: String,
        @Header("Authorization") authHeader: String
    ): Response<CategoriesProducts>

    @GET("products/")
    suspend fun products(
        @Query("userID") userId: String,
        @Header("Authorization") authHeader: String
    ): Response<List<Product>>

    @GET("products/{id}/")
    suspend fun productsDetail(
        @Path("id") productId: Int,
        @Header("Authorization") authHeader: String
    ): Response<Product>

    @POST("likes/like")
    suspend fun likeProduct(@Header("Authorization") token: String, @Body likeRequest: LikeRequest)

    @GET("likes/{userID}/liked-products")
    suspend fun getLikedProducts(
        @Path("userID") userId: String,
        @Header("Authorization") token: String
    ): List<Product>

    @POST("likes/unlike")
    suspend fun unlikeProduct(
        @Header("Authorization") token: String,
        @Body unlikeRequest: UnlikeRequest
    )
}