package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_drafts")
data class PropertyDraftEntity(
    @PrimaryKey val id: String,
    val propertyType: String = "HOSTEL",
    val currentStep: Int = 1,
    val propertyName: String = "",
    val address: String = "",
    val country: String = "India",
    val state: String = "Delhi NCR",
    val district: String = "",
    val city: String = "Delhi NCR",
    val area: String = "",
    val landmark: String = "",
    val postalCode: String = "",
    val ownerName: String = "",
    val ownerPhone: String = "",
    val ownerEmail: String = "",
    val gpsCoords: String = "",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val photosCsv: String = "",
    val videoUrl: String = "",
    val isAvailable: Boolean = true,
    val totalRooms: Int = 10,
    val availableRooms: Int = 3,
    val floorNumber: String = "1st Floor",
    val checkInTime: String = "12:00 PM",
    val checkOutTime: String = "11:00 AM",
    val gateClosingTime: String = "10:30 PM",
    val roomOptionsCsv: String = "",
    val amenitiesCsv: String = "",
    val foodServicesConfig: String = "",
    val categoryConfig: String = "",
    val startingPrice: Double = 5000.0,
    val securityDeposit: Double = 5000.0,
    val durationType: String = "MONTHLY",
    val genderSuitability: String = "BOYS_ONLY",
    val updatedAt: Long = System.currentTimeMillis()
)
