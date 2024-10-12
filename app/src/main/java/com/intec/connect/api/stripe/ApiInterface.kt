package com.intec.connect.api.stripe

import com.intec.connect.api.stripe.Ultils.SECRET_KEY
import com.intec.connect.data.model.CustomerModel
import com.intec.connect.data.model.PaymentModel
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomer(): Response<CustomerModel>

    @Headers(
        "Authorization: Bearer §SECRET_KEY",
        "Stripe-Version: 2022-11-15"
    )
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String
    ): Response<CustomerModel>


    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customer: String,
        @Query("amount") amount: String = "100",
        @Query("currency") currency: String = "inr",
        @Query("automatic_payment_methods[enable]") automatePay: Boolean = true,
    ): Response<PaymentModel>
}