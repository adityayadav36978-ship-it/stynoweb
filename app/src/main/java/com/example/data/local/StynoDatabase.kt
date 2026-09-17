package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedPropertyEntity::class,
        LocalWishlistCollectionEntity::class,
        LocalBookingEntity::class,
        LocalChatMessageEntity::class,
        CustomPropertyEntity::class,
        LocalComplaintEntity::class,
        CachedPropertyEntity::class,
        CachedSearchResultEntity::class,
        PropertyDraftEntity::class
    ],
    version = 9,
    exportSchema = false
)

abstract class StynoDatabase : RoomDatabase() {
    abstract fun stynoDao(): StynoDao

    companion object {
        @Volatile
        private var INSTANCE: StynoDatabase? = null

        fun getDatabase(context: Context): StynoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StynoDatabase::class.java,
                    "styno_stays_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
