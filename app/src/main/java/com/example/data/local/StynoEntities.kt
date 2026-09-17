package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BookingStatus
import com.example.data.model.PropertyType

@Entity(tableName = "saved_properties")
data class SavedPropertyEntity(
    @PrimaryKey val propertyId: String,
    val savedAt: Long = System.currentTimeMillis(),
    val collectionName: String = "All Favorites",
    val userNotes: String = "",
    val priorityTag: String = "High Priority ⭐",
    val priceAlertEnabled: Boolean = true,
    val firestoreSynced: Boolean = false,
    val firestoreDocId: String = ""
)

@Entity(tableName = "wishlist_collections")
data class LocalWishlistCollectionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val iconEmoji: String = "❤️",
    val colorHex: Long = 0xFF2563EB,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class LocalBookingEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val propertyName: String,
    val propertyType: String,
    val propertyImage: String,
    val address: String,
    val roomTypeName: String,
    val checkInDate: String,
    val checkInTime: String,
    val checkOutDate: String,
    val durationText: String,
    val guestName: String,
    val guestPhone: String,
    val guestEmail: String,
    val guestIdProofType: String,
    val basePrice: Double,
    val serviceFee: Double,
    val taxes: Double,
    val discount: Double,
    val finalAmount: Double,
    val securityDeposit: Double,
    val digitalPasscode: String,
    val status: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val transactionId: String = "",
    val paymentGateway: String = "Stripe",
    val bookedAtTimestamp: Long,
    val propertyContactPhone: String,
    val ownerUpiId: String = "",
    val ownerAccountHolderName: String = "",
    val ownerBankName: String = "",
    val ownerPayoutStatus: String = "Settlement Scheduled (Instant UPI / NEFT)"
)

@Entity(tableName = "chat_messages")
data class LocalChatMessageEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val propertyName: String,
    val senderName: String,
    val isFromGuest: Boolean,
    val text: String,
    val timeString: String,
    val timestamp: Long
)

@Entity(tableName = "custom_properties")
data class CustomPropertyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val propertyType: String,
    val genderSuitability: String,
    val address: String,
    val city: String,
    val area: String,
    val nearbyLandmark: String,
    val startingPrice: Double,
    val durationType: String,
    val verificationStatus: String,
    val amenitiesCsv: String,
    val roomTypesCsv: String,
    val ownerName: String,
    val ownerPhone: String,
    val imagesCsv: String = "",
    val description: String = "",
    val securityDeposit: Double = 0.0,
    val flatBhkConfig: String = "",
    val furnishingType: String = "",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val country: String = "India",
    val state: String = "Delhi NCR",
    val district: String = "",
    val quickStayEnabled: Boolean = false,
    val price1Hour: Double = 199.0,
    val price2Hours: Double = 299.0,
    val price3Hours: Double = 399.0,
    val price4Hours: Double = 499.0,
    val price5Hours: Double = 599.0,
    val price6Hours: Double = 699.0,
    val price10Hours: Double = 899.0,
    val price12Hours: Double = 999.0,
    val price24Hours: Double = 1499.0,
    val availableSlots: Int = 5,
    val instantCheckIn: Boolean = true,
    val checkInInstructions: String = "",
    val transitFacilitiesCsv: String = "",
    val ownerAccountHolderName: String = "",
    val ownerBankName: String = "",
    val ownerAccountNumber: String = "",
    val ownerIfscCode: String = "",
    val ownerUpiId: String = "",
    val ownerSettlementMode: String = "Instant UPI Settlement (0% fee)",
    val ownerIsVerifiedAccount: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "complaints")
data class LocalComplaintEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String,
    val createdAt: String,
    val createdTimestamp: Long,
    val userRefId: String,
    val userName: String,
    val userPhone: String,
    val userEmail: String,
    val propertyId: String?,
    val propertyName: String?,
    val isOwnerComplaint: Boolean,
    val assignedTeam: String,
    val resolutionNotes: String
)

@Entity(tableName = "cached_properties")
data class CachedPropertyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val propertyType: String,
    val genderSuitability: String,
    val address: String,
    val city: String,
    val area: String,
    val nearbyLandmark: String,
    val distanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val startingPrice: Double,
    val durationType: String,
    val rating: Float,
    val reviewCount: Int,
    val verificationStatus: String,
    val isAvailable: Boolean,
    val featured: Boolean,
    val imageDrawableNamesJson: String,
    val shortFacilitiesJson: String,
    val allAmenitiesJson: String,
    val roomOptionsJson: String,
    val checkInTime: String,
    val checkOutTime: String,
    val gateClosingTime: String,
    val cancellationPolicy: String,
    val rulesJson: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String,
    val ownerResponseTime: String,
    val ownerVerified: Boolean = true,
    val ownerJoinedDate: String = "Joined 2024",
    val hasCanteenMenu: Boolean = true,
    val quickStayHoursCsv: String = "3,6,12,24",
    val accessCount: Int = 1,
    val lastAccessedTimestamp: Long = System.currentTimeMillis(),
    val cachedAtTimestamp: Long = System.currentTimeMillis(),
    val isPreCached: Boolean = false
)

@Entity(tableName = "cached_search_results")
data class CachedSearchResultEntity(
    @PrimaryKey val searchKey: String,
    val queryText: String,
    val city: String,
    val category: String,
    val gender: String,
    val resultPropertyIdsJson: String,
    val resultCount: Int,
    val searchTimestamp: Long = System.currentTimeMillis()
)


