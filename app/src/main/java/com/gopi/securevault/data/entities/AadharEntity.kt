package com.gopi.securevault.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gopi.securevault.data.db.converters.EncryptionConverter

@Entity(tableName = "aadhar")
data class AadharEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @TypeConverters(EncryptionConverter::class)
    val name: String?,
    @TypeConverters(EncryptionConverter::class)
    val number: String?,
    @TypeConverters(EncryptionConverter::class)
    val dob: String?,
    @TypeConverters(EncryptionConverter::class)
    val address: String?,
    @TypeConverters(EncryptionConverter::class)
    val notes: String?,
    val documentPath: String?
)
