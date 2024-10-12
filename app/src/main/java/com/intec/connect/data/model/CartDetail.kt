package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class CartDetail(
    @SerializedName("ID") val id: Int,
    @SerializedName("Product") val product: Product,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("UnitPrice") val unitPrice: String
)