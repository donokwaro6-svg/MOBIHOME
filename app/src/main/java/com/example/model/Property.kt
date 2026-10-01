package com.example.model

enum class ListingPurpose(val displayName: String, val badge: String, val priceSuffix: String) {
    FOR_SALE("For Sale", "FOR SALE", ""),
    FOR_RENT("For Rent", "FOR RENT", " / mo"),
    BNB_STAY("BnB & Staycation", "BNB STAY", " / night")
}

data class Property(
    val id: String = "",
    val title: String = "",
    val tagline: String = "",
    val description: String = "",
    val propertyType: PropertyType = PropertyType.APARTMENT,
    val listingPurpose: ListingPurpose = ListingPurpose.BNB_STAY,
    val categoryId: String = "all",
    val city: String = "",
    val country: String = "",
    val address: String = "",
    val latitude: Double = -1.2921,
    val longitude: Double = 36.8219,
    val pricePerNight: Int = 0,
    val currency: String = "KES",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isSuperhost: Boolean = false,
    val isGuestFavorite: Boolean = true,
    val isRareFind: Boolean = false,
    val imageUrl: String = "",
    val imageResIds: List<Int> = emptyList(),
    val photos: List<PropertyPhoto> = emptyList(),
    val bedroomCount: Int = 1,
    val bedCount: Int = 1,
    val bathroomCount: Int = 1,
    val maxGuests: Int = 2,
    val squareFeet: Int = 500,
    val amenities: List<Amenity> = emptyList(),
    val host: Host = Host(),
    val sleepingArrangements: List<SleepingArrangement> = emptyList(),
    val cleaningFee: Int = 45,
    val serviceFeeRate: Double = 0.12,
    val taxesRate: Double = 0.08,
    val reviews: List<Review> = emptyList(),
    val parkingSpaces: Int = 2,
    val zoningType: String = "Residential"
) {
    val isForSale: Boolean get() = listingPurpose == ListingPurpose.FOR_SALE
    val isForRent: Boolean get() = listingPurpose == ListingPurpose.FOR_RENT
    val isBnBStay: Boolean get() = listingPurpose == ListingPurpose.BNB_STAY
    val isCommercialOrOffice: Boolean get() = propertyType.isCommercialOrOffice
    val isOfficeOrCommercial: Boolean get() = propertyType.isCommercialOrOffice
    val priceSuffix: String get() = listingPurpose.priceSuffix
    val primaryPhoto: PropertyPhoto? get() = allPhotoItems().firstOrNull()

    fun priceUnitLabel(): String = when (listingPurpose) {
        ListingPurpose.FOR_SALE -> " total"
        ListingPurpose.FOR_RENT -> " / mo"
        ListingPurpose.BNB_STAY -> " / night"
    }

    fun actionLabel(): String = when (listingPurpose) {
        ListingPurpose.FOR_SALE -> "Reserve Purchase"
        ListingPurpose.FOR_RENT -> "Reserve Lease"
        ListingPurpose.BNB_STAY -> "Reserve Dates"
    }
}

enum class PropertyType(val displayName: String, val isCommercialOrOffice: Boolean = false) {
    ENTIRE_VILLA("Entire villa"),
    CABIN("Entire cabin"),
    PENTHOUSE("Luxury penthouse"),
    BEACHFRONT("Beachfront home"),
    MODERN_LOFT("Modern loft"),
    CHALET("Mountain chalet"),
    TREEHOUSE("Treehouse retreat"),
    COUNTRYSIDE("Countryside estate"),
    HOUSE("Single-Family House"),
    APARTMENT("Apartment & Flat"),
    BNB("Bed & Breakfast / BnB"),
    OFFICE("Executive Office Suite", isCommercialOrOffice = true),
    COMMERCIAL_SPACE("Commercial Retail & Showroom", isCommercialOrOffice = true),
    WAREHOUSE("Warehouse & Logistics Space", isCommercialOrOffice = true)
}

data class SleepingArrangement(
    val roomName: String,
    val bedType: String,
    val iconName: String
)

data class Host(
    val id: String = "",
    val name: String = "",
    val avatarResId: Int? = null,
    val isSuperhost: Boolean = false,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val responseRate: String = "100%",
    val responseTime: String = "within an hour",
    val joinedYear: Int = 2024,
    val bio: String = ""
)

data class Amenity(
    val id: String,
    val name: String,
    val category: String,
    val iconType: AmenityIcon
)

enum class AmenityIcon {
    WIFI,
    POOL,
    HOT_TUB,
    KITCHEN,
    WORKSPACE,
    PARKING,
    EV_CHARGER,
    BEACH_ACCESS,
    AC,
    FIREPLACE,
    MOUNTAIN_VIEW,
    WASHER,
    GYM,
    PET_FRIENDLY,
    BALCONY,
    BBQ,
    SECURITY,
    BREAKFAST,
    ACCESSIBILITY
}

data class PropertyPhoto(
    val id: String,
    val propertyId: String,
    val urlOrUri: String? = null,
    val resId: Int? = null,
    val caption: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val storagePath: String = "properties/$propertyId/photos/$id.jpg",
    val isSyncedToFirebase: Boolean = true,
    val fileSizeKb: Int = 0
)

fun Property.allPhotoItems(): List<PropertyPhoto> {
    val items = mutableListOf<PropertyPhoto>()
    if (imageUrl.isNotBlank()) {
        items.add(
            PropertyPhoto(
                id = "$id-primary",
                propertyId = id,
                urlOrUri = imageUrl,
                caption = title,
                storagePath = "properties/$id/photos/primary.jpg",
                isSyncedToFirebase = true
            )
        )
    }
    // Add custom/Firebase uploaded photos
    photos.forEach { p ->
        if (p.urlOrUri != imageUrl) {
            items.add(p)
        }
    }
    // Add resource ID photos if any
    imageResIds.forEachIndexed { index, resId ->
        if (items.none { it.resId == resId }) {
            items.add(
                PropertyPhoto(
                    id = "$id-res-$index",
                    propertyId = id,
                    resId = resId,
                    caption = "View ${index + 1}",
                    storagePath = "properties/$id/photos/res_$index.jpg",
                    isSyncedToFirebase = true,
                    fileSizeKb = 340 + index * 45
                )
            )
        }
    }
    return items
}

val Property.primaryPhoto: PropertyPhoto?
    get() = allPhotoItems().firstOrNull()

