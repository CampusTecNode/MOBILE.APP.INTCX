package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class CartStatus(
    @SerializedName("ID") val id: Int,
    @SerializedName("Name") val name: String
)