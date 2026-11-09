package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ServerDao
import com.example.data.local.dao.SpeedRecordDao
import com.example.data.local.entity.ServerEntity
import com.example.data.local.entity.SpeedRecordEntity

@Database(
    entities = [ServerEntity::class, SpeedRecordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class VpnDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun speedRecordDao(): SpeedRecordDao

    companion object {
        @Volatile
        private var INSTANCE: VpnDatabase? = null

        fun getInstance(context: Context): VpnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VpnDatabase::class.java,
                    "protonplus_vpn.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
