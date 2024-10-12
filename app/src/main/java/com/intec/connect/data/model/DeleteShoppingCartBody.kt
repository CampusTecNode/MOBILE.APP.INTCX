package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class DeleteShoppingCartBody(
    @SerializedName("CartID") val cartId: Int,
    @SerializedName("ProductID") val productID: Int
)