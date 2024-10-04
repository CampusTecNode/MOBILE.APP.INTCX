package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class RegisterModel(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)