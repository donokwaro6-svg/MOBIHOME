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

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tenant_property_requests")
data class TenantPropertyRequestEntity(
    @PrimaryKey val id: String,
    val tenantId: String,
    val tenantName: String,
    val tenantEmail: String,
    val tenantPhone: String = "",
    val title: String,
    val purpose: String = "FOR_RENT", // BNB_STAY, FOR_RENT, FOR_SALE
    val city: String,
    val neighborhood: String = "",
    val maxBudget: Double,
    val currency: String = "KES",
    val bedrooms: Int = 1,
    val bathrooms: Int = 1,
    val moveInDate: String = "Immediate / Flexible",
    val leaseDuration: String = "12 Months",
    val requiredAmenities: String = "", // Comma-separated amenity strings
    val notes: String = "",
    val status: String = "OPEN", // "OPEN", "RESPONDED", "CLOSED"
    val responsesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tenant_request_responses")
data class TenantRequestResponseEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val adminId: String,
    val adminName: String,
    val adminEmail: String = "",
    val adminPhone: String = "",
    val isAdminVerified: Boolean = false,
    val propertyId: String? = null,
    val propertyTitle: String,
    val offeredPrice: Double,
    val currency: String = "KES",
    val propertyLocation: String = "",
    val propertyImage: String? = null,
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)


