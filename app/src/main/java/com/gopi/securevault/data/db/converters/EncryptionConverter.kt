package com.gopi.securevault.data.db.converters

import androidx.room.TypeConverter
import com.gopi.securevault.security.EncryptionService

class EncryptionConverter {
    companion object {
        lateinit var encryptionService: EncryptionService
    }

    @TypeConverter
    fun fromString(value: String?): String? {
        return value?.let { encryptionService.decrypt(it) }
    }

    @TypeConverter
    fun toString(value: String?): String? {
        return value?.let { encryptionService.encrypt(it) }
    }
}