package com.gopi.securevault.data.db.migration

import android.content.Context
import android.database.Cursor
import com.gopi.securevault.data.db.AppDatabase
import com.gopi.securevault.data.entities.*
import com.gopi.securevault.util.CryptoPrefs
import kotlinx.coroutines.runBlocking
import net.sqlcipher.database.SQLiteDatabase

object MigrationHelper {

    private const val MIGRATION_DONE_KEY = "migration_v6_done"

    fun needsMigration(context: Context): Boolean {
        val prefs = CryptoPrefs(context)
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        // Migration is needed if the DB file exists and the migration flag is not set
        return dbFile.exists() && !prefs.getBoolean(MIGRATION_DONE_KEY, false)
    }

    fun performMigration(context: Context) {
        if (!needsMigration(context)) {
            return
        }

        val prefs = CryptoPrefs(context)
        val passphrase = (prefs.getString("master_hash", null) ?: "fallback-key").toCharArray()
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)

        // 1. Open the old SQLCipher database directly
        val oldDb: SQLiteDatabase
        try {
            oldDb = SQLiteDatabase.openDatabase(dbFile.path, passphrase, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: Exception) {
            // If we can't open the old DB, we can't migrate.
            // We'll assume it's a fresh install or the DB is corrupt.
            // Mark migration as done to avoid getting stuck in a loop.
            prefs.putBoolean(MIGRATION_DONE_KEY, true)
            return
        }

        // 2. Read all data into memory using raw queries
        val banks = readBanks(oldDb)
        val cards = readCards(oldDb)
        val policies = readPolicies(oldDb)
        val aadhars = readAadhars(oldDb)
        val pans = readPans(oldDb)
        val voterIds = readVoterIds(oldDb)
        val licenses = readLicenses(oldDb)
        val miscs = readMiscs(oldDb)

        // 3. Close the old database
        oldDb.close()

        // 4. Close any existing Room instance and delete the old database file
        AppDatabase.closeInstance()
        if (dbFile.exists()) {
            dbFile.delete()
        }

        // 5. Create the new database and insert data
        val newDb = AppDatabase.get(context)
        runBlocking {
            banks.forEach { newDb.bankDao().insert(it) }
            cards.forEach { newDb.cardDao().insert(it) }
            policies.forEach { newDb.policyDao().insert(it) }
            aadhars.forEach { newDb.aadharDao().insert(it) }
            pans.forEach { newDb.panDao().insert(it) }
            voterIds.forEach { newDb.voterIdDao().insert(it) }
            licenses.forEach { newDb.licenseDao().insert(it) }
            miscs.forEach { newDb.miscDao().insert(it) }
        }
        AppDatabase.closeInstance()


        // 6. Mark migration as complete
        prefs.putBoolean(MIGRATION_DONE_KEY, true)
    }

