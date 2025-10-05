package com.gopi.securevault.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gopi.securevault.data.db.converters.EncryptionConverter

@Entity(tableName = "policies")
data class PolicyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @TypeConverters(EncryptionConverter::class)
    val name: String?,
    @TypeConverters(EncryptionConverter::class)
    val amount: String?,
    @TypeConverters(EncryptionConverter::class)
    val company: String?,
    @TypeConverters(EncryptionConverter::class)
    val nextPremiumDate: String?,
    @TypeConverters(EncryptionConverter::class)
    val premiumValue: String?,
    @TypeConverters(EncryptionConverter::class)
    val maturityValue: String?,
    @TypeConverters(EncryptionConverter::class)
    val notes: String?
)
