package dev.aurakai.auraframefx.securecomm.keystore

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.*
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureKeyStoreTest {

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var sharedPrefs: SharedPreferences

    @MockK
    lateinit var editor: SharedPreferences.Editor

    @MockK
    lateinit var keyStore: KeyStore

    @MockK
    lateinit var secretKeyEntry: KeyStore.SecretKeyEntry

    @MockK
    lateinit var secretKey: SecretKey

    @MockK
    lateinit var cipher: Cipher

    @MockK
    lateinit var keyGenerator: KeyGenerator

    private lateinit var secureKeyStore: SecureKeyStore

    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        // Mock Android Base64 - using NO_WRAP as per implementation
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), Base64.NO_WRAP) } answers {
            java.util.Base64.getEncoder().encodeToString(firstArg())
        }
        every { Base64.decode(any<String>(), Base64.NO_WRAP) } answers {
            java.util.Base64.getDecoder().decode(firstArg<String>())
        }

        // Mock Context and SharedPreferences - using "secure_prefs" as per implementation
        every {
            context.getSharedPreferences(
                "secure_prefs",
                Context.MODE_PRIVATE
            )
        } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.apply() } just Runs

        // Mock KeyStore
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance("AndroidKeyStore") } returns keyStore
        every { keyStore.load(null) } just Runs

        // Mock KeyGenerator
        mockkStatic(KeyGenerator::class)
        every {
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
        } returns keyGenerator
        every { keyGenerator.init(any<KeyGenParameterSpec>()) } just Runs
        every { keyGenerator.generateKey() } returns secretKey

        // Mock Cipher
        mockkStatic(Cipher::class)
        every { Cipher.getInstance("AES/GCM/NoPadding") } returns cipher

        secureKeyStore = SecureKeyStore(context)
    }

    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun storeData_encryptsAndStoresSuccessfully() {
        // ... earlier setup and verifications ...

        verify { editor.apply() }

        // Verify stored data layout: Base64(IV(12) + ciphertext)
        val decoded = java.util.Base64.getDecoder().decode(storedData.captured)
        assertTrue(decoded.size >= 12)
        assertArrayEquals(iv, decoded.copyOfRange(0, 12))
        assertArrayEquals(encryptedData, decoded.copyOfRange(12, decoded.size))
    }

    @Test
    fun storeData_usesExistingKey_whenKeyExists() {
        val key = "test_key"
        val data = "sensitive data".toByteArray()
        val keyAlias = "aura_secure_key_$key"
        val iv = ByteArray(12) { it.toByte() }
        val encryptedData = "encrypted_data".toByteArray()

        // Mock key retrieval (key already exists)
        every { keyStore.containsAlias(keyAlias) } returns true
        every { keyStore.getEntry(keyAlias, null) } returns secretKeyEntry
        every { secretKeyEntry.secretKey } returns secretKey

        // Mock encryption
        every { cipher.init(Cipher.ENCRYPT_MODE, secretKey) } just Runs
        every { cipher.iv } returns iv
        every { cipher.doFinal(data) } returns encryptedData

        secureKeyStore.storeData(key, data)

        verify { keyStore.getEntry(keyAlias, null) }
        verify(exactly = 0) { keyGenerator.generateKey() } // Should not generate new key
        verify { cipher.init(Cipher.ENCRYPT_MODE, secretKey) }
        verify { cipher.doFinal(data) }
    }

    @Test
    fun retrieveData_decryptsAndReturnsData() {
        val key = "test_key"
        val keyAlias = "aura_secure_key_$key"
        val originalData = "sensitive data".toByteArray()
        val iv = ByteArray(12) { it.toByte() }
        val encryptedData = "encrypted_data".toByteArray()
        val combinedData = iv + encryptedData
        val base64Data = java.util.Base64.getEncoder().encodeToString(combinedData)

        // Mock key retrieval
        every { keyStore.containsAlias(keyAlias) } returns true
        every { keyStore.getEntry(keyAlias, null) } returns secretKeyEntry
        every { secretKeyEntry.secretKey } returns secretKey

        // Mock SharedPreferences retrieval
        every { sharedPrefs.getString(key, null) } returns base64Data

        // Mock decryption
        every { cipher.init(Cipher.DECRYPT_MODE, secretKey, any<GCMParameterSpec>()) } answers {
            val spec = arg<GCMParameterSpec>(2)
            assertEquals(128, spec.tLen) // Verify GCM tag length
            assertArrayEquals(iv, spec.iv) // Verify IV
        }
        every { cipher.doFinal(encryptedData) } returns originalData

        val result = secureKeyStore.retrieveData(key)

        assertNotNull(result)
        assertArrayEquals(originalData, result)
        verify { cipher.init(Cipher.DECRYPT_MODE, secretKey, any<GCMParameterSpec>()) }
        verify { cipher.doFinal(encryptedData) }
    }

    @Test
    fun retrieveData_returnsNull_whenKeyNotFound() {
        val key = "nonexistent_key"
        every { sharedPrefs.getString(key, null) } returns null

        val result = secureKeyStore.retrieveData(key)

        assertNull(result)
    }

    @Test
    fun retrieveData_returnsNull_whenDecryptionFails() {
        val key = "test_key"
        val keyAlias = "aura_secure_key_$key"
        val iv = ByteArray(12) { it.toByte() }
        val encryptedData = "encrypted_data".toByteArray()
        val combinedData = iv + encryptedData
        val base64Data = java.util.Base64.getEncoder().encodeToString(combinedData)

        every { keyStore.containsAlias(keyAlias) } returns true
        every { keyStore.getEntry(keyAlias, null) } returns secretKeyEntry
        every { secretKeyEntry.secretKey } returns secretKey
        every { sharedPrefs.getString(key, null) } returns base64Data

        // Mock decryption failure
        every { cipher.init(Cipher.DECRYPT_MODE, secretKey, any<GCMParameterSpec>()) } just Runs
        every { cipher.doFinal(any<ByteArray>()) } throws javax.crypto.AEADBadTagException("Decryption failed")

        val result = secureKeyStore.retrieveData(key)

        assertNull(result)
    }

    @Test
    fun retrieveData_returnsNull_whenDataTooShort() {
        val key = "test_key"
        val shortData = ByteArray(5) // Less than 12 bytes (GCM_IV_LENGTH)
        val base64ShortData = java.util.Base64.getEncoder().encodeToString(shortData)

        every { sharedPrefs.getString(key, null) } returns base64ShortData

        val result = secureKeyStore.retrieveData(key)

        assertNull(result)
    }

    @Test
    fun retrieveData_returnsNull_whenBase64DecodingFails() {
        val key = "test_key"
        every { sharedPrefs.getString(key, null) } returns "invalid_base64_data"
        every {
            Base64.decode(
                "invalid_base64_data",
                Base64.NO_WRAP
            )
        } throws IllegalArgumentException("Invalid Base64")

        val result = secureKeyStore.retrieveData(key)

        assertNull(result)
    }

    @Test
    fun removeData_removesFromSharedPreferences() {
        val key = "test_key"

        secureKeyStore.removeData(key)

        verify { editor.remove(key) }
        verify { editor.apply() }
    }

    @Test
    fun clearAllData_clearsSharedPreferences() {
        secureKeyStore.clearAllData()

        verify { editor.clear() }
        verify { editor.apply() }
    }

    @Test
    fun encryptDecrypt_roundTrip_worksCorrectly() {
        val key = "round_trip_key"
        val keyAlias = "aura_secure_key_$key"
        val originalData = "test data for round trip".toByteArray()
        val iv = ByteArray(12) { it.toByte() }
        val encryptedData = "mock_encrypted_data".toByteArray()
        iv + encryptedData

        // Setup for store operation
        every { keyStore.containsAlias(keyAlias) } returns false
        every { cipher.init(Cipher.ENCRYPT_MODE, secretKey) } just Runs
        every { cipher.iv } returns iv
        every { cipher.doFinal(originalData) } returns encryptedData

        val storedDataSlot = slot<String>()
        every { editor.putString(key, capture(storedDataSlot)) } returns editor

        // Store the data
        secureKeyStore.storeData(key, originalData)

        // Setup for retrieve operation
        every { keyStore.containsAlias(keyAlias) } returns true
        every { keyStore.getEntry(keyAlias, null) } returns secretKeyEntry
        every { secretKeyEntry.secretKey } returns secretKey
        every { sharedPrefs.getString(key, null) } answers { storedDataSlot.captured }
        every { cipher.init(Cipher.DECRYPT_MODE, secretKey, any<GCMParameterSpec>()) } just Runs
        every { cipher.doFinal(encryptedData) } returns originalData

        // Retrieve the data
        val result = secureKeyStore.retrieveData(key)

        assertNotNull(result)
        assertArrayEquals(originalData, result)
    }
}