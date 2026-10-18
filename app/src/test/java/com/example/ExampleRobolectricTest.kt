package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.CryptoManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("UniNotes", appName)
    }

    @Test
    fun `crypto encryption and decryption round trip`() {
        val plainText = "UniNotes Top Secret Backup Content"
        val password = "SuperSecretPassword123!"

        val encrypted = CryptoManager.encrypt(plainText, password)
        assertNotNull(encrypted)
        assertNotEquals(plainText, encrypted)

        val decryptedResult = CryptoManager.decrypt(encrypted, password)
        assertTrue(decryptedResult.isSuccess)
        assertEquals(plainText, decryptedResult.getOrNull())
    }

    @Test
    fun `crypto decrypt with wrong password fails`() {
        val plainText = "Private Note Data"
        val password = "CorrectPassword"
        val wrongPassword = "WrongPassword"

        val encrypted = CryptoManager.encrypt(plainText, password)
        val decryptedResult = CryptoManager.decrypt(encrypted, wrongPassword)
        assertTrue(decryptedResult.isFailure)
    }
}
