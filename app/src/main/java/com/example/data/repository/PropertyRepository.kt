package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.firebase.FirebasePhotoService
import com.example.data.firebase.FirestorePropertyService
import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.local.HostNotificationDao
import com.example.data.local.HostNotificationEntity
import com.example.data.local.WishlistDao
import com.example.data.local.WishlistEntity
import com.example.model.BookingReservation
import com.example.model.BookingStatus
import com.example.model.HostNotification
import com.example.model.ListingPurpose
import com.example.model.NotificationType
import com.example.model.Property
import com.example.model.PropertyPhoto
import com.example.model.PropertyType
import com.example.model.allPhotoItems
import com.example.util.HostNotificationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

class PropertyRepository(
    private val context: Context,
    private val wishlistDao: WishlistDao,
    private val bookingDao: BookingDao,
    private val hostNotificationDao: HostNotificationDao,
    val firebasePhotoService: FirebasePhotoService,
    val firestorePropertyService: FirestorePropertyService
) {

    val wishlistedIds: Flow<Set<String>> = wishlistDao.getAllWishlist().map { list ->
        list.map { it.propertyId }.toSet()
    }

    val allPhotosFlow: Flow<List<PropertyPhoto>> = firebasePhotoService.allPhotosFlow

    // Real-time flow of all Firestore properties
    val firestorePropertiesFlow: Flow<List<Property>> = firestorePropertyService.fetchAllPropertiesFlow()

    // Requirement 2: Fetch user listings with query where("userId", "==", currentUser.uid)
    fun getUserListingsFlow(userId: String): Flow<List<Property>> {
        return firestorePropertyService.fetchUserListingsFlow(userId)
    }

    suspend fun fetchUserListingsOnce(userId: String): List<Property> {
        return firestorePropertyService.fetchUserListingsOnce(userId)
    }

    // All properties for Explore: Combines Firestore listings with local uploaded photos
    val allProperties: Flow<List<Property>> = combine(
        firestorePropertiesFlow,
        allPhotosFlow
    ) { firestoreProps, allPhotos ->
        firestoreProps.map { prop ->
            val additionalPhotos = allPhotos.filter { it.propertyId == prop.id }
            if (additionalPhotos.isNotEmpty()) {
                prop.copy(photos = additionalPhotos + prop.allPhotoItems())
            } else {
                prop
            }
        }
    }

    val allHostNotifications: Flow<List<HostNotification>> = hostNotificationDao.getAllNotifications().map { list ->
        list.map { entity ->
            HostNotification(
                id = entity.id,
                hostId = entity.hostId,
                propertyId = entity.propertyId,
                propertyTitle = entity.propertyTitle,
                type = runCatching { NotificationType.valueOf(entity.type) }.getOrDefault(NotificationType.BOOKING),
                title = entity.title,
                message = entity.message,
                guestName = entity.guestName,
                timestamp = entity.timestamp,
                isRead = entity.isRead
            )
        }
    }

    val unreadHostNotificationCount: Flow<Int> = hostNotificationDao.getUnreadCount()

    fun isWishlisted(propertyId: String): Flow<Boolean> = wishlistDao.isWishlisted(propertyId)

    suspend fun toggleWishlist(propertyId: String, currentWishlisted: Boolean) {
        if (currentWishlisted) {
            wishlistDao.deleteWishlist(propertyId)
        } else {
            wishlistDao.insertWishlist(WishlistEntity(propertyId = propertyId))

            val prop = firestorePropertyService.getPropertyById(propertyId)
            val propTitle = prop?.title ?: "Your listing"
            val hostId = prop?.host?.id ?: ""
            val likeTitle = "❤️ Listing Saved to Wishlist!"
            val likeMsg = "A prospective guest just saved '$propTitle' to their favorites list."
            val notifId = UUID.randomUUID().toString()

            hostNotificationDao.insertNotification(
                HostNotificationEntity(
                    id = notifId,
                    hostId = hostId,
                    propertyId = propertyId,
                    propertyTitle = propTitle,
                    type = NotificationType.LIKE.name,
                    title = likeTitle,
                    message = likeMsg,
                    guestName = "A guest",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
            )

            HostNotificationManager.sendHostAlert(
                context = context,
                notificationId = notifId.hashCode(),
                title = likeTitle,
                message = likeMsg
            )
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
                createdTimestamp = entity.createdTimestamp,
                listingPurpose = entity.listingPurpose
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
                createdTimestamp = booking.createdTimestamp,
                listingPurpose = booking.listingPurpose
            )
        )

        val matchedProperty = firestorePropertyService.getPropertyById(booking.propertyId)
        val hostId = matchedProperty?.host?.id ?: ""
        val notificationTitle = when (booking.listingPurpose) {
            "FOR_SALE" -> "🎉 Purchase Reservation Confirmed!"
            "FOR_RENT" -> "🎉 Lease Application Reserved!"
            else -> "🎉 New Booking Confirmed!"
        }
        val notificationMsg = "${booking.guestName} booked ${booking.propertyTitle} (${booking.checkInDate} - ${booking.checkOutDate}). Total: $${booking.totalAmount}. Ref: ${booking.bookingReference}."

        val notifId = UUID.randomUUID().toString()
        hostNotificationDao.insertNotification(
            HostNotificationEntity(
                id = notifId,
                hostId = hostId,
                propertyId = booking.propertyId,
                propertyTitle = booking.propertyTitle,
                type = NotificationType.BOOKING.name,
                title = notificationTitle,
                message = notificationMsg,
                guestName = booking.guestName,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )

        HostNotificationManager.sendHostAlert(
            context = context,
            notificationId = notifId.hashCode(),
            title = notificationTitle,
            message = notificationMsg
        )
    }

    suspend fun cancelBooking(bookingId: String) {
        bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED.name)
    }

    suspend fun markNotificationAsRead(id: String) {
        hostNotificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        hostNotificationDao.markAllAsRead()
    }

    suspend fun deleteNotification(id: String) {
        hostNotificationDao.deleteNotification(id)
    }

    suspend fun clearAllNotifications() {
        hostNotificationDao.clearAll()
    }

    /**
     * Requirement 2: Save all new listings to firestore, not localStorage.
     * Document has a userId field equal to the owner's auth uid.
     * Requirement 3: Upload property images to Firebase Storage and save document URL in Firestore.
     */
    suspend fun createFirestoreListing(
        userId: String,
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
        listingPurpose: String = "BNB_STAY",
        imageUris: List<Uri> = emptyList(),
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): String {
        return firestorePropertyService.createPropertyListing(
            userId = userId,
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
            listingPurpose = listingPurpose,
            imageUris = imageUris,
            onProgress = onProgress
        )
    }

    suspend fun updateFirestoreListing(
        propertyId: String,
        title: String,
        description: String,
        pricePerNight: Int,
        city: String,
        country: String,
        address: String
    ) {
        firestorePropertyService.updateListing(
            propertyId = propertyId,
            title = title,
            description = description,
            pricePerNight = pricePerNight,
            city = city,
            country = country,
            address = address
        )
    }

    suspend fun deleteFirestoreListing(propertyId: String) {
        firestorePropertyService.deleteListing(propertyId)
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
