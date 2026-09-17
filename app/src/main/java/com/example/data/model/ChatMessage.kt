package com.example.data.model

data class ChatMessage(
    val id: String,
    val propertyId: String,
    val propertyName: String,
    val senderName: String,
    val isFromGuest: Boolean,
    val text: String,
    val timeString: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)
