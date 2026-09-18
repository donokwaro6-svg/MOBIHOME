package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY savedTimestamp DESC")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE propertyId = :propertyId)")
    fun isWishlisted(propertyId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(entity: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE propertyId = :propertyId")
    suspend fun deleteWishlist(propertyId: String)

    @Query("DELETE FROM wishlist")
    suspend fun clearWishlist()
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdTimestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :newStatus WHERE id = :id")
    suspend fun updateBookingStatus(id: String, newStatus: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBooking(id: String)
}

@Dao
interface PropertyPhotoDao {
    @Query("SELECT * FROM property_photos ORDER BY uploadedAt ASC")
    fun getAllPhotos(): Flow<List<PropertyPhotoEntity>>

    @Query("SELECT * FROM property_photos WHERE propertyId = :propertyId ORDER BY uploadedAt ASC")
    fun getPhotosByPropertyId(propertyId: String): Flow<List<PropertyPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PropertyPhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PropertyPhotoEntity>)

    @Query("UPDATE property_photos SET caption = :caption WHERE id = :id")
    suspend fun updatePhotoCaption(id: String, caption: String)

    @Query("DELETE FROM property_photos WHERE id = :id")
    suspend fun deletePhoto(id: String)

    @Query("DELETE FROM property_photos WHERE propertyId = :propertyId")
    suspend fun deletePhotosForProperty(propertyId: String)
}

@Dao
interface HostNotificationDao {
    @Query("SELECT * FROM host_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<HostNotificationEntity>>

    @Query("SELECT * FROM host_notifications WHERE hostId = :hostId ORDER BY timestamp DESC")
    fun getNotificationsForHost(hostId: String): Flow<List<HostNotificationEntity>>

    @Query("SELECT COUNT(*) FROM host_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: HostNotificationEntity)

    @Query("UPDATE host_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE host_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM host_notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    @Query("DELETE FROM host_notifications")
    suspend fun clearAll()
}

