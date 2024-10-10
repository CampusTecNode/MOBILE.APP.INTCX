package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("Brand") val brand: String,
    @SerializedName("CategoryID") val categoryID: Int,
    @SerializedName("Color") val color: String,
    @SerializedName("Description") val description: String,
    @SerializedName("ID") val id: Int,
    @SerializedName("IsLiked") var liked: Boolean,
    @SerializedName("ImageURL") val imageURL: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Price") val price: String,
    @SerializedName("SKU") val sku: String,
    @SerializedName("Size") val size: String,
    @SerializedName("Stock") val stock: Int,
    @SerializedName("Weight") val weight: Double
) : Serializable