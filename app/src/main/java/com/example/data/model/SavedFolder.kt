package com.example.data.model

data class SavedFolder(
    val id: String,
    val name: String,
    val description: String = "",
    val iconName: String = "folder", // "sun", "work", "school", "beach", "favorite", "star", "home", "folder"
    val colorHex: Long = 0xFF2563EB,
    val propertyIds: Set<String> = emptySet(),
    val createdAt: Long = System.currentTimeMillis()
)
