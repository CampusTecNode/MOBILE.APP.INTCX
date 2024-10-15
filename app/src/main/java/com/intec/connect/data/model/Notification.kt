package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("ID") val notificationId: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Message") val message: String,
    @SerializedName("Type") val body: String,
    @SerializedName("IsRead") val isRead: Boolean,
    @SerializedName("CreatedAt") val createdAt: String
)