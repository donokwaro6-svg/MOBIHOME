package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WishlistEntity::class,
        BookingEntity::class,
        CustomListingEntity::class,
        PropertyPhotoEntity::class,
        UserAccountEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MobiHomeDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun bookingDao(): BookingDao
    abstract fun customListingDao(): CustomListingDao
    abstract fun propertyPhotoDao(): PropertyPhotoDao
    abstract fun userAccountDao(): UserAccountDao

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
