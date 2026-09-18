package com.example.model

data class PropertyCategory(
    val id: String,
    val title: String,
    val iconName: String
)

data class Review(
    val id: String,
    val authorName: String,
    val authorLocation: String,
    val rating: Double,
    val date: String,
    val comment: String,
    val cleanliness: Double = 4.9,
    val accuracy: Double = 5.0,
    val communication: Double = 4.9,
    val location: Double = 4.8,
    val checkIn: Double = 5.0,
    val value: Double = 4.8
)

data class BookingReservation(
    val id: String,
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
    val status: BookingStatus,
    val guestName: String,
    val specialRequests: String = "",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val listingPurpose: String = "BNB_STAY"
)

enum class BookingStatus(val label: String) {
    CONFIRMED("Confirmed"),
    ACTIVE("Checked In"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class HostNotification(
    val id: String,
    val hostId: String,
    val propertyId: String,
    val propertyTitle: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val guestName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType(val label: String) {
    BOOKING("Booking"),
    LIKE("Wishlist Like")
}

data class SearchFilterState(
    val query: String = "",
    val selectedCategory: String = "all",
    val selectedPurpose: ListingPurpose? = null,
    val minPrice: Int = 0,
    val maxPrice: Int = 1500,
    val selectedPropertyTypes: Set<PropertyType> = emptySet(),
    val minBedrooms: Int = 0,
    val minBeds: Int = 0,
    val minBathrooms: Int = 0,
    val selectedAmenities: Set<AmenityIcon> = emptySet(),
    val superhostOnly: Boolean = false,
    val guestFavoriteOnly: Boolean = false
)
