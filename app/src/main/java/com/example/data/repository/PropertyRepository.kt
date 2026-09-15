package com.example.data.repository

import com.example.data.firebase.FirebasePhotoService
import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.local.CustomListingDao
import com.example.data.local.CustomListingEntity
import com.example.data.local.PropertyPhotoDao
import com.example.data.local.WishlistDao
import com.example.data.local.WishlistEntity
import com.example.model.Amenity
import com.example.model.AmenityIcon
import com.example.model.BookingReservation
import com.example.model.BookingStatus
import com.example.model.Host
import com.example.model.Property
import com.example.model.PropertyPhoto
import com.example.model.PropertyType
import com.example.model.allPhotoItems
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PropertyRepository(
    private val wishlistDao: WishlistDao,
    private val bookingDao: BookingDao,
    private val customListingDao: CustomListingDao,
    val firebasePhotoService: FirebasePhotoService
) {

    val wishlistedIds: Flow<Set<String>> = wishlistDao.getAllWishlist().map { list ->
        list.map { it.propertyId }.toSet()
    }

    val allPhotosFlow: Flow<List<PropertyPhoto>> = firebasePhotoService.allPhotosFlow

    val customListings: Flow<List<Property>> = combine(
        customListingDao.getAllCustomListings(),
        allPhotosFlow
    ) { list, allPhotos ->
        list.map { entity ->
            val attachedPhotos = allPhotos.filter { it.propertyId == entity.id }
            Property(
                id = entity.id,
                title = entity.title,
                tagline = "Hosted by ${entity.hostName} in ${entity.city}",
                description = entity.description,
                propertyType = runCatching { PropertyType.valueOf(entity.propertyType) }.getOrDefault(PropertyType.ENTIRE_VILLA),
                categoryId = "luxury",
                city = entity.city,
                country = entity.country,
                address = entity.address,
                latitude = 40.7128,
                longitude = -74.0060,
                pricePerNight = entity.pricePerNight,
                rating = 5.0,
                reviewCount = 1,
                isSuperhost = true,
                isGuestFavorite = true,
                isRareFind = false,
                imageResIds = listOf(entity.imageResId),
                photos = attachedPhotos,
                bedroomCount = entity.bedrooms,
                bedCount = entity.beds,
                bathroomCount = entity.bathrooms,
                maxGuests = entity.maxGuests,
                squareFeet = 2200,
                amenities = listOf(
                    Amenity("cu1", "Fast WiFi", "Essentials", AmenityIcon.WIFI),
                    Amenity("cu2", "Chef Kitchen", "Essentials", AmenityIcon.KITCHEN),
                    Amenity("cu3", "Air Conditioning", "Comfort", AmenityIcon.AC),
                    Amenity("cu4", "Free Parking", "Features", AmenityIcon.PARKING)
                ),
                host = Host(
                    id = "host-user",
                    name = entity.hostName,
                    isSuperhost = true,
                    rating = 5.0,
                    reviewsCount = 1,
                    responseRate = "100%",
                    responseTime = "within an hour",
                    joinedYear = 2026,
                    bio = entity.hostBio
                ),
                cleaningFee = 50
            )
        }
    }

    val allProperties: Flow<List<Property>> = combine(
        customListings,
        allPhotosFlow
    ) { customProps, allPhotos ->
        val samplePropsWithPhotos = SampleData.properties.map { sampleProp ->
            val additionalPhotos = allPhotos.filter { it.propertyId == sampleProp.id }
            if (additionalPhotos.isNotEmpty()) {
                sampleProp.copy(photos = additionalPhotos + sampleProp.allPhotoItems())
            } else {
                sampleProp
            }
        }
        customProps + samplePropsWithPhotos
    }

    fun isWishlisted(propertyId: String): Flow<Boolean> = wishlistDao.isWishlisted(propertyId)

    suspend fun toggleWishlist(propertyId: String, currentWishlisted: Boolean) {
        if (currentWishlisted) {
            wishlistDao.deleteWishlist(propertyId)
        } else {
            wishlistDao.insertWishlist(WishlistEntity(propertyId = propertyId))
        }
    }

    val allBookings: Flow<List<BookingReservation>> = bookingDao.getAllBookings().map { list ->
        list.map { entity ->
            BookingReservation(
                id = entity.id,
                propertyId = entity.propertyId,
                propertyTitle = entity.propertyTitle,
                propertyLocation = entity.propertyLocation,
                propertyType = entity.propertyType,
                imageResId = entity.imageResId,
                checkInDate = entity.checkInDate,
                checkOutDate = entity.checkOutDate,
                nightsCount = entity.nightsCount,
                guestsCount = entity.guestsCount,
                pricePerNight = entity.pricePerNight,
                totalAmount = entity.totalAmount,
                bookingReference = entity.bookingReference,
                status = runCatching { BookingStatus.valueOf(entity.status) }.getOrDefault(BookingStatus.CONFIRMED),
                guestName = entity.guestName,
                specialRequests = entity.specialRequests,
                createdTimestamp = entity.createdTimestamp
            )
        }
    }

    suspend fun createBooking(booking: BookingReservation) {
        bookingDao.insertBooking(
            BookingEntity(
                id = booking.id,
                propertyId = booking.propertyId,
                propertyTitle = booking.propertyTitle,
                propertyLocation = booking.propertyLocation,
                propertyType = booking.propertyType,
                imageResId = booking.imageResId,
                checkInDate = booking.checkInDate,
                checkOutDate = booking.checkOutDate,
                nightsCount = booking.nightsCount,
                guestsCount = booking.guestsCount,
                pricePerNight = booking.pricePerNight,
                totalAmount = booking.totalAmount,
                bookingReference = booking.bookingReference,
                status = booking.status.name,
                guestName = booking.guestName,
                specialRequests = booking.specialRequests,
                createdTimestamp = booking.createdTimestamp
            )
        )
    }

    suspend fun cancelBooking(bookingId: String) {
        bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED.name)
    }

    suspend fun addCustomListing(
        title: String,
        description: String,
        propertyType: String,
        city: String,
        country: String,
        address: String,
        pricePerNight: Int,
        bedrooms: Int,
        beds: Int,
        bathrooms: Int,
        maxGuests: Int,
        hostName: String,
        hostBio: String,
        imageResId: Int,
        initialPhotoUris: List<Uri> = emptyList()
    ): String {
        val id = "custom-${System.currentTimeMillis()}"
        customListingDao.insertCustomListing(
            CustomListingEntity(
                id = id,
                title = title,
                description = description,
                propertyType = propertyType,
                city = city,
                country = country,
                address = address,
                pricePerNight = pricePerNight,
                bedrooms = bedrooms,
                beds = beds,
                bathrooms = bathrooms,
                maxGuests = maxGuests,
                hostName = hostName,
                hostBio = hostBio,
                imageResId = imageResId
            )
        )

        // Upload any initial photos to Firebase
        for (uri in initialPhotoUris) {
            runCatching {
                firebasePhotoService.uploadPhotoFromUri(
                    propertyId = id,
                    uri = uri,
                    caption = "Host Photo - $title"
                )
            }
        }
        return id
    }

    suspend fun deleteCustomListing(id: String) {
        customListingDao.deleteListing(id)
    }

    suspend fun updateCustomListing(
        id: String,
        title: String,
        description: String,
        pricePerNight: Int,
        city: String,
        country: String,
        address: String
    ) {
        customListingDao.updateCustomListing(
            id = id,
            title = title,
            description = description,
            pricePerNight = pricePerNight,
            city = city,
            country = country,
            address = address
        )
    }

    suspend fun uploadPhotoForProperty(
        propertyId: String,
        uri: Uri,
        caption: String,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): PropertyPhoto {
        return firebasePhotoService.uploadPhotoFromUri(propertyId, uri, caption, onProgress)
    }

    suspend fun addPresetPhotoForProperty(
        propertyId: String,
        resId: Int,
        caption: String,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): PropertyPhoto {
        return firebasePhotoService.addPhotoFromPreset(propertyId, resId, caption, onProgress)
    }

    suspend fun addUrlPhotoForProperty(
        propertyId: String,
        url: String,
        caption: String,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): PropertyPhoto {
        return firebasePhotoService.addPhotoFromUrl(propertyId, url, caption, onProgress)
    }

    suspend fun updatePhotoCaption(photoId: String, newCaption: String) {
        firebasePhotoService.updatePhotoCaption(photoId, newCaption)
    }

    suspend fun deletePropertyPhoto(photoId: String, propertyId: String, storagePath: String) {
        firebasePhotoService.deletePhoto(photoId, propertyId, storagePath)
    }
}

