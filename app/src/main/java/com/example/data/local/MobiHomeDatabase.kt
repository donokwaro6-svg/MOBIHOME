package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WishlistEntity::class,
        BookingEntity::class,
        PropertyPhotoEntity::class,
        HostNotificationEntity::class,
        RecentSearchEntity::class,
        TenantPropertyRequestEntity::class,
        TenantRequestResponseEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class MobiHomeDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun bookingDao(): BookingDao
    abstract fun propertyPhotoDao(): PropertyPhotoDao
    abstract fun hostNotificationDao(): HostNotificationDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun tenantPropertyRequestDao(): TenantPropertyRequestDao

    companion object {
        @Volatile
        private var INSTANCE: MobiHomeDatabase? = null

        fun getInstance(context: Context): MobiHomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MobiHomeDatabase::class.java,
                    "mobihome_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
