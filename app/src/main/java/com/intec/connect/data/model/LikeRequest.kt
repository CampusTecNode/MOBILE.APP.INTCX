package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class LikeRequest(
    @SerializedName("UserID") val userID: String,
    @SerializedName("ProductID") val productID: String
)
