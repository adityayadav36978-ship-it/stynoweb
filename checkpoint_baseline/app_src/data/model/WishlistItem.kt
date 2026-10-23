package com.example.data.model

data class WishlistItem(
    val property: Property,
    val savedAt: Long = System.currentTimeMillis(),
    val collectionName: String = "All Favorites",
    val userNotes: String = "",
    val priorityTag: String = "High Priority ⭐",
    val priceAlertEnabled: Boolean = true,
    val firestoreSynced: Boolean = false,
    val firestoreDocId: String = ""
)

data class WishlistCollection(
    val id: String,
    val name: String,
    val description: String = "",
    val iconEmoji: String = "❤️",
    val colorHex: Long = 0xFF2563EB,
    val count: Int = 0
)

enum class WishlistSyncState {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE
}
