package com.example.data.model

enum class PickupHubType(val displayName: String, val iconName: String) {
    RAILWAY_STATION("Railway Station", "train"),
    BUS_STATION("Bus Terminal / ISBT", "bus"),
    AIRPORT("Airport Terminal", "flight"),
    CURRENT_LOCATION("My Current Location (GPS)", "my_location"),
    CUSTOM_LANDMARK("City Landmark / Metro Station", "place")
}

data class PickupOption(
    val hubType: PickupHubType,
    val hubName: String,               // e.g. "New Delhi Railway Station (Platform 1 Exit)"
    val price: Double,                 // e.g. 149.0
    val vehicleType: String = "Hostel Dedicated Shuttle / Cab",
    val estimatedArrivalMins: Int = 15
)

data class PropertyPickupConfig(
    val propertyId: String,
    val isPickupAvailable: Boolean = true,
    val baseCharge: Double = 149.0,
    val vehicleDetails: String = "STYNO Verified Partner Cab / PG Shuttle",
    val driverContact: String = "+91 98765 99881",
    val hubs: List<PickupOption> = listOf(
        PickupOption(PickupHubType.RAILWAY_STATION, "Main Railway Station", 149.0, "PG Shuttle / AC Cab", 15),
        PickupOption(PickupHubType.BUS_STATION, "Central Bus Stand", 99.0, "PG Shuttle / Auto", 10),
        PickupOption(PickupHubType.AIRPORT, "Domestic / Intl Airport", 349.0, "Verified Sedan Cab", 30),
        PickupOption(PickupHubType.CURRENT_LOCATION, "Live GPS Location", 199.0, "Point-to-Point Pickup", 20)
    )
)
