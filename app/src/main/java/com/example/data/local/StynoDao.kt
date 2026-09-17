package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StynoDao {
    // Saved Properties / Wishlist
    @Query("SELECT * FROM saved_properties ORDER BY savedAt DESC")
    fun getAllSavedProperties(): Flow<List<SavedPropertyEntity>>

    @Query("SELECT * FROM saved_properties WHERE propertyId = :propertyId LIMIT 1")
    suspend fun getSavedProperty(propertyId: String): SavedPropertyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProperty(saved: SavedPropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProperties(list: List<SavedPropertyEntity>)

    @Query("DELETE FROM saved_properties WHERE propertyId = :propertyId")
    suspend fun removeSavedProperty(propertyId: String)

    @Query("DELETE FROM saved_properties")
    suspend fun clearAllSavedProperties()

    @Query("UPDATE saved_properties SET collectionName = :collectionName, userNotes = :notes, priorityTag = :priorityTag, priceAlertEnabled = :priceAlert WHERE propertyId = :propertyId")
    suspend fun updateSavedPropertyDetails(
        propertyId: String,
        collectionName: String,
        notes: String,
        priorityTag: String,
        priceAlert: Boolean
    )

    @Query("UPDATE saved_properties SET firestoreSynced = :synced, firestoreDocId = :docId WHERE propertyId = :propertyId")
    suspend fun updateSavedPropertySyncStatus(propertyId: String, synced: Boolean, docId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_properties WHERE propertyId = :propertyId)")
    fun isPropertySaved(propertyId: String): Flow<Boolean>

    // Wishlist Collections
    @Query("SELECT * FROM wishlist_collections ORDER BY createdAt ASC")
    fun getAllWishlistCollections(): Flow<List<LocalWishlistCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistCollection(collection: LocalWishlistCollectionEntity)

    @Query("DELETE FROM wishlist_collections WHERE id = :collectionId")
    suspend fun deleteWishlistCollection(collectionId: String)

    // Bookings
    @Query("SELECT * FROM bookings ORDER BY bookedAtTimestamp DESC")
    fun getAllBookings(): Flow<List<LocalBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: LocalBookingEntity)

    @Query("UPDATE bookings SET status = :newStatus WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, newStatus: String)

    // Chat
    @Query("SELECT * FROM chat_messages WHERE propertyId = :propertyId ORDER BY timestamp ASC")
    fun getChatMessages(propertyId: String): Flow<List<LocalChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: LocalChatMessageEntity)

    // Custom Owner Properties
    @Query("SELECT * FROM custom_properties ORDER BY createdAt DESC")
    fun getCustomProperties(): Flow<List<CustomPropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomProperty(property: CustomPropertyEntity)

    @Query("UPDATE custom_properties SET verificationStatus = :status WHERE id = :propertyId")
    suspend fun updatePropertyVerification(propertyId: String, status: String)

    @Query("DELETE FROM custom_properties WHERE id = :propertyId")
    suspend fun deleteCustomProperty(propertyId: String)

    // Property Listing Drafts
    @Query("SELECT * FROM property_drafts ORDER BY updatedAt DESC")
    fun getAllPropertyDrafts(): Flow<List<PropertyDraftEntity>>

    @Query("SELECT * FROM property_drafts WHERE id = :draftId LIMIT 1")
    suspend fun getPropertyDraftById(draftId: String): PropertyDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPropertyDraft(draft: PropertyDraftEntity)

    @Query("DELETE FROM property_drafts WHERE id = :draftId")
    suspend fun deletePropertyDraft(draftId: String)

    // Complaints
    @Query("SELECT * FROM complaints ORDER BY createdTimestamp DESC")
    fun getAllComplaints(): Flow<List<LocalComplaintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: LocalComplaintEntity)

    @Query("UPDATE complaints SET status = :status, resolutionNotes = :notes WHERE id = :complaintId")
    suspend fun updateComplaintStatus(complaintId: String, status: String, notes: String)

    // Cached Properties (Offline & Frequently Accessed Cache)
    @Query("SELECT * FROM cached_properties ORDER BY lastAccessedTimestamp DESC")
    fun getAllCachedProperties(): Flow<List<CachedPropertyEntity>>

    @Query("SELECT * FROM cached_properties ORDER BY lastAccessedTimestamp DESC")
    suspend fun getAllCachedPropertiesList(): List<CachedPropertyEntity>

    @Query("SELECT * FROM cached_properties WHERE id = :propertyId LIMIT 1")
    fun getCachedPropertyFlow(propertyId: String): Flow<CachedPropertyEntity?>

    @Query("SELECT * FROM cached_properties WHERE id = :propertyId LIMIT 1")
    suspend fun getCachedPropertyDirect(propertyId: String): CachedPropertyEntity?

    @Query("SELECT * FROM cached_properties ORDER BY accessCount DESC, lastAccessedTimestamp DESC LIMIT :limit")
    fun getFrequentlyAccessedPropertiesFlow(limit: Int = 10): Flow<List<CachedPropertyEntity>>

    @Query("SELECT * FROM cached_properties ORDER BY accessCount DESC, lastAccessedTimestamp DESC LIMIT :limit")
    suspend fun getFrequentlyAccessedPropertiesList(limit: Int = 10): List<CachedPropertyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedProperty(property: CachedPropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedProperties(properties: List<CachedPropertyEntity>)

    @Query("UPDATE cached_properties SET accessCount = accessCount + 1, lastAccessedTimestamp = :timestamp WHERE id = :propertyId")
    suspend fun recordPropertyAccess(propertyId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM cached_properties WHERE id = :propertyId")
    suspend fun deleteCachedProperty(propertyId: String)

    @Query("DELETE FROM cached_properties")
    suspend fun clearAllCachedProperties()

    @Query("SELECT COUNT(*) FROM cached_properties")
    fun getCachedPropertyCountFlow(): Flow<Int>

    // Cached Search Results
    @Query("SELECT * FROM cached_search_results WHERE searchKey = :searchKey LIMIT 1")
    suspend fun getCachedSearchResult(searchKey: String): CachedSearchResultEntity?

    @Query("SELECT * FROM cached_search_results ORDER BY searchTimestamp DESC LIMIT :limit")
    fun getRecentCachedSearchesFlow(limit: Int = 8): Flow<List<CachedSearchResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedSearchResult(search: CachedSearchResultEntity)

    @Query("DELETE FROM cached_search_results")
    suspend fun clearAllCachedSearches()
}


