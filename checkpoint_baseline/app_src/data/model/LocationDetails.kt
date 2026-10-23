package com.example.data.model

data class LocationDetails(
    val country: String = "India",
    val state: String = "Delhi NCR",
    val district: String = "Gautam Buddha Nagar",
    val city: String = "Noida",
    val locality: String = "Sector 62",
    val neighborhood: String = "Institutional Area",
    val postalCode: String = "201309",
    val nearbyLandmarks: String = "Near Electronic City Metro Station & Tech Park",
    val nearbyRailwayStation: String = "Anand Vihar Terminal (ANVT) - 8.5 km",
    val nearbyBusStand: String = "Noida Inter-State Bus Terminus (ISBT) - 5.2 km",
    val nearbyAirport: String = "Indira Gandhi International Airport (DEL) - 34 km",
    val nearbyImportantPlaces: String = "Knowledge Boulevard, Cyber Park",
    val nearbyUniversities: String = "Jaypee Institute of Information Technology (JIIT) - 600 m",
    val nearbyHospitals: String = "Fortis Hospital Sector 62 - 1.1 km",
    val nearbyMarkets: String = "B-Block Sector 62 Central Market - 450 m",
    val nearbyMajorRoads: String = "NH-24 / Delhi-Meerut Expressway - 700 m",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.3653
) {
    fun toFormattedAddress(): String {
        return "$locality, $neighborhood, $city, $state - $postalCode"
    }
}
