package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SampleData
import com.example.model.BookingStatus
import com.example.model.PropertyType
import com.example.model.allPhotoItems
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource is MobiHome`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MobiHome", appName)
    }

    @Test
    fun `verify sample properties data integrity`() {
        val properties = SampleData.properties
        assertTrue("Properties list should not be empty", properties.isNotEmpty())
        
        val first = properties.first()
        assertNotNull(first.id)
        assertNotNull(first.title)
        assertTrue(first.pricePerNight > 0)
        assertTrue(first.bedroomCount > 0)
        assertTrue(first.amenities.isNotEmpty())
    }

    @Test
    fun `verify categories coverage`() {
        val categories = SampleData.categories
        assertTrue("Categories should contain all", categories.any { it.id == "all" })
        assertTrue("Categories should contain cabins", categories.any { it.id == "cabins" })
        assertTrue("Categories should contain beachfront", categories.any { it.id == "beachfront" })
    }

    @Test
    fun `verify allPhotoItems combines preset and custom photos`() {
        val property = SampleData.properties.first()
        val photos = property.allPhotoItems()
        assertTrue(photos.isNotEmpty())
        assertEquals(property.imageResIds.size, photos.size)
    }

    @Test
    fun `verify AuthUser model initials and properties`() {
        val user = com.example.model.AuthUser(
            uid = "firebase-user-12345",
            email = "alexander.wright@mobihome.app",
            displayName = "Alexander Wright",
            provider = "google.com",
            isSuperhost = true
        )
        assertEquals("AW", user.initials)
        assertEquals("firebase-user-12345", user.uid)
        assertTrue(user.isSuperhost)
        assertEquals("google.com", user.provider)
    }

    @Test
    fun `verify AuthState sealed hierarchy`() {
        val unauth: com.example.model.AuthState = com.example.model.AuthState.Unauthenticated
        val authenticating: com.example.model.AuthState = com.example.model.AuthState.Authenticating
        val error: com.example.model.AuthState = com.example.model.AuthState.Error("Invalid credentials")

        assertTrue(unauth is com.example.model.AuthState.Unauthenticated)
        assertTrue(authenticating is com.example.model.AuthState.Authenticating)
        assertTrue(error is com.example.model.AuthState.Error)
        assertEquals("Invalid credentials", (error as com.example.model.AuthState.Error).message)
    }

    @Test
    fun `verify default currency is KES`() {
        val defaultCurrency = com.example.viewmodel.Currency.entries.first()
        assertEquals(com.example.viewmodel.Currency.KES, defaultCurrency)
        assertEquals("KES", defaultCurrency.code)
        assertEquals("KSh", defaultCurrency.symbol)
        assertEquals("Kenyan Shilling", defaultCurrency.displayName)
    }

    @Test
    fun `verify password hashing and verification`() {
        val rawPassword = "secureSecretPassword123"
        val hash1 = com.example.data.firebase.FirebaseAuthService.hashPassword(rawPassword)
        val hash2 = com.example.data.firebase.FirebaseAuthService.hashPassword(rawPassword)
        val wrongHash = com.example.data.firebase.FirebaseAuthService.hashPassword("wrongPassword")

        assertEquals(64, hash1.length) // SHA-256 produces 64 hex characters
        assertEquals(hash1, hash2)
        org.junit.Assert.assertNotEquals(hash1, wrongHash)
    }
}
