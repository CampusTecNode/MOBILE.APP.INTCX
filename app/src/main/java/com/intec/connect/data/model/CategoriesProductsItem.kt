package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class CategoriesProductsItem(
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("CreatedBy") val createdBy: String,
    @SerializedName("Description") val description: String,
    @SerializedName("ID") val id: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Products") val products: List<Product>,
    @SerializedName("UpdatedAt") val updatedAt: String,
    @SerializedName("UpdatedBy") val updatedBy: String,
    @SerializedName("ImageURL") val imageURL: String
)