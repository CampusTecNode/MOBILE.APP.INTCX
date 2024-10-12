package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class ShoppingCartBody(
    @SerializedName("ProductID") val productID: Int,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("UserID") val userID: String
)