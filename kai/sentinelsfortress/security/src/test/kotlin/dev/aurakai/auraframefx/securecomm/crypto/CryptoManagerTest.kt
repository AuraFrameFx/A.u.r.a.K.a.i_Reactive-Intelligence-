package dev.aurakai.auraframefx.securecomm.crypto

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import kotlinx.coroutines.test.runTest
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import javax.crypto.AEADBadTagException
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test suite for CryptoManager focusing on JVM-safe surfaces:
 * - performKeyAgreement()
 * - deriveSessionKey()
 * - encrypt()/decrypt() sync and suspend variants
 *
 * We intentionally avoid getOrCreateKeyPair(), sign(), verify() as they rely on AndroidKeyStore,
 * which is not available in plain JVM unit tests.
 *
 * Framework: kotlin.test with JUnit platform.
 */
class CryptoManagerTest {

    // Provide a dummy Context; CryptoManager does not actually use it in the tested paths.
    // We avoid introducing new mocking dependencies by using a minimal stub.
    private val dummyContext = object : Context() {
        // All methods throw UnsupportedOperationException if ever accidentally called
        override fun getAssets() = throw UnsupportedOperationException()
        override fun getResources() = throw UnsupportedOperationException()
        override fun getPackageManager() = throw UnsupportedOperationException()
        override fun getContentResolver() = throw UnsupportedOperationException()
        override fun getMainLooper() = throw UnsupportedOperationException()
        override fun getApplicationContext() = this
        override fun setTheme(resid: Int) = throw UnsupportedOperationException()
        override fun getTheme() = throw UnsupportedOperationException()
        override fun getClassLoader() = throw UnsupportedOperationException()
        override fun getPackageName() = "dev.aurakai.auraframefx.test"
        override fun getApplicationInfo() = throw UnsupportedOperationException()
        override fun getPackageResourcePath() = throw UnsupportedOperationException()
        override fun getPackageCodePath() = throw UnsupportedOperationException()
        override fun getSharedPreferences(name: String?, mode: Int) =
            throw UnsupportedOperationException()

        override fun moveSharedPreferencesFrom(sourceContext: Context?, name: String?) =
            throw UnsupportedOperationException()

        override fun deleteSharedPreferences(name: String?) = throw UnsupportedOperationException()
        override fun openFileInput(name: String?) = throw UnsupportedOperationException()
        override fun openFileOutput(name: String?, mode: Int) =
            throw UnsupportedOperationException()

        override fun deleteFile(name: String?) = throw UnsupportedOperationException()
        override fun getFileStreamPath(name: String?) = throw UnsupportedOperationException()
        override fun getDataDir() = throw UnsupportedOperationException()
        override fun getFilesDir() = throw UnsupportedOperationException()
        override fun getNoBackupFilesDir() = throw UnsupportedOperationException()
        override fun getExternalFilesDir(type: String?) = throw UnsupportedOperationException()
        override fun getExternalFilesDirs(type: String?) = throw UnsupportedOperationException()
        override fun getObbDir() = throw UnsupportedOperationException()
        override fun getObbDirs() = throw UnsupportedOperationException()
        override fun getCacheDir() = throw UnsupportedOperationException()
        override fun getCodeCacheDir() = throw UnsupportedOperationException()
        override fun getExternalCacheDir() = throw UnsupportedOperationException()
        override fun getExternalCacheDirs() = throw UnsupportedOperationException()
        override fun getExternalMediaDirs() = throw UnsupportedOperationException()
        override fun fileList() = throw UnsupportedOperationException()
        override fun getDir(name: String?, mode: Int) = throw UnsupportedOperationException()
        override fun openOrCreateDatabase(
            mode: Int,
            factory: android.database.sqlite.SQLiteDatabase.CursorFactory?

            override fun openOrCreateDatabase(
        name: String?,
        mode: Int,
        factory: android.database.sqlite.SQLiteDatabase.CursorFactory?,
        errorHandler: android.database.DatabaseErrorHandler?
        ) = throw UnsupportedOperationException()

        override fun moveDatabaseFrom(sourceContext: Context?, name: String?) =
            throw UnsupportedOperationException()

        override fun deleteDatabase(name: String?) = throw UnsupportedOperationException()
        override fun getDatabasePath(name: String?) = throw UnsupportedOperationException()
        override fun databaseList() = throw UnsupportedOperationException()
        override fun getWallpaper() = throw UnsupportedOperationException()
        override fun peekWallpaper() = throw UnsupportedOperationException()
        override fun getWallpaperDesiredMinimumWidth() = throw UnsupportedOperationException()
        override fun getWallpaperDesiredMinimumHeight() = throw UnsupportedOperationException()
        override fun setWallpaper(bitmap: android.graphics.Bitmap?) =
            throw UnsupportedOperationException()

        override fun setWallpaper(data: java.io.InputStream?) =
            throw UnsupportedOperationException()

        override fun clearWallpaper() = throw UnsupportedOperationException()
        throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        override fun startActivities(
        ) = throw UnsupportedOperationException()

        override fun startIntentSender(
            intent: android.content.IntentSender?,
            flagsMask: Int,
            flagsValues: Int,
            extraFlags: Int
        ) = throw UnsupportedOperationException()

        override fun startIntentSender(
            intent: android.content.IntentSender?,
            flagsMask: Int,
            flagsValues: Int,
            extraFlags: Int,
        ) = throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        override fun sendOrderedBroadcast(
            receiverPermission: String?
        ) = throw UnsupportedOperationException()

        override fun sendOrderedBroadcast(
            receiverPermission: String?,
            initialCode: Int,
            initialData: String?,
        ) = throw UnsupportedOperationException()

        override fun sendBroadcastAsUser(
            user: android.os.UserHandle?
        ) = throw UnsupportedOperationException()

        override fun sendBroadcastAsUser(
            user: android.os.UserHandle?,
            receiverPermission: String?
        ) = throw UnsupportedOperationException()

        override fun sendOrderedBroadcastAsUser(
            user: android.os.UserHandle?,
            receiverPermission: String?,
            initialCode: Int,
            initialData: String?,
        ) = throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        override fun sendStickyOrderedBroadcast(
            initialCode: Int,
            initialData: String?,
        ) = throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        override fun sendStickyBroadcastAsUser(
            user: android.os.UserHandle?
        ) = throw UnsupportedOperationException()

        override fun sendStickyOrderedBroadcastAsUser(
            user: android.os.UserHandle?,
            initialCode: Int,
            initialData: String?,
        ) = throw UnsupportedOperationException()

        override fun removeStickyBroadcastAsUser(
            user: android.os.UserHandle?
        ) = throw UnsupportedOperationException()

        override fun registerReceiver(
            filter: android.content.IntentFilter?
        ) = throw UnsupportedOperationException()

        override fun registerReceiver(
            filter: android.content.IntentFilter?,
            flags: Int
        ) = throw UnsupportedOperationException()

        override fun registerReceiver(
            filter: android.content.IntentFilter?,
            broadcastPermission: String?,
        ) = throw UnsupportedOperationException()

        override fun registerReceiver(
            filter: android.content.IntentFilter?,
            broadcastPermission: String?,
            flags: Int
        ) = throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        override fun startInstrumentation(
            className: ComponentName, profileFile: String?, arguments: Bundle?
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun stopService(name: Intent?) = throw UnsupportedOperationException()

        override fun bindService(
            flags: Int
        ) = throw UnsupportedOperationException()

        throw UnsupportedOperationException()

        override fun startInstrumentation(
            profileFile: String?,
        ) = throw UnsupportedOperationException()

        override fun getSystemService(name: String) = null
        override fun getSystemServiceName(serviceClass: Class<*>) = null
        override fun checkPermission(permission: String, pid: Int, uid: Int) =
            throw UnsupportedOperationException()

        override fun checkSelfPermission(permission: String): Int {
            return PackageManager.PERMISSION_GRANTED
        }

        override fun checkCallingPermission(permission: String) =
            throw UnsupportedOperationException()

        override fun bindService(
            service: Intent, conn: ServiceConnection, flags: Int
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun checkCallingOrSelfPermission(permission: String) =
            throw UnsupportedOperationException()

        override fun enforcePermission(permission: String, pid: Int, uid: Int, message: String?) =
            throw UnsupportedOperationException()

        override fun enforceCallingPermission(permission: String, message: String?) =
            throw UnsupportedOperationException()

        override fun enforceCallingOrSelfPermission(permission: String, message: String?) =
            throw UnsupportedOperationException()

        override fun grantUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) =
            throw UnsupportedOperationException()

        override fun revokeUriPermission(uri: android.net.Uri?, modeFlags: Int) =
            throw UnsupportedOperationException()

        override fun revokeUriPermission(
            toPackage: String?,
            uri: android.net.Uri?,
            modeFlags: Int
        ) = throw UnsupportedOperationException()

        override fun checkUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int) =
            throw UnsupportedOperationException()

        override fun checkCallingUriPermission(uri: android.net.Uri?, modeFlags: Int) =
            throw UnsupportedOperationException()

        override fun checkCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int) =
            throw UnsupportedOperationException()

        override fun checkUriPermission(
            uri: android.net.Uri?,
            readPermission: String?,
            writePermission: String?,
            pid: Int,
            uid: Int,
            modeFlags: Int
        ) = throw UnsupportedOperationException()

        override fun enforceUriPermission(
            uri: android.net.Uri?,
            pid: Int,
            uid: Int,
            modeFlags: Int,
            message: String?
        ) = throw UnsupportedOperationException()

        override fun enforceCallingUriPermission(
            uri: android.net.Uri?,
            modeFlags: Int,
            message: String?
        ) = throw UnsupportedOperationException()

        override fun enforceCallingOrSelfUriPermission(
            uri: android.net.Uri?,
            modeFlags: Int,
            message: String?
        ) = throw UnsupportedOperationException()

        override fun enforceUriPermission(
            uri: android.net.Uri?,
            readPermission: String?,
            writePermission: String?,
            pid: Int,
            uid: Int,
            modeFlags: Int,
            message: String?
        ) = throw UnsupportedOperationException()

        override fun createPackageContext(packageName: String?, flags: Int) =
            throw UnsupportedOperationException()

        override fun createContextForSplit(splitName: String?) =
            throw UnsupportedOperationException()

        override fun createConfigurationContext(overrideConfiguration: android.content.res.Configuration) =
            throw UnsupportedOperationException()

        override fun createDisplayContext(display: android.view.Display) =
            throw UnsupportedOperationException()

        override fun createDeviceProtectedStorageContext() = throw UnsupportedOperationException()
        override fun isDeviceProtectedStorage() = false
    }

    private val crypto = CryptoManager(dummyContext)

    private fun generateEcKeyPairP256(): java.security.KeyPair {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        return kpg.generateKeyPair()
    }

    private fun deriveAesKey(
        shared: ByteArray,
        salt: ByteArray = ByteArray(32),
        info: ByteArray = "AuraFrameFX-SecureComm".toByteArray()
    ): SecretKey {
        return crypto.deriveSessionKey(shared, salt, info)
    }

    @Test
    fun `performKeyAgreement yields identical shared secret for both parties`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()

        val sharedA = crypto.performKeyAgreement(a.private, b.public)
        val sharedB = crypto.performKeyAgreement(b.private, a.public)

        assertContentEquals(sharedA, sharedB)
        assertTrue(sharedA.isNotEmpty(), "Shared secret should not be empty")
    }

    @Test
    fun `deriveSessionKey is deterministic for same inputs and 256-bit AES key`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()

        val sharedA = crypto.performKeyAgreement(a.private, b.public)
        val sharedB = crypto.performKeyAgreement(b.private, a.public)

        val salt = ByteArray(32) { 1 } // non-zero salt for determinism
        val info = "AuraFrameFX-SecureComm".toByteArray()

        val key1 = crypto.deriveSessionKey(sharedA, salt, info)
        val key2 = crypto.deriveSessionKey(sharedB, salt, info)

        assertContentEquals((key1 as SecretKeySpec).encoded, (key2 as SecretKeySpec).encoded)
        assertEquals("AES", key1.algorithm)
        assertEquals(32, (key1 as SecretKeySpec).encoded.size, "Key length must be 256-bit")
    }

    @Test
    fun `deriveSessionKey changes with different salt and info`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()
        val shared = crypto.performKeyAgreement(a.private, b.public)

        val salt1 = ByteArray(32) { 2 }
        val salt2 = ByteArray(32) { 3 }
        val info1 = "Aura".toByteArray()
        val info2 = "FrameFX".toByteArray()

        val k11 = crypto.deriveSessionKey(shared, salt1, info1)
        val k12 = crypto.deriveSessionKey(shared, salt1, info2)
        val k21 = crypto.deriveSessionKey(shared, salt2, info1)

        assertNotEquals(
            (k11 as SecretKeySpec).encoded.contentToString(),
            (k12 as SecretKeySpec).encoded.contentToString()
        )
        assertNotEquals(
            (k11 as SecretKeySpec).encoded.contentToString(),
            (k21 as SecretKeySpec).encoded.contentToString()
        )
    }

    @Test
    fun `encrypt sync then decrypt returns original plaintext and GCM tag increases size`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()
        val shared = crypto.performKeyAgreement(a.private, b.public)
        val key = deriveAesKey(shared)

        val data = "hello secure world".encodeToByteArray()
        val (ciphertext, iv) = crypto.encrypt(data, key)
        val plaintext = crypto.decrypt(ciphertext, key, iv)

        assertContentEquals(data, plaintext)
        assertEquals(
            data.size + 16,
            ciphertext.size,
            "GCM tag (16 bytes) should increase ciphertext length by 16"
        )
    }

    @Test
    fun `encrypt twice produces different IV and ciphertext for same plaintext`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()
        val shared = crypto.performKeyAgreement(a.private, b.public)
        val key = deriveAesKey(shared)

        val data = "same plaintext".encodeToByteArray()
        val (ct1, iv1) = crypto.encrypt(data, key)
        val (ct2, iv2) = crypto.encrypt(data, key)

        assertNotEquals(
            iv1.contentToString(),
            iv2.contentToString(),
            "IVs must be random and unique"
        )
        assertNotEquals(
            ct1.contentToString(),
            ct2.contentToString(),
            "Ciphertexts should differ due to different IVs"
        )
    }

    @Test
    fun `decrypt with wrong key fails with AEADBadTagException`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()
        val shared = crypto.performKeyAgreement(a.private, b.public)
        val rightKey = deriveAesKey(shared)
        val wrongKey = SecretKeySpec(ByteArray(32) { 9 }, "AES")

        val data = ByteArray(128) { it.toByte() }
        val (ciphertext, iv) = crypto.encrypt(data, rightKey)

        assertFailsWith<AEADBadTagException> {
            crypto.decrypt(ciphertext, wrongKey, iv)
        }
    }

    @Test
    fun `decrypt with wrong IV fails with AEADBadTagException`() {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()
        val shared = crypto.performKeyAgreement(a.private, b.public)
        val key = deriveAesKey(shared)

        val data = "data".encodeToByteArray()
        val (ciphertext, _) = crypto.encrypt(data, key)
        val wrongIv = ByteArray(12) { 7 } // AES-GCM standard 96-bit IV size

        assertFailsWith<AEADBadTagException> {
            crypto.decrypt(ciphertext, key, wrongIv)
        }
    }

    @Test
    fun `suspend encrypt produces decryptable output equivalent to original plaintext`() = runTest {
        val a = generateEcKeyPairP256()
        val b = generateEcKeyPairP256()
        val shared = crypto.performKeyAgreement(a.private, b.public)
        val key = deriveAesKey(shared)

        val data = "suspend variant test payload".encodeToByteArray()

        val roundTrip = crypto.decrypt(ciphertext, key, iv)
        assertContentEquals(data, roundTrip)
    }
}