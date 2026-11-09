package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class ServerEntity(
    @PrimaryKey val id: String,
    val countryCode: String,
    val countryName: String,
    val flagEmoji: String,
    val cityName: String,
    val hostname: String,
    val endpointIp: String,
    val port: Int,
    val publicKey: String,
    val latencyMs: Int,
    val loadPercent: Int,
    val packetLossPercent: Float,
    val capacityMbps: Int,
    val supportsDedicatedIp: Boolean,
    val isFavorite: Boolean = false,
    val latitude: Float = 0f,
    val longitude: Float = 0f
)
