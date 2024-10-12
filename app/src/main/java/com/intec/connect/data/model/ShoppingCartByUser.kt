package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class ShoppingCartByUser(
    @SerializedName("CartDetails") val cartDetails: List<CartDetail>,
    @SerializedName("CartStatus") val cartStatus: CartStatus,
    @SerializedName("ID") val id: Int,
    @SerializedName("Status") val status: Boolean,
    @SerializedName("UserID") val userId: Int
)