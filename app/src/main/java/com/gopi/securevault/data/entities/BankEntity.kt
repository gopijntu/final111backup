package com.gopi.securevault.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gopi.securevault.data.db.converters.EncryptionConverter

@Entity(tableName = "banks")
data class BankEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String?,
    @TypeConverters(EncryptionConverter::class)
    val accountNo: String,
    @TypeConverters(EncryptionConverter::class)
    val bankName: String?,
    @TypeConverters(EncryptionConverter::class)
    val ifsc: String?,
    @TypeConverters(EncryptionConverter::class)
    val cifNo: String?,
    @TypeConverters(EncryptionConverter::class)
    val username: String?,
    @TypeConverters(EncryptionConverter::class)
    val profilePrivy: String?,
    @TypeConverters(EncryptionConverter::class)
    val mPin: String?,
    @TypeConverters(EncryptionConverter::class)
    val tPin: String?,
    @TypeConverters(EncryptionConverter::class)
    val notes: String?,
    @TypeConverters(EncryptionConverter::class)
    val privy: String?
)
