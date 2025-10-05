package com.gopi.securevault.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gopi.securevault.data.db.converters.EncryptionConverter

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bankName: String?,
    val cardType: String?,
    @TypeConverters(EncryptionConverter::class)
    val cardNumber: String?,
    @TypeConverters(EncryptionConverter::class)
    val cvv: String?,
    @TypeConverters(EncryptionConverter::class)
    val validTill: String?,
    @TypeConverters(EncryptionConverter::class)
    val customerId: String?,
    @TypeConverters(EncryptionConverter::class)
    val pin: String?,
    @TypeConverters(EncryptionConverter::class)
    val notes: String?
)
