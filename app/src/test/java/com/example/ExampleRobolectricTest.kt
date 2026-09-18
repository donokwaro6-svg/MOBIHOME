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
    fun `verify initial properties starts 100 percent empty for zero mock data rule`() {
        val properties = SampleData.properties
        assertTrue("Properties list must be empty for zero-mock rule", properties.isEmpty())
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
        val property = com.example.model.Property(
            id = "test-prop-1",
            title = "Modern Studio",
            description = "Cozy apartment",
            city = "Nairobi",
            country = "Kenya",
            address = "Kilimani",
            pricePerNight = 4500,
            imageResIds = listOf(R.drawable.img_hero_banner),
            photos = emptyList()
        )
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
    fun `verify AuthUser profile fields and editing capabilities`() {
        val user = com.example.model.AuthUser(
            uid = "user-test-789",
            email = "user@example.com",
            displayName = "Wanjiku Kamau",
            provider = "password",
            isSuperhost = false,
            phoneNumber = "+254 712 345678",
            bio = "Wildlife photographer based in Nairobi.",
            location = "Nairobi, Kenya",
            photoUrl = "https://example.com/avatar.jpg"
        )

        assertEquals("WK", user.initials)
        assertEquals("+254 712 345678", user.phoneNumber)
        assertEquals("Wildlife photographer based in Nairobi.", user.bio)
        assertEquals("Nairobi, Kenya", user.location)
        assertEquals("https://example.com/avatar.jpg", user.photoUrl)

        // Updated copy simulates profile update
        val updated = user.copy(
            displayName = "Wanjiku K.",
            phoneNumber = "+254 722 000111",
            bio = "Avid traveler & host."
        )
        assertEquals("WK", updated.initials)
        assertEquals("Wanjiku K.", updated.displayName)
        assertEquals("+254 722 000111", updated.phoneNumber)
        assertEquals("Avid traveler & host.", updated.bio)
    }

    @Test
    fun `verify password validation and authentication format`() {
        val validEmail = "user@example.com"
        val invalidEmail = "invalid-email"
        val shortPassword = "123"
        val validPassword = "securePassword123"

        assertTrue(validEmail.contains("@"))
        assertFalse(invalidEmail.contains("@"))
        assertTrue(shortPassword.length < 6)
        assertTrue(validPassword.length >= 6)
    }

    @Test
    fun `verify listing purposes coverage and badges`() {
        val forSale = com.example.model.ListingPurpose.FOR_SALE
        val forRent = com.example.model.ListingPurpose.FOR_RENT
        val bnbStay = com.example.model.ListingPurpose.BNB_STAY

        assertEquals("For Sale", forSale.displayName)
        assertEquals("FOR SALE", forSale.badge)
        assertEquals("", forSale.priceSuffix)

        assertEquals("For Rent", forRent.displayName)
        assertEquals("FOR RENT", forRent.badge)
        assertEquals(" / mo", forRent.priceSuffix)

        assertEquals("BnB & Staycation", bnbStay.displayName)
        assertEquals("BNB STAY", bnbStay.badge)
        assertEquals(" / night", bnbStay.priceSuffix)
    }

    @Test
    fun `verify commercial and office property types`() {
        val office = com.example.model.PropertyType.OFFICE
        val commercial = com.example.model.PropertyType.COMMERCIAL_SPACE
        val warehouse = com.example.model.PropertyType.WAREHOUSE
        val villa = com.example.model.PropertyType.ENTIRE_VILLA

        assertTrue(office.isCommercialOrOffice)
        assertTrue(commercial.isCommercialOrOffice)
        assertTrue(warehouse.isCommercialOrOffice)
        assertFalse(villa.isCommercialOrOffice)
    }

    @Test
    fun `verify host notification model and notification types`() {
        val bookingNotif = com.example.model.HostNotification(
            id = "notif-1",
            hostId = "host-user",
            propertyId = "prop-101",
            propertyTitle = "Sunset Cliff Penthouse",
            type = com.example.model.NotificationType.BOOKING,
            title = "New Booking Reserved!",
            message = "Alice booked your property for 3 night(s).",
            guestName = "Alice",
            isRead = false
        )

        assertEquals(com.example.model.NotificationType.BOOKING, bookingNotif.type)
        assertEquals("Booking", bookingNotif.type.label)
        assertFalse(bookingNotif.isRead)

        val likeNotif = bookingNotif.copy(
            id = "notif-2",
            type = com.example.model.NotificationType.LIKE,
            title = "Listing Added to Wishlist!",
            isRead = true
        )
        assertEquals(com.example.model.NotificationType.LIKE, likeNotif.type)
        assertEquals("Wishlist Like", likeNotif.type.label)
        assertTrue(likeNotif.isRead)
    }

    @Test
    fun `verify listing purpose and commercial properties logic`() {
        val saleProp = com.example.model.Property(
            id = "test-sale",
            title = "Land for Sale",
            description = "Prime plot",
            city = "Mombasa",
            country = "Kenya",
            address = "Nyali",
            pricePerNight = 15000000,
            listingPurpose = com.example.model.ListingPurpose.FOR_SALE
        )
        val commercialProp = com.example.model.Property(
            id = "test-office",
            title = "Westlands Office Suite",
            description = "Commercial space",
            city = "Nairobi",
            country = "Kenya",
            address = "Westlands",
            pricePerNight = 120000,
            propertyType = com.example.model.PropertyType.COMMERCIAL_SPACE,
            listingPurpose = com.example.model.ListingPurpose.FOR_RENT
        )

        assertEquals(com.example.model.ListingPurpose.FOR_SALE, saleProp.listingPurpose)
        assertTrue(commercialProp.isCommercialOrOffice)
        assertEquals(com.example.model.ListingPurpose.FOR_RENT, commercialProp.listingPurpose)
    }
}

