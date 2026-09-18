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
    val createdTimestamp: Long = System.currentTimeMillis(),
    val listingPurpose: String = "BNB_STAY"
)

@Entity(tableName = "host_notifications")
data class HostNotificationEntity(
    @PrimaryKey val id: String,
    val hostId: String,
    val propertyId: String,
    val propertyTitle: String,
    val type: String,
    val title: String,
    val message: String,
    val guestName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
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


