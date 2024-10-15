package com.intec.connect.data.model

import com.google.gson.annotations.SerializedName

data class SpacesItem(
    @SerializedName("Available") val available: Boolean,
    @SerializedName("Capacity") val capacity: Int,
    @SerializedName("Description") val description: String,
    @SerializedName("ID") val spacesId: Int,
    @SerializedName("Name") val name: String
)