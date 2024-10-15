package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class NotificationBody(
    @SerializedName("Title") val title: String,
    @SerializedName("Message") val message: String,
    @SerializedName("UserID") val userId: String,
    @SerializedName("Type") val type: String
)
