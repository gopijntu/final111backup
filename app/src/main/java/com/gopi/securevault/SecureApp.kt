package com.gopi.securevault

import android.app.Application
import com.gopi.securevault.data.db.converters.EncryptionConverter
import com.gopi.securevault.data.db.migration.MigrationHelper
import com.gopi.securevault.security.EncryptionService

class SecureApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Perform data migration if needed
        MigrationHelper.performMigration(this)

        // Initialize EncryptionService
        val encryptionService = EncryptionService()
        EncryptionConverter.encryptionService = encryptionService
    }
}
