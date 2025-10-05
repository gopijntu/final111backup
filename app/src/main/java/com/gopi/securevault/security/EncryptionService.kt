package com.gopi.securevault.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptionService {

    private val KEY_ALIAS = "secure_vault_aes_key"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val IV_SIZE = 12 // bytes
    private val TAG_SIZE = 128 // bits

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    private fun getSecretKey(): SecretKey {
        return (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
            ?: generateSecretKey()
    }

    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
        }.build()
        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }

    fun encrypt(data: String?): String? {
        if (data == null) return null
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        // Prepend IV to ciphertext
        val byteBuffer = ByteBuffer.allocate(iv.size + ciphertext.size)
        byteBuffer.put(iv)
        byteBuffer.put(ciphertext)
        return Base64.encodeToString(byteBuffer.array(), Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String?): String? {
        if (encryptedData == null) return null
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)

        if (decodedData.size <= IV_SIZE) {
            return null
        }

        val byteBuffer = ByteBuffer.wrap(decodedData)
        val iv = ByteArray(IV_SIZE)
        byteBuffer.get(iv)
        val ciphertext = ByteArray(byteBuffer.remaining())
        byteBuffer.get(ciphertext)

        try {
            val spec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}