package com.example.data.repository

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val SALT_LENGTH_BYTE = 16
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH_BIT = 256

    fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plaintext into format: [16 bytes salt] + [12 bytes IV] + [ciphertext + tag]
     * Encoded as Base64.
     */
    fun encrypt(plainText: String, masterPassword: String): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH_BYTE).apply { random.nextBytes(this) }
        val iv = ByteArray(IV_LENGTH_BYTE).apply { random.nextBytes(this) }

        val key = deriveKey(masterPassword, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BIT, iv))

        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(salt.size + iv.size + cipherBytes.size)

        System.arraycopy(salt, 0, combined, 0, salt.size)
        System.arraycopy(iv, 0, combined, salt.size, iv.size)
        System.arraycopy(cipherBytes, 0, combined, salt.size + iv.size, cipherBytes.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts combined Base64 payload using the master password.
     */
    fun decrypt(encryptedBase64: String, masterPassword: String): Result<String> {
        return runCatching {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            if (combined.size < SALT_LENGTH_BYTE + IV_LENGTH_BYTE) {
                throw IllegalArgumentException("Invalid encrypted payload size")
            }

            val salt = ByteArray(SALT_LENGTH_BYTE)
            val iv = ByteArray(IV_LENGTH_BYTE)
            val cipherBytes = ByteArray(combined.size - SALT_LENGTH_BYTE - IV_LENGTH_BYTE)

            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH_BYTE)
            System.arraycopy(combined, SALT_LENGTH_BYTE, iv, 0, IV_LENGTH_BYTE)
            System.arraycopy(combined, SALT_LENGTH_BYTE + IV_LENGTH_BYTE, cipherBytes, 0, cipherBytes.size)

            val key = deriveKey(masterPassword, salt)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BIT, iv))

            val decryptedBytes = cipher.doFinal(cipherBytes)
            String(decryptedBytes, Charsets.UTF_8)
        }
    }
}