    private fun readBanks(db: SQLiteDatabase): List<BankEntity> {
        val cursor = db.rawQuery("SELECT * FROM banks", null)
        val banks = mutableListOf<BankEntity>()
        while (cursor.moveToNext()) {
            banks.add(BankEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo")),
                bankName = cursor.getString(cursor.getColumnIndexOrThrow("bankName")),
                ifsc = cursor.getString(cursor.getColumnIndexOrThrow("ifsc")),
                cifNo = cursor.getString(cursor.getColumnIndexOrThrow("cifNo")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                profilePrivy = cursor.getString(cursor.getColumnIndexOrThrow("profilePrivy")),
                mPin = cursor.getString(cursor.getColumnIndexOrThrow("mPin")),
                tPin = cursor.getString(cursor.getColumnIndexOrThrow("tPin")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                privy = cursor.getString(cursor.getColumnIndexOrThrow("privy"))
            ))
        }
        cursor.close()
        return banks
    }

    private fun readCards(db: SQLiteDatabase): List<CardEntity> {
        val cursor = db.rawQuery("SELECT * FROM cards", null)
        val cards = mutableListOf<CardEntity>()
        while (cursor.moveToNext()) {
            cards.add(CardEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                bankName = cursor.getString(cursor.getColumnIndexOrThrow("bankName")),
                cardType = cursor.getString(cursor.getColumnIndexOrThrow("cardType")),
                cardNumber = cursor.getString(cursor.getColumnIndexOrThrow("cardNumber")),
                cvv = cursor.getString(cursor.getColumnIndexOrThrow("cvv")),
                validTill = cursor.getString(cursor.getColumnIndexOrThrow("validTill")),
                customerId = cursor.getString(cursor.getColumnIndexOrThrow("customerId")),
                pin = cursor.getString(cursor.getColumnIndexOrThrow("pin")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            ))
        }
        cursor.close()
        return cards
    }

    private fun readPolicies(db: SQLiteDatabase): List<PolicyEntity> {
        val cursor = db.rawQuery("SELECT * FROM policies", null)
        val policies = mutableListOf<PolicyEntity>()
        while (cursor.moveToNext()) {
            policies.add(PolicyEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                amount = cursor.getString(cursor.getColumnIndexOrThrow("amount")),
                company = cursor.getString(cursor.getColumnIndexOrThrow("company")),
                nextPremiumDate = cursor.getString(cursor.getColumnIndexOrThrow("nextPremiumDate")),
                premiumValue = cursor.getString(cursor.getColumnIndexOrThrow("premiumValue")),
                maturityValue = cursor.getString(cursor.getColumnIndexOrThrow("maturityValue")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            ))
        }
        cursor.close()
        return policies
    }

    private fun readAadhars(db: SQLiteDatabase): List<AadharEntity> {
        val cursor = db.rawQuery("SELECT * FROM aadhar", null)
        val aadhars = mutableListOf<AadharEntity>()
        while (cursor.moveToNext()) {
            aadhars.add(AadharEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                number = cursor.getString(cursor.getColumnIndexOrThrow("number")),
                dob = cursor.getString(cursor.getColumnIndexOrThrow("dob")),
                address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                documentPath = cursor.getString(cursor.getColumnIndexOrThrow("documentPath"))
            ))
        }
        cursor.close()
        return aadhars
    }

    private fun readPans(db: SQLiteDatabase): List<PanEntity> {
        val cursor = db.rawQuery("SELECT * FROM pan", null)
        val pans = mutableListOf<PanEntity>()
        while (cursor.moveToNext()) {
            pans.add(PanEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                documentPath = cursor.getString(cursor.getColumnIndexOrThrow("documentPath"))
            ))
        }
        cursor.close()
        return pans
    }

    private fun readVoterIds(db: SQLiteDatabase): List<VoterIdEntity> {
        val cursor = db.rawQuery("SELECT * FROM voter_id", null)
        val voterIds = mutableListOf<VoterIdEntity>()
        while (cursor.moveToNext()) {
            voterIds.add(VoterIdEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                voterIdNumber = cursor.getString(cursor.getColumnIndexOrThrow("voterIdNumber")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                documentPath = cursor.getString(cursor.getColumnIndexOrThrow("documentPath"))
            ))
        }
        cursor.close()
        return voterIds
    }

    private fun readLicenses(db: SQLiteDatabase): List<LicenseEntity> {
        val cursor = db.rawQuery("SELECT * FROM license", null)
        val licenses = mutableListOf<LicenseEntity>()
        while (cursor.moveToNext()) {
            licenses.add(LicenseEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                licenseNumber = cursor.getString(cursor.getColumnIndexOrThrow("licenseNumber")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                documentPath = cursor.getString(cursor.getColumnIndexOrThrow("documentPath"))
            ))
        }
        cursor.close()
        return licenses
    }

    private fun readMiscs(db: SQLiteDatabase): List<MiscEntity> {
        val cursor = db.rawQuery("SELECT * FROM misc", null)
        val miscs = mutableListOf<MiscEntity>()
        while (cursor.moveToNext()) {
            miscs.add(MiscEntity(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                number = cursor.getString(cursor.getColumnIndexOrThrow("number")),
                amount = cursor.getString(cursor.getColumnIndexOrThrow("amount")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                documentPath = cursor.getString(cursor.getColumnIndexOrThrow("documentPath"))
            ))
        }
        cursor.close()
        return miscs
    }
}