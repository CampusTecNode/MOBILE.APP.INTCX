package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("userID") val userID: String,
    @SerializedName("name") val name: String,
    @SerializedName("lastname") val lastname: String
)