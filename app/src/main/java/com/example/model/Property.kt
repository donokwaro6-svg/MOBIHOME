package com.example.model

data class Property(
    val id: String,
    val title: String,
    val tagline: String,
    val description: String,
    val propertyType: PropertyType,
    val categoryId: String,
    val city: String,
    val country: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerNight: Int,
    val rating: Double,
    val reviewCount: Int,
    val isSuperhost: Boolean,
    val isGuestFavorite: Boolean = true,
    val isRareFind: Boolean = false,
    val imageResIds: List<Int> = emptyList(),
    val photos: List<PropertyPhoto> = emptyList(),
    val bedroomCount: Int,
    val bedCount: Int,
    val bathroomCount: Int,
    val maxGuests: Int,
    val squareFeet: Int,
    val amenities: List<Amenity>,
    val host: Host,
    val sleepingArrangements: List<SleepingArrangement> = emptyList(),
    val cleaningFee: Int = 45,
    val serviceFeeRate: Double = 0.12,
    val taxesRate: Double = 0.08,
    val reviews: List<Review> = emptyList()
)

enum class PropertyType(val displayName: String) {
    ENTIRE_VILLA("Entire villa"),
    CABIN("Entire cabin"),
    PENTHOUSE("Luxury penthouse"),
    BEACHFRONT("Beachfront home"),
    MODERN_LOFT("Modern loft"),
    CHALET("Mountain chalet"),
    TREEHOUSE("Treehouse retreat"),
    COUNTRYSIDE("Countryside estate")
}

data class SleepingArrangement(
    val roomName: String,
    val bedType: String,
    val iconName: String
)

data class Host(
    val id: String,
    val name: String,
    val avatarResId: Int? = null,
    val isSuperhost: Boolean,
    val rating: Double,
    val reviewsCount: Int,
    val responseRate: String,
    val responseTime: String,
    val joinedYear: Int,
    val bio: String
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
    BBQ
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
    // Add custom/Firebase uploaded photos first
    items.addAll(photos)
    // Add resource ID photos
    imageResIds.forEachIndexed { index, resId ->
        // Avoid duplicates if resId is already included
        if (photos.none { it.resId == resId }) {
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
    if (items.isEmpty()) {
        items.add(
            PropertyPhoto(
                id = "$id-default",
                propertyId = id,
                resId = com.example.R.drawable.img_hero_banner,
                caption = "Featured View",
                storagePath = "properties/$id/photos/featured.jpg",
                isSyncedToFirebase = true,
                fileSizeKb = 420
            )
        )
    }
    return items
}

