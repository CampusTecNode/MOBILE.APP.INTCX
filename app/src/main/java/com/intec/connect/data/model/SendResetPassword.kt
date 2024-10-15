package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class SendResetPassword(
    @SerializedName("email") val email: String
)