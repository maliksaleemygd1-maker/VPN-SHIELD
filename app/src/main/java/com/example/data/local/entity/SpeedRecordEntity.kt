package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speed_records")
data class SpeedRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val serverId: String,
    val serverName: String,
    val downloadMbps: Float,
    val uploadMbps: Float,
    val pingMs: Int
)
