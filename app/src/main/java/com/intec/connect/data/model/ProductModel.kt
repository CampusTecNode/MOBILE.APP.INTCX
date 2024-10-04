package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class ProductModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("categoryID") val categoryID: Int,
    @SerializedName("imageURL") val imageURL: String
)