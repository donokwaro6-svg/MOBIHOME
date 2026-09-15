package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val propertyId: String,
    val collectionName: String = "Favorites",
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val propertyLocation: String,
    val propertyType: String,
    val imageResId: Int,
    val checkInDate: String,
    val checkOutDate: String,
    val nightsCount: Int,
    val guestsCount: Int,
    val pricePerNight: Int,
    val totalAmount: Int,
    val bookingReference: String,
    val status: String,
    val guestName: String,
    val specialRequests: String,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_listings")
data class CustomListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val propertyType: String,
    val city: String,
    val country: String,
    val address: String,
    val pricePerNight: Int,
    val bedrooms: Int,
    val beds: Int,
    val bathrooms: Int,
    val maxGuests: Int,
    val hostName: String,
    val hostBio: String,
    val imageResId: Int,
    val isActive: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "property_photos")
data class PropertyPhotoEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val urlOrUri: String?,
    val resId: Int = 0,
    val caption: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val storagePath: String = "",
    val isSyncedToFirebase: Boolean = true,
    val fileSizeKb: Int = 0
)

@Entity(tableName = "registered_users")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val displayName: String,
    val uid: String,
    val isSuperhost: Boolean = false,
    val provider: String = "password",
    val createdTimestamp: Long = System.currentTimeMillis()
)

