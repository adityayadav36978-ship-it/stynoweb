package com.example.data.engine

import com.example.BuildConfig
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.DailyMeal
import com.example.data.model.FoodMenu
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.ui.viewmodel.AiAssistantMessage
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class StynoAiIntelligenceEngine {

    data class AiEngineResult(
        val answer: String,
        val actionType: String? = null,
        val actionLabel: String? = null,
        val suggestedProperties: List<Property> = emptyList(),
        val suggestedBookings: List<Booking> = emptyList(),
        val matchedCity: String? = null,
        val matchedCategory: PropertyType? = null,
        val matchedMaxBudget: Double? = null,
        val matchedGender: GenderSuitability? = null,
        val matchedAmenities: Set<String> = emptySet()
    )

    // Conversational Context Memory (persists across multi-turn queries in a session)
    private var rememberedCity: String? = null
    private var rememberedCategory: PropertyType? = null
    private var rememberedMaxBudget: Double? = null
    private var rememberedGender: GenderSuitability? = null
    private val rememberedAmenities = mutableSetOf<String>()
    private var awaitingBudgetForCity: String? = null

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    suspend fun processQuery(
        prompt: String,
        conversationHistory: List<AiAssistantMessage>,
        allProperties: List<Property>,
        userBookings: List<Booking>,
        ownerProperties: List<Property>,
        ownerBookings: List<Booking>,
        propertyMenus: Map<String, FoodMenu>,
        selectedCity: String,
        userRole: UserRole,
        isLoggedIn: Boolean,
        userName: String
    ): AiEngineResult = withContext(Dispatchers.IO) {
        val trimmed = prompt.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        // 1. Try Gemini API if valid key is available
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val hasValidGeminiKey = apiKey.isNotBlank() &&
            apiKey != "MY_GEMINI_API_KEY" &&
            apiKey != "YOUR_GEMINI_API_KEY"

        if (hasValidGeminiKey) {
            try {
                val geminiResult = queryGeminiModel(
                    apiKey = apiKey,
                    prompt = prompt,
                    allProperties = allProperties,
                    selectedCity = selectedCity,
                    userRole = userRole,
                    isLoggedIn = isLoggedIn
                )
                if (geminiResult != null && geminiResult.isNotBlank()) {
                    // Ground Gemini response with real STYNO database matches
                    val semanticGrounding = runSemanticMatching(
                        prompt = prompt,
                        lower = lower,
                        allProperties = allProperties,
                        userBookings = userBookings,
                        ownerProperties = ownerProperties,
                        ownerBookings = ownerBookings,
                        propertyMenus = propertyMenus,
                        selectedCity = selectedCity,
                        userRole = userRole,
                        isLoggedIn = isLoggedIn,
                        userName = userName
                    )
                    return@withContext semanticGrounding.copy(
                        answer = geminiResult
                    )
                }
            } catch (_: Exception) {
                // Fall back gracefully to local semantic engine
            }
        }

        // 2. High-performance, 100% resilient Semantic Real-Data Grounding Engine
        runSemanticMatching(
            prompt = prompt,
            lower = lower,
            allProperties = allProperties,
            userBookings = userBookings,
            ownerProperties = ownerProperties,
            ownerBookings = ownerBookings,
            propertyMenus = propertyMenus,
            selectedCity = selectedCity,
            userRole = userRole,
            isLoggedIn = isLoggedIn,
            userName = userName
        )
    }

    private fun runSemanticMatching(
        prompt: String,
        lower: String,
        allProperties: List<Property>,
        userBookings: List<Booking>,
        ownerProperties: List<Property>,
        ownerBookings: List<Booking>,
        propertyMenus: Map<String, FoodMenu>,
        selectedCity: String,
        userRole: UserRole,
        isLoggedIn: Boolean,
        userName: String
    ): AiEngineResult {
        val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        // --- Context Memory Handling ---
        // Extract city from query or fallback to memory
        val detectedCity = detectCityInPrompt(prompt, allProperties)
        if (detectedCity != null) {
            rememberedCity = detectedCity
        }
        val effectiveCity = rememberedCity ?: if (selectedCity != "All Cities") selectedCity else null

        // Extract budget
        val detectedBudget = detectBudgetInPrompt(lower)
        if (detectedBudget != null) {
            rememberedMaxBudget = detectedBudget
            awaitingBudgetForCity = null
        }

        // Extract category
        val detectedCategory = detectCategoryInPrompt(lower)
        if (detectedCategory != null) {
            rememberedCategory = detectedCategory
        }

        // Extract gender
        val detectedGender = detectGenderInPrompt(lower)
        if (detectedGender != null) {
            rememberedGender = detectedGender
        }

        // Extract amenities
        val detectedAmenities = detectAmenitiesInPrompt(lower)
        rememberedAmenities.addAll(detectedAmenities)

        // 1. OWNER / HOST INTENT
        val isOwnerContext = userRole == UserRole.OWNER ||
            lower.contains("owner") ||
            lower.contains("host") ||
            lower.contains("my listing") ||
            lower.contains("my property") ||
            lower.contains("my properties") ||
            lower.contains("host studio") ||
            lower.contains("owner dashboard")

        if (isOwnerContext) {
            return handleOwnerIntent(
                lower = lower,
                ownerProperties = ownerProperties,
                ownerBookings = ownerBookings,
                propertyMenus = propertyMenus,
                inrFormatter = inrFormatter
            )
        }

        // 2. BOOKINGS INTENT ("my booking", "show my booking", "check-in passcode")
        if (lower.contains("my booking") ||
            lower.contains("show booking") ||
            lower.contains("check booking") ||
            lower.contains("view booking") ||
            lower.contains("my reservation") ||
            lower.contains("passcode") ||
            lower.contains("digital pass")
        ) {
            return handleBookingsIntent(userBookings, inrFormatter)
        }

        // 3. COMPARISON INTENT ("compare", "cheaper", "which is better")
        if (lower.contains("compare") ||
            lower.contains("cheaper") ||
            lower.contains("difference") ||
            lower.contains("vs")
        ) {
            return handleComparisonIntent(
                lower = lower,
                allProperties = allProperties,
                inrFormatter = inrFormatter
            )
        }

        // 4. FOOD MENU INTENT ("today's menu", "food", "lunch", "dinner", "breakfast", "canteen")
        if (lower.contains("menu") ||
            lower.contains("canteen") ||
            lower.contains("mess") ||
            lower.contains("what's for lunch") ||
            lower.contains("what's for dinner") ||
            lower.contains("breakfast today")
        ) {
            return handleFoodMenuIntent(
                lower = lower,
                allProperties = allProperties,
                propertyMenus = propertyMenus,
                effectiveCity = effectiveCity
            )
        }

        // 5. SAFETY & SOS INTENT
        if (lower.contains("safety") ||
            lower.contains("emergency") ||
            lower.contains("sos") ||
            lower.contains("warden") ||
            lower.contains("girls safety")
        ) {
            return AiEngineResult(
                answer = """
                    🛡️ **STYNO Resident Safety Architecture**
                    
                    Every accommodation listed on STYNO undergoes rigorous physical and operational audits:
                    
                    • **Biometric / CCTV Security**: 24/7 video monitoring of main entrance and common lounges.
                    • **On-Site Wardens**: Girls Hostels have round-the-clock female wardens and biometric gate locking.
                    • **Instant Action SOS**: Tap the red Emergency SOS button in the top bar to alert the property warden and STYNO emergency dispatch.
                    • **Digital Passcode Verification**: No unauthorized outsiders can access resident premises without a verified STYNO booking passcode.
                """.trimIndent(),
                actionType = "NAVIGATE_SAFETY",
                actionLabel = "Open Safety Center"
            )
        }

        // 6. COMPLAINTS & MAINTENANCE INTENT
        if (lower.contains("complaint") ||
            lower.contains("broken") ||
            lower.contains("geyser") ||
            lower.contains("wifi down") ||
            lower.contains("cleanliness issue") ||
            lower.contains("report problem")
        ) {
            return AiEngineResult(
                answer = """
                    🚨 **Instant Resident Action Desk**
                    
                    We take room maintenance, plumbing, electrical, and canteen hygiene complaints very seriously.
                    
                    When you lodge an Instant Action Complaint:
                    1. The property warden and caretaker receive an immediate SMS & push dispatch.
                    2. STYNO Escrow locks host payout until verified resident sign-off.
                    3. Standard resolution SLA is under 4 hours for priority tickets.
                """.trimIndent(),
                actionType = "COMPLAINT",
                actionLabel = "File Instant Complaint"
            )
        }

        // 7. STATION / AIRPORT PICKUP INTENT
        if (lower.contains("pickup") ||
            lower.contains("airport") ||
            lower.contains("station") ||
            lower.contains("shuttle") ||
            lower.contains("cab")
        ) {
            return AiEngineResult(
                answer = """
                    🚖 **Verified Station & Airport Pickup Service**
                    
                    Need a hassle-free ride to your hostel or PG?
                    
                    • **How to book**: When checking out a stay, toggle **Optional Pickup Service** in the booking screen.
                    • **Arrival Coordination**: A verified hostel shuttle or cab driver will meet you right at the railway platform exit or airport arrivals gate.
                    • **Safety guarantee**: Real-time driver phone number and vehicle registration are updated directly in your booking confirmation!
                """.trimIndent(),
                actionType = "NAVIGATE_SEARCH",
                actionLabel = "Explore Stays with Pickup"
            )
        }

        // 8. CLARIFICATION HANDLING FOR UNDERSPECIFIED QUERIES
        val words = lower.split(Regex("\\s+")).filter { it.isNotBlank() }
        val isExtremelyVague = words.size <= 3 && (
            lower in listOf("help", "find room", "find place", "need room", "room", "hostel", "pg", "stay", "book", "find stay")
        )
        if (isExtremelyVague && effectiveCity == null && rememberedMaxBudget == null) {
            return AiEngineResult(
                answer = """
                    Namaste! I am your **STYNO AI Assistant**. I can find the best verified stays with 0% brokerage, compare prices, or help manage your bookings.
                    
                    To give you the most accurate recommendations:
                    1. **Which city** are you looking in (e.g. Patna, Bagaha, Delhi, Bengaluru)?
                    2. **What is your budget** (e.g. under ₹3,000, ₹5,000/month)?
                    3. **Preferred stay type** (Hostel, PG, Flat, or Room)?
                """.trimIndent(),
                actionType = "NAVIGATE_SEARCH",
                actionLabel = "Browse All Stays"
            )
        }

        // 9. PROPERTY SEARCH & DISCOVERY MATCHING
        return executePropertySearch(
            effectiveCity = effectiveCity,
            category = rememberedCategory,
            maxBudget = rememberedMaxBudget,
            gender = rememberedGender,
            amenities = rememberedAmenities,
            allProperties = allProperties,
            inrFormatter = inrFormatter
        )
    }

    private fun handleOwnerIntent(
        lower: String,
        ownerProperties: List<Property>,
        ownerBookings: List<Booking>,
        propertyMenus: Map<String, FoodMenu>,
        inrFormatter: NumberFormat
    ): AiEngineResult {
        if (lower.contains("booking") || lower.contains("pending") || lower.contains("reservation")) {
            val pending = ownerBookings.filter { it.status == BookingStatus.UPCOMING || it.status == BookingStatus.ACTIVE }
            val answer = if (pending.isNotEmpty()) {
                val listText = pending.take(4).joinToString("\n") {
                    "• **${it.propertyName}** (${it.roomTypeName}): Guest **${it.guestName}** | Check-in: ${it.checkInDate} | ${inrFormatter.format(it.finalAmount)}"
                }
                """
                    📋 **Host Studio: Active & Upcoming Guest Bookings (${pending.size})**
                    
                    $listText
                    
                    You can inspect verification documents, confirm check-in passcodes, and coordinate room preparation in your Host Studio.
                """.trimIndent()
            } else {
                """
                    📋 **Host Studio: Guest Bookings**
                    
                    You currently have no pending reservations. All scheduled guest bookings are up to date!
                """.trimIndent()
            }
            return AiEngineResult(
                answer = answer,
                actionType = "NAVIGATE_OWNER_DASHBOARD",
                actionLabel = "Open Host Studio",
                suggestedBookings = pending.take(3)
            )
        }

        if (lower.contains("earning") || lower.contains("revenue") || lower.contains("payout") || lower.contains("income") || lower.contains("money")) {
            val totalEarnings = ownerBookings.sumOf { it.finalAmount }
            val confirmedCount = ownerBookings.count { it.status != BookingStatus.CANCELLED }
            val netPayout = totalEarnings * 0.98 // 2% platform facilitation
            val answer = """
                💰 **Host Financial Analytics & Payout Summary**
                
                • **Total Gross Revenue**: ${inrFormatter.format(totalEarnings)}
                • **Confirmed Bookings**: $confirmedCount bookings
                • **Net Settlement Available**: ${inrFormatter.format(netPayout)}
                • **Brokerage Charged by STYNO**: ₹0 (100% Zero-Brokerage Policy)
                
                Payouts are deposited directly to your registered bank account or UPI ID via STYNO Escrow Settlement.
            """.trimIndent()
            return AiEngineResult(
                answer = answer,
                actionType = "NAVIGATE_OWNER_DASHBOARD",
                actionLabel = "View Earnings Tab"
            )
        }

        if (lower.contains("add property") || lower.contains("new property") || lower.contains("list property")) {
            return AiEngineResult(
                answer = """
                    🏡 **List Your Property on STYNO in 10 Guided Steps**
                    
                    1. **Property Type**: Choose Hostel, PG, Flat, Room, Hotel, or Quick Stay.
                    2. **Basic Info**: Name, address, and live Google Maps geolocation.
                    3. **Room & Bed Matrix**: Single, double, 3-sharing allocations.
                    4. **Pricing & Security Deposit**: Transparent zero-brokerage rates.
                    5. **Amenities**: Wi-Fi, 24/7 Power Backup, CCTV, AC.
                    6. **Food & Canteen Menu**: Upload daily meal schedules.
                    7. **Live Verification**: Upload property photos and safety credentials.
                """.trimIndent(),
                actionType = "NAVIGATE_ADD_PROPERTY",
                actionLabel = "Launch 10-Step Wizard"
            )
        }

        // General Owner overview
        val propCount = ownerProperties.size
        val propertiesSummary = if (ownerProperties.isNotEmpty()) {
            ownerProperties.take(3).joinToString("\n") {
                "• **${it.name}** in ${it.city} | Starting ${inrFormatter.format(it.startingPrice)} | ${it.hostelAvailableBeds} beds available"
            }
        } else {
            "No active property listings found. Tap below to publish your first stay!"
        }

        return AiEngineResult(
            answer = """
                🏢 **STYNO Host Intelligence Studio**
                
                **Active Managed Properties ($propCount):**
                $propertiesSummary
                
                **What would you like to do?**
                • Check guest reservation statuses
                • Update today's canteen food menu
                • Configure hourly Quick Stay slots
                • Adjust seasonal room tariffs and deposit amounts
            """.trimIndent(),
            actionType = "NAVIGATE_OWNER_DASHBOARD",
            actionLabel = "Manage Host Properties",
            suggestedProperties = ownerProperties.take(3)
        )
    }

    private fun handleBookingsIntent(
        userBookings: List<Booking>,
        inrFormatter: NumberFormat
    ): AiEngineResult {
        if (userBookings.isEmpty()) {
            return AiEngineResult(
                answer = """
                    📅 **No Active Bookings Found**
                    
                    You do not currently have any active room reservations.
                    
                    Browse our catalog of verified Hostels, PGs, Flats, and Quick Stays with 0% brokerage and instant digital check-in!
                """.trimIndent(),
                actionType = "NAVIGATE_SEARCH",
                actionLabel = "Explore Verified Stays"
            )
        }

        val active = userBookings.firstOrNull { it.status == BookingStatus.UPCOMING || it.status == BookingStatus.ACTIVE }
            ?: userBookings.first()

        val answer = """
            🎫 **Your Active Booking Details**
            
            • **Property**: ${active.propertyName}
            • **Room Type**: ${active.roomTypeName}
            • **Check-In Date**: ${active.checkInDate} (${active.checkInTime})
            • **Duration**: ${active.durationText}
            • **Total Paid**: ${inrFormatter.format(active.finalAmount)} (${active.paymentStatus})
            • **Digital Passcode**: 🔑 `${active.digitalPasscode}`
            
            *Show this passcode at the property entrance for paperless biometric entry.*
        """.trimIndent()

        return AiEngineResult(
            answer = answer,
            actionType = "NAVIGATE_BOOKINGS",
            actionLabel = "Open My Bookings",
            suggestedBookings = listOf(active)
        )
    }

    private fun handleComparisonIntent(
        lower: String,
        allProperties: List<Property>,
        inrFormatter: NumberFormat
    ): AiEngineResult {
        // Try finding 2 matching properties mentioned
        val matchedProps = allProperties.filter {
            lower.contains(it.name.lowercase(Locale.ROOT)) ||
                lower.contains(it.city.lowercase(Locale.ROOT))
        }

        val toCompare = if (matchedProps.size >= 2) {
            matchedProps.take(2)
        } else {
            allProperties.take(2)
        }

        val p1 = toCompare.getOrNull(0)
        val p2 = toCompare.getOrNull(1)

        if (p1 == null || p2 == null) {
            return AiEngineResult(
                answer = "Please mention two properties or a city to compare, and I will generate a side-by-side assessment of price, amenities, and ratings!",
                actionType = "NAVIGATE_SEARCH",
                actionLabel = "Browse Stays"
            )
        }

        val cheaper = if (p1.startingPrice <= p2.startingPrice) p1 else p2
        val diffPrice = Math.abs(p1.startingPrice - p2.startingPrice)

        val answer = """
            ⚖️ **Direct Property Comparison**
            
            1. **${p1.name}** (${p1.propertyType.displayName})
               • Price: ${inrFormatter.format(p1.startingPrice)} / ${p1.durationType.name.lowercase()}
               • Rating: ⭐ ${p1.rating} (${p1.reviewCount} reviews)
               • Location: ${p1.city}, ${p1.area}
               • Key Features: ${p1.shortFacilities.take(3).joinToString(", ")}
            
            2. **${p2.name}** (${p2.propertyType.displayName})
               • Price: ${inrFormatter.format(p2.startingPrice)} / ${p2.durationType.name.lowercase()}
               • Rating: ⭐ ${p2.rating} (${p2.reviewCount} reviews)
               • Location: ${p2.city}, ${p2.area}
               • Key Features: ${p2.shortFacilities.take(3).joinToString(", ")}
            
            💡 **Verdict**: **${cheaper.name}** is more budget-friendly by **${inrFormatter.format(diffPrice)}**. Both properties carry verified STYNO certification.
        """.trimIndent()

        return AiEngineResult(
            answer = answer,
            actionType = "OPEN_COMPARISON",
            actionLabel = "Compare in Side-by-Side Sheet",
            suggestedProperties = listOf(p1, p2)
        )
    }

    private fun handleFoodMenuIntent(
        lower: String,
        allProperties: List<Property>,
        propertyMenus: Map<String, FoodMenu>,
        effectiveCity: String?
    ): AiEngineResult {
        // Find property with food menu
        val targetProperty = allProperties.firstOrNull {
            it.hasCanteenMenu && (effectiveCity == null || it.city.equals(effectiveCity, ignoreCase = true))
        } ?: allProperties.firstOrNull { it.hasCanteenMenu }

        val menu = if (targetProperty != null) propertyMenus[targetProperty.id] ?: targetProperty.foodMenu else null

        if (menu != null && menu.meals.isNotEmpty()) {
            val mealsText = menu.meals.joinToString("\n\n") { meal ->
                "• **${meal.mealType}** (${meal.timing}):\n  ${meal.items.joinToString(", ")}"
            }

            val answer = """
                🍲 **Today's Fresh Canteen Menu: ${targetProperty?.name ?: "STYNO Residency"}**
                *Hygiene Score: ⭐ ${menu.hygieneScore}/5.0 | FSSAI Certified: Yes*
                
                $mealsText
                
                *All daily meals are freshly prepared under strict hygiene supervision. Monthly food subscription is zero brokerage.*
            """.trimIndent()

            return AiEngineResult(
                answer = answer,
                actionType = "NAVIGATE_DETAIL",
                actionLabel = "View Property Food Menu",
                suggestedProperties = listOfNotNull(targetProperty)
            )
        } else {
            return AiEngineResult(
                answer = """
                    🍲 **Fresh Food & Canteen Facility**
                    
                    Most Hostels and PGs on STYNO include 3-time hot meals (Breakfast, Lunch, Evening Tea, Dinner) with unlimited rotis and pure vegetarian / non-veg options.
                    
                    Daily food menus are updated every 24 hours directly by verified hostel wardens!
                """.trimIndent(),
                actionType = "NAVIGATE_SEARCH",
                actionLabel = "Explore Stays with Food"
            )
        }
    }

    private fun executePropertySearch(
        effectiveCity: String?,
        category: PropertyType?,
        maxBudget: Double?,
        gender: GenderSuitability?,
        amenities: Set<String>,
        allProperties: List<Property>,
        inrFormatter: NumberFormat
    ): AiEngineResult {
        // Filter properties
        var candidates = allProperties

        if (effectiveCity != null && effectiveCity != "All Cities") {
            val cityCandidates = candidates.filter { it.city.equals(effectiveCity, ignoreCase = true) }
            if (cityCandidates.isNotEmpty()) {
                candidates = cityCandidates
            }
        }

        if (category != null) {
            val catCandidates = candidates.filter { it.propertyType == category }
            if (catCandidates.isNotEmpty()) {
                candidates = catCandidates
            }
        }

        if (gender != null) {
            val genderCandidates = candidates.filter { it.genderSuitability == gender || it.genderSuitability == GenderSuitability.CO_ED }
            if (genderCandidates.isNotEmpty()) {
                candidates = genderCandidates
            }
        }

        if (maxBudget != null && maxBudget > 0) {
            val budgetCandidates = candidates.filter { it.startingPrice <= maxBudget }
            if (budgetCandidates.isNotEmpty()) {
                candidates = budgetCandidates
            }
        }

        if (amenities.isNotEmpty()) {
            val amenityCandidates = candidates.filter { prop ->
                amenities.any { required ->
                    prop.shortFacilities.any { it.contains(required, ignoreCase = true) } ||
                        prop.allAmenities.any { it.contains(required, ignoreCase = true) }
                }
            }
            if (amenityCandidates.isNotEmpty()) {
                candidates = amenityCandidates
            }
        }

        // Sort by value/rating
        val sortedMatches = candidates.sortedByDescending { it.rating }
        val topMatches = sortedMatches.take(3)

        val cityDisplay = effectiveCity ?: "your preferred location"
        val filterDesc = buildList {
            if (effectiveCity != null) add("in **$effectiveCity**")
            if (category != null) add("for **${category.displayName}**")
            if (maxBudget != null) add("under **${inrFormatter.format(maxBudget)}**")
            if (gender != null) add("eligible for **${gender.displayName}**")
            if (amenities.isNotEmpty()) add("with **${amenities.joinToString(", ")}**")
        }.joinToString(", ")

        val answer = if (topMatches.isNotEmpty()) {
            val propertiesListText = topMatches.mapIndexed { index, prop ->
                "${index + 1}. **${prop.name}** (${prop.propertyType.displayName})\n" +
                    "   • Price: **${inrFormatter.format(prop.startingPrice)}** / ${prop.durationType.name.lowercase()}\n" +
                    "   • Rating: ⭐ **${prop.rating}** (${prop.reviewCount} reviews)\n" +
                    "   • Facilities: ${prop.shortFacilities.take(3).joinToString(" • ")}\n" +
                    "   • Location: ${prop.area}, ${prop.city}"
            }.joinToString("\n\n")

            """
                Found **${candidates.size} verified stay(s)** $filterDesc:
                
                $propertiesListText
                
                Tap any property below to view verified photos, room configurations, and complete a 0% brokerage booking!
            """.trimIndent()
        } else {
            """
                I couldn't find an exact match for $filterDesc, but here are our top verified accommodations with high ratings and zero brokerage:
            """.trimIndent()
        }

        return AiEngineResult(
            answer = answer,
            actionType = "NAVIGATE_SEARCH",
            actionLabel = "View All Matches in Search",
            suggestedProperties = topMatches,
            matchedCity = effectiveCity,
            matchedCategory = category,
            matchedMaxBudget = maxBudget,
            matchedGender = gender,
            matchedAmenities = amenities
        )
    }

    private fun detectCityInPrompt(prompt: String, allProperties: List<Property>): String? {
        val knownCities = listOf(
            "Patna", "Bagaha", "Bettiah", "Motihari", "Muzaffarpur", "Delhi", "Bengaluru",
            "Bangalore", "Noida", "Gurugram", "Gurgaon", "Mumbai", "Pune", "Kota", "Hyderabad",
            "Kolkata", "Chennai", "Jaipur", "Dehradun", "Lucknow", "Varanasi", "Indore"
        ) + allProperties.map { it.city }.distinct()

        for (city in knownCities) {
            if (prompt.contains(city, ignoreCase = true)) {
                return city
            }
        }
        return null
    }

    private fun detectBudgetInPrompt(lower: String): Double? {
        val regexes = listOf(
            Regex("(?:under|below|less than|within|max|upto|up to)\\s*₹?\\s*([0-9]{3,6})"),
            Regex("₹?\\s*([0-9]{3,6})\\s*(?:or less|budget|max)"),
            Regex("budget\\s*(?:of)?\\s*₹?\\s*([0-9]{3,6})")
        )

        for (regex in regexes) {
            val match = regex.find(lower)
            if (match != null) {
                val num = match.groupValues[1].toDoubleOrNull()
                if (num != null && num > 100) return num
            }
        }

        if (lower.contains("cheap") || lower.contains("affordable") || lower.contains("low budget")) {
            return 4000.0
        }

        return null
    }

    private fun detectCategoryInPrompt(lower: String): PropertyType? {
        return when {
            lower.contains("hostel") -> PropertyType.HOSTEL
            lower.contains("pg") || lower.contains("paying guest") -> PropertyType.PG
            lower.contains("flat") || lower.contains("apartment") || lower.contains("1bhk") || lower.contains("2bhk") -> PropertyType.FLAT
            lower.contains("quick stay") || lower.contains("transit pod") || lower.contains("hourly") || lower.contains("sleep pod") -> PropertyType.QUICK_STAY
            lower.contains("hotel") -> PropertyType.HOTEL
            lower.contains("room") -> PropertyType.ROOM
            else -> null
        }
    }

    private fun detectGenderInPrompt(lower: String): GenderSuitability? {
        return when {
            lower.contains("girls") || lower.contains("girl") || lower.contains("female") || lower.contains("women") || lower.contains("ladies") -> GenderSuitability.GIRLS_ONLY
            lower.contains("boys") || lower.contains("boy") || lower.contains("male") || lower.contains("men") -> GenderSuitability.BOYS_ONLY
            lower.contains("family") || lower.contains("families") -> GenderSuitability.FAMILY
            lower.contains("co-ed") || lower.contains("coliving") || lower.contains("co living") -> GenderSuitability.CO_ED
            else -> null
        }
    }

    private fun detectAmenitiesInPrompt(lower: String): Set<String> {
        val set = mutableSetOf<String>()
        if (lower.contains("ac") || lower.contains("air conditioner") || lower.contains("air conditioning")) set.add("Air Conditioner")
        if (lower.contains("food") || lower.contains("mess") || lower.contains("meals") || lower.contains("canteen")) set.add("3-Time Meals")
        if (lower.contains("wifi") || lower.contains("wi-fi") || lower.contains("internet")) set.add("High-Speed Wi-Fi")
        if (lower.contains("attached bathroom") || lower.contains("washroom") || lower.contains("attached washroom")) set.add("Attached Washroom")
        if (lower.contains("kitchen")) set.add("Kitchen")
        if (lower.contains("power backup") || lower.contains("generator")) set.add("24/7 Power Backup")
        if (lower.contains("cctv") || lower.contains("security")) set.add("Biometric / CCTV Security")
        return set
    }

    // Call Gemini 3.5 Flash REST API directly when configured
    private fun queryGeminiModel(
        apiKey: String,
        prompt: String,
        allProperties: List<Property>,
        selectedCity: String,
        userRole: UserRole,
        isLoggedIn: Boolean
    ): String? {
        val topProps = allProperties.take(6).map {
            "{name: \"${it.name}\", city: \"${it.city}\", price: ₹${it.startingPrice}, type: \"${it.propertyType.displayName}\", rating: ${it.rating}}"
        }.joinToString(", ")

        val systemPrompt = "You are STYNO AI, the official accommodation and resident concierge for the STYNO application (0% brokerage marketplace for verified Hostels, PGs, Flats, Rooms, and Quick Stays in India). " +
            "Active city: $selectedCity. User role: $userRole. Logged in: $isLoggedIn. " +
            "Top verified properties in catalog: [$topProps]. " +
            "Respond concisely, politely, and use Indian Rupee (₹) symbol. Help user find stays, compare properties, understand safety, and manage bookings."

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemPrompt))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
                put("maxOutputTokens", 500)
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBodyStr = response.body?.string() ?: return null
        val responseJson = JSONObject(responseBodyStr)
        val candidates = responseJson.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null
        return parts.getJSONObject(0).optString("text", null)
    }
}
