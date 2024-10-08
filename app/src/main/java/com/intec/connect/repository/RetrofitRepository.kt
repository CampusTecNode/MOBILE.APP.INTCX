package com.intec.connect.repository

import android.util.Log
import com.intec.connect.api.RetrofitApiClient
import com.intec.connect.data.model.CategoriesProducts
import com.intec.connect.data.model.LoginModel
import com.intec.connect.data.model.Product
import com.intec.connect.data.model.TokenModel
import javax.inject.Inject

class RetrofitRepository @Inject constructor(private val userAPI: RetrofitApiClient) {

    companion object {
        private const val TAG = "RetrofitRepository"
    }

    /**
     * Logs in the user and retrieves a token.
     *
     * @param loginModel The login model containing user credentials.
     * @return TokenModel containing the access token.
     * @throws Exception If the login request fails.
     */
    suspend fun loginUser(loginModel: LoginModel): TokenModel {
        val response = userAPI.login(loginModel)

        Log.d(TAG, "loginUser: $response")

        return handleResponse(response)
    }

    /**
     * Retrieves categories and their products.
     *
     * @param tokenModel The token used for authentication.
     * @return CategoriesProducts containing the retrieved categories and products.
     * @throws Exception If the request fails.
     */
    suspend fun getCategoriesProducts(tokenModel: String): CategoriesProducts {
        val response = userAPI.categoriesProduct(tokenModel)

        Log.d(TAG, "getCategoriesProducts: $response")

        return handleResponse(response)
    }

    /**
     * Retrieves a list of products.
     *
     * @param tokenModel The token used for authentication.
     * @return List of Product containing the retrieved products.
     * @throws Exception If the request fails.
     */
    suspend fun getProducts(tokenModel: String): List<Product> {
        val response = userAPI.products(tokenModel)

        Log.d(TAG, "getProducts: $response")

        return handleResponse(response)
    }

    /**
     * Handles the response from the API call.
     *
     * @param response The response from the API.
     * @throws Exception If the response is unsuccessful.
     */
    private fun <T> handleResponse(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
        } else {
            throw Exception("Request failed: ${response.code()} - ${response.message()}")
        }
    }
}