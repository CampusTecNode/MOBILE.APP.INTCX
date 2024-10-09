package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("CategoryID") val categoryID: Int,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("CreatedBy") val createdBy: String,
    @SerializedName("Description") val description: String,
    @SerializedName("ID") val id: Int,
    @SerializedName("ImageURL") val imageURL: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Price") val price: String,
    @SerializedName("Stock") val stock: Int,
    @SerializedName("UpdatedAt") val updatedAt: String,
    @SerializedName("UpdatedBy") val updatedBy: String,
    @SerializedName("liked") var liked: Boolean = false
)