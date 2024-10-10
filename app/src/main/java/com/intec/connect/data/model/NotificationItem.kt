package com.intec.connect.data.model // Or your preferred package

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications") // Specify the table name
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Optional auto-generated ID
    val title: String,
    val body: String,
    val timestamp: Long
)