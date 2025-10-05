package com.gopi.securevault.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gopi.securevault.data.db.converters.EncryptionConverter

@Entity(tableName = "license")
data class LicenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @TypeConverters(EncryptionConverter::class)
    val name: String?,
    @TypeConverters(EncryptionConverter::class)
    val licenseNumber: String?,
    @TypeConverters(EncryptionConverter::class)
    val notes: String?,
    val documentPath: String?
)
