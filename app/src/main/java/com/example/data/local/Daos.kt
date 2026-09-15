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
interface CustomListingDao {
    @Query("SELECT * FROM custom_listings ORDER BY createdTimestamp DESC")
    fun getAllCustomListings(): Flow<List<CustomListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomListing(listing: CustomListingEntity)

    @Query("UPDATE custom_listings SET title = :title, description = :description, pricePerNight = :pricePerNight, city = :city, country = :country, address = :address WHERE id = :id")
    suspend fun updateCustomListing(id: String, title: String, description: String, pricePerNight: Int, city: String, country: String, address: String)

    @Query("UPDATE custom_listings SET isActive = :isActive WHERE id = :id")
    suspend fun updateListingStatus(id: String, isActive: Boolean)

    @Query("DELETE FROM custom_listings WHERE id = :id")
    suspend fun deleteListing(id: String)
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
interface UserAccountDao {
    @Query("SELECT * FROM registered_users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM registered_users WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Query("SELECT COUNT(*) FROM registered_users")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM registered_users ORDER BY createdTimestamp DESC")
    suspend fun getAllUsers(): List<UserAccountEntity>
}

