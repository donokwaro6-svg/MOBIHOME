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

data class TenantPropertyRequest(
    val id: String,
    val tenantId: String,
    val tenantName: String,
    val tenantEmail: String,
    val tenantPhone: String = "",
    val title: String,
    val purpose: String = "FOR_RENT",
    val city: String,
    val preferredNeighborhood: String = "",
    val maxBudget: Double = 0.0,
    val currency: String = "KES",
    val bedrooms: Int = 1,
    val bathrooms: Int = 1,
    val desiredMoveInDate: String = "Immediate / Flexible",
    val leaseDuration: String = "12 Months",
    val requiredAmenities: List<String> = emptyList(),
    val specialRequirements: String = "",
    val status: String = "OPEN",
    val responsesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val neighborhood: String get() = preferredNeighborhood
    val moveInDate: String get() = desiredMoveInDate
    val notes: String get() = specialRequirements
    val contactPhone: String get() = tenantPhone
}

data class PropertyOptionResponse(
    val id: String,
    val requestId: String,
    val adminId: String,
    val adminName: String,
    val adminEmail: String = "",
    val adminPhone: String = "",
    val adminIsVerified: Boolean = false,
    val propertyId: String? = null,
    val propertyTitle: String,
    val offeredPrice: Double = 0.0,
    val currency: String = "KES",
    val city: String = "",
    val address: String = "",
    val imageUrl: String? = null,
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val propertyLocation: String get() = if (city.isNotBlank() && address.isNotBlank()) "$city · $address" else city.ifBlank { address }
    val propertyImage: String? get() = imageUrl
    val isAdminVerified: Boolean get() = adminIsVerified
}

data class PropertyAdmin(
    val id: String,
    val name: String,
    val agency: String = "MobiHome Accredited Estates",
    val title: String = "Senior Property Administrator",
    val email: String,
    val phone: String = "+254 712 345 678",
    val avatarUrl: String = "",
    val location: String = "Nairobi, Kenya",
    val bio: String = "Licensed property administrator managing premier residential suites, executive rentals, and modern furnished apartments.",
    val memberSince: String = "2023",
    val isVerified: Boolean = true,
    val propertiesCount: Int = 78,
    val totalTenants: Int = 184,
    val occupancyRate: Double = 89.5,
    val rating: Double = 4.94,
    val reviewsCount: Int = 52,
    val responseTime: String = "within an hour",
    val specialties: List<String> = listOf("Executive Rentals", "Furnished Stays", "Commercial Leasing"),
    val reviews: List<Review> = emptyList(),
    val properties: List<Property> = emptyList()
) {
    val meetsOrganicCriteria: Boolean
        get() = propertiesCount > 70 && totalTenants > 150 && occupancyRate > 60.0
}


