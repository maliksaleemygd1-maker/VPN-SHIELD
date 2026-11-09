package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.SpeedRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedRecordDao {
    @Query("SELECT * FROM speed_records ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSpeedRecords(): Flow<List<SpeedRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: SpeedRecordEntity)

    @Query("DELETE FROM speed_records")
    suspend fun clearHistory()
}
