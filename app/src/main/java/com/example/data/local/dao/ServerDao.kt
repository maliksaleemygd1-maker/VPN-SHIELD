package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM vpn_servers ORDER BY countryName ASC, cityName ASC")
    fun getAllServersFlow(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM vpn_servers WHERE id = :id LIMIT 1")
    suspend fun getServerById(id: String): ServerEntity?

    @Query("SELECT * FROM vpn_servers ORDER BY latencyMs ASC LIMIT 1")
    suspend fun getFastestServer(): ServerEntity?

    @Query("SELECT * FROM vpn_servers WHERE countryCode = :code ORDER BY latencyMs ASC")
    fun getServersByCountry(code: String): Flow<List<ServerEntity>>

    @Query("SELECT * FROM vpn_servers WHERE supportsDedicatedIp = 1")
    fun getDedicatedIpServers(): Flow<List<ServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    @Update
    suspend fun updateServer(server: ServerEntity)

    @Query("SELECT COUNT(*) FROM vpn_servers")
    suspend fun getCount(): Int

    @Query("DELETE FROM vpn_servers")
    suspend fun clearAll()
}
