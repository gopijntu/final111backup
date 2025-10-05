package com.gopi.securevault.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gopi.securevault.data.dao.*
import com.gopi.securevault.data.db.converters.EncryptionConverter
import com.gopi.securevault.data.entities.*
import com.gopi.securevault.util.CryptoPrefs

@Database(
    entities = [BankEntity::class, CardEntity::class, PolicyEntity::class, AadharEntity::class, PanEntity::class, VoterIdEntity::class, LicenseEntity::class, MiscEntity::class],
    version = 6, // Bump version for migration
    exportSchema = false
)
@TypeConverters(EncryptionConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankDao(): BankDao
    abstract fun cardDao(): CardDao
    abstract fun policyDao(): PolicyDao
    abstract fun aadharDao(): AadharDao
    abstract fun panDao(): PanDao
    abstract fun voterIdDao(): VoterIdDao
    abstract fun licenseDao(): LicenseDao
    abstract fun miscDao(): MiscDao

    suspend fun clearAllTablesManually() {
        clearAllTables()
    }

    companion object {
        const val DATABASE_NAME = "securevault.db"
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
            }
        }

        fun changeDatabasePassword(context: Context, newPassword: String) {
            val prefs = CryptoPrefs(context)
            prefs.putString("master_hash", newPassword)
        }

        fun closeInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}