package com.example.data.engine

import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.StynoAiAction
import com.example.data.model.StynoAiActionType
import com.example.data.model.StynoAiConversationState
import com.example.data.model.StynoAiMessage
import com.example.data.model.StynoAiRole
import java.util.UUID

object StynoConversationalEngine {

    fun createInitialMessage(role: StynoAiRole): StynoAiMessage {
        return if (role == StynoAiRole.RESIDENT) {
            StynoAiMessage(
                id = "init-res-${UUID.randomUUID()}",
                text = "Namaste! Main Styno AI Assistant hoon. Aapko kis location par room chahiye?",
                isUser = false,
                quickReplies = listOf(
                    "📍 Current Location",
                    "Bagaha",
                    "Delhi NCR",
                    "Noida",
                    "Patna",
                    "Kota",
                    "Bangalore"
                )
            )
        } else {
            StynoAiMessage(
                id = "init-own-${UUID.randomUUID()}",
                text = "Namaste Host! Main aapko property listing, room pricing, photo upload aur verification mein guide karunga. Aap kya karna chahte hain?",
                isUser = false,
                quickReplies = listOf(
                    "List New Property",
                    "Upload Room Photos",
                    "Set Room Pricing & Deposit",
                    "Setup UPI / Bank Payment",
                    "Open My Properties"
                )
            )
        }
    }

    fun processResidentInput(
        input: String,
        currentState: StynoAiConversationState,
        allProperties: List<Property>
    ): Pair<StynoAiConversationState, StynoAiMessage> {
        val cleanInput = input.trim()
        val lower = cleanInput.lowercase()

        // 1. Direct command shortcuts (voice or text)
        when {
            lower.contains("booking") || lower.contains("meri booking") -> {
                return currentState to StynoAiMessage(
                    text = "Aapki bookings open ki ja rahi hain. Yahan aap apne active aur upcoming stays, digital passcodes aur check-in details dekh sakte hain.",
                    isUser = false,
                    quickReplies = listOf("Find Another Room", "Back to Home"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "Open My Bookings",
                        payload = "BOOKINGS"
                    )
                )
            }
            lower.contains("list karni") || lower.contains("property list") || lower.contains("host banna") || lower.contains("add property") -> {
                return currentState to StynoAiMessage(
                    text = "Host portal open kiya ja raha hai. Aap apne verified Hostel, PG, Flat ya Room ko direct 0% brokerage ke saath publish kar sakte hain.",
                    isUser = false,
                    quickReplies = listOf("Start 10-Step Wizard", "View Owner Dashboard"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "List Property Now",
                        payload = "ADD_PROPERTY"
                    )
                )
            }
            lower.contains("safety") || lower.contains("sos") || lower.contains("emergency") || lower.contains("police") -> {
                return currentState to StynoAiMessage(
                    text = "STYNO 24/7 Safety & SOS Center open kiya ja raha hai. Emergency assistance ke liye tap karein.",
                    isUser = false,
                    quickReplies = listOf("Call 1800-STYNO-STAY", "Back to Home"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "Open Safety Center",
                        payload = "SAFETY_CENTER"
                    )
                )
            }
            lower.contains("saved") || lower.contains("wishlist") -> {
                return currentState to StynoAiMessage(
                    text = "Aapke saved aur shortlisted stays open kiye ja rahe hain.",
                    isUser = false,
                    quickReplies = listOf("Find More Rooms", "Back to Home"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "Open Wishlist",
                        payload = "SAVED"
                    )
                )
            }
            // Direct Q&A: What is Styno / Zero Brokerage
            lower.contains("what is styno") || lower.contains("styno kya hai") || lower.contains("about styno") || lower.contains("app kya hai") -> {
                return currentState to StynoAiMessage(
                    text = "STYNO ek 100% Zero Brokerage verified accommodation network hai. Yahan aap Hostels, PGs, Hotels, Flats, Single Rooms aur hourly Quick Stays bina kisi broker fee ke direct hosts se book kar sakte hain.",
                    isUser = false,
                    quickReplies = listOf("Find Hostels & PGs", "View Quick Stays", "Girls Safety Info")
                )
            }
            // Direct Q&A: Digital Passcode & Check-in
            lower.contains("passcode") || lower.contains("check in kaise") || lower.contains("keyless") || lower.contains("digital pass") -> {
                return currentState to StynoAiMessage(
                    text = "Har confirmed booking ke baad aapko app mein ek secure Digital Passcode milta hai. Property pahunchte hi biometric punch ya passcode dikha kar instant check-in karein — zero paperwork!",
                    isUser = false,
                    quickReplies = listOf("View My Bookings", "Search Verified Stays")
                )
            }
            // Direct Q&A: Girls Safety
            lower.contains("girl safety") || lower.contains("girls safety") || lower.contains("mahila suraksha") || lower.contains("female warden") -> {
                val girlsStays = allProperties.filter {
                    it.genderSuitability == GenderSuitability.GIRLS_ONLY || it.girlsSafetyVerification.status.name == "VERIFIED"
                }.take(3)
                return currentState to StynoAiMessage(
                    text = "STYNO Girls Stays mein 24/7 on-site female wardens, biometric gate security, CCTV monitoring aur strict visitor screening rehti hai. Yahan top verified girls properties hain:",
                    isUser = false,
                    quickReplies = listOf("Girls Hostels in Noida", "Girls PG in Delhi", "Safety Center"),
                    matchedProperties = girlsStays
                )
            }
            // Direct Q&A: Food & Meals
            lower.contains("khana") || lower.contains("food") || lower.contains("mess") || lower.contains("canteen") || lower.contains("meals") -> {
                return currentState to StynoAiMessage(
                    text = "STYNO Hostels aur PGs mein hygienic 3-time meals (Breakfast, Lunch, Dinner) included hote hain. Property page par aap live weekly menu aur actual canteen photos dekh sakte hain.",
                    isUser = false,
                    quickReplies = listOf("Stays with Food Included", "View Menu", "Search Rooms")
                )
            }
            // Direct Q&A: Security Deposit Refund
            lower.contains("deposit") || lower.contains("refund") || lower.contains("security money") -> {
                return currentState to StynoAiMessage(
                    text = "Security deposit direct host ke saath secure rehta hai. Check-out ke baad bina kisi unverified deduction ke aapke UPI ID par instant refund credit ho jata hai.",
                    isUser = false,
                    quickReplies = listOf("0% Brokerage Policy", "Explore Stays")
                )
            }
            // Direct Q&A: Quick Stay / Hourly Pods
            lower.contains("quick stay") || lower.contains("hourly") || lower.contains("transit") || lower.contains("layover") -> {
                val quickStays = allProperties.filter { it.durationType == com.example.data.model.DurationType.HOURLY }.take(3)
                return currentState to StynoAiMessage(
                    text = "Quick Stay transit rooms aap 3h, 6h, 12h ya 24h ke flexible slots par book kar sakte hain. Short layovers, interview days aur exams ke liye best solution!",
                    isUser = false,
                    quickReplies = listOf("Book 3 Hours Pod", "Book 6 Hours Stay"),
                    matchedProperties = if (quickStays.isNotEmpty()) quickStays else allProperties.take(2)
                )
            }
            // Direct Q&A: Wi-Fi & Study Amenities
            lower.contains("wifi") || lower.contains("wi-fi") || lower.contains("internet") || lower.contains("speed") -> {
                return currentState to StynoAiMessage(
                    text = "Sabhi verified student aur working hostels/PGs mein high-speed fiber Wi-Fi, power backup, study desks aur silent study zones available hain. Har property card par Wi-Fi verification status check karein.",
                    isUser = false,
                    quickReplies = listOf("Search Study Stays", "Verified Hostels")
                )
            }
            // Direct Q&A: Cancellation Policy
            lower.contains("cancel") || lower.contains("cancellation") -> {
                return currentState to StynoAiMessage(
                    text = "STYNO par flexible cancellation policy milti hai. Check-in se 24 ghante pehle cancel karne par poora refund direct aapke payment mode par milta hai, bina kisi hidden fee ke.",
                    isUser = false,
                    quickReplies = listOf("Open My Bookings", "Contact 24/7 Helpline"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "View My Bookings",
                        payload = "BOOKINGS"
                    )
                )
            }
        }

        // Budget Correction & Context Update (TEST B: Retains location, updates budget)
        val (extractedBudget, parsedPeriod) = parseBudget(cleanInput)
        val isBudgetUpdate = extractedBudget != null && (
            lower.contains("budget") || lower.contains("badal") || lower.contains("change") ||
            lower.contains("ab ") || lower.contains("kar do") || lower.contains("karo") ||
            (currentState.selectedLocation != null && currentState.conversationStep >= 3)
        )

        if (isBudgetUpdate && currentState.selectedLocation != null) {
            val updatedState = currentState.copy(
                budgetMax = extractedBudget,
                budgetPeriod = parsedPeriod,
                conversationStep = 6
            )
            val matches = searchMatchingProperties(updatedState, allProperties)
            return if (matches.isNotEmpty()) {
                updatedState to StynoAiMessage(
                    text = "Theek hai, ${currentState.selectedLocation} mein aapka budget ₹${extractedBudget.toInt()} update kar diya gaya hai. Yahan matching verified stays hain:",
                    isUser = false,
                    quickReplies = listOf("Lowest Price", "Girls Safety Verified", "Change Location"),
                    matchedProperties = matches
                )
            } else {
                updatedState to StynoAiMessage(
                    text = "Khed hai, ${currentState.selectedLocation} mein ₹${extractedBudget.toInt()} ke andar koi verified stay available nahi mila. Kya aap budget thoda badhana chahenge?",
                    isUser = false,
                    quickReplies = listOf("Change Budget", "Show All in City", "Reset Search"),
                    matchedProperties = emptyList()
                )
            }
        }

        // Natural Stay Inquiry (e.g., "Mujhe Bagaha mein 2 din ke liye room chahiye")
        val knownCities = listOf("bagaha", "delhi", "noida", "patna", "kota", "bengaluru", "bangalore", "pune", "gurugram", "gurgaon", "mumbai")
        val matchedKnownCity = knownCities.firstOrNull { lower.contains(it) }
        val hasBudget = extractedBudget != null || lower.contains("under")
        val hasStayType = lower.contains("hostel") || lower.contains("pg") || lower.contains("hotel") || lower.contains("flat") || lower.contains("room")

        if (matchedKnownCity != null && !hasBudget && currentState.conversationStep <= 1) {
            val cityCapitalized = matchedKnownCity.replaceFirstChar { it.uppercase() }
            val updatedState = currentState.copy(
                selectedLocation = cityCapitalized,
                conversationStep = 2
            )
            val durationText = if (lower.contains("din") || lower.contains("day")) "stay ke liye " else ""
            return updatedState to StynoAiMessage(
                text = "Zaroor! $cityCapitalized mein ${durationText}room dhoondhne mein help karta hoon. Kitne log rahenge ya room kiske liye chahiye?",
                isUser = false,
                quickReplies = listOf("Boys 👦", "Girls 👧", "2 Log 👥", "Family 👨‍👩‍👧‍👦", "Student 🎓")
            )
        }

        if (matchedKnownCity != null && hasBudget) {
            val cityCapitalized = matchedKnownCity.replaceFirstChar { it.uppercase() }
            val updatedState = currentState.copy(
                selectedLocation = cityCapitalized,
                budgetMax = extractedBudget ?: currentState.budgetMax,
                budgetPeriod = parsedPeriod,
                conversationStep = 6
            )
            val matches = searchMatchingProperties(updatedState, allProperties)
            return if (matches.isNotEmpty()) {
                updatedState to StynoAiMessage(
                    text = "Mujhe $cityCapitalized mein ${matches.size} verified stays mile hain! Real photos, pricing aur amenities check karein:",
                    isUser = false,
                    quickReplies = listOf("Lowest Price", "Girls Safety Verified", "Change Location"),
                    matchedProperties = matches
                )
            } else {
                updatedState to StynoAiMessage(
                    text = "Khed hai, $cityCapitalized mein is filter ke mutabiq koi verified stay available nahi mila.",
                    isUser = false,
                    quickReplies = listOf("Change Budget", "Show All in City", "Reset Search"),
                    matchedProperties = emptyList()
                )
            }
        }

        // 2. Multi-turn Guided Conversation Flow
        var nextState = currentState

        when (currentState.conversationStep) {
            1 -> {
                // User provided location
                val extractedLocation = cleanLocation(cleanInput)
                nextState = nextState.copy(
                    selectedLocation = extractedLocation,
                    conversationStep = 2
                )
                return nextState to StynoAiMessage(
                    text = "Theek hai, $extractedLocation mein search karenge! Kitne log rahenge ya room kiske liye chahiye?",
                    isUser = false,
                    quickReplies = listOf(
                        "Boys 👦",
                        "Girls 👧",
                        "2 Log 👥",
                        "Family 👨‍👩‍👧‍👦",
                        "Student 🎓",
                        "Working Professional 💼"
                    )
                )
            }
            2 -> {
                // User provided occupant type or guest count
                val audience = cleanInput
                val occupants = when {
                    lower.contains("2") || lower.contains("do log") || lower.contains("two") -> 2
                    lower.contains("3") || lower.contains("teen") || lower.contains("three") -> 3
                    lower.contains("family") || lower.contains("parivar") -> 4
                    else -> 1
                }
                nextState = nextState.copy(
                    targetAudience = audience,
                    occupantCount = occupants,
                    conversationStep = 3
                )
                val occText = if (occupants > 1) "$occupants logon ke liye" else "aapke liye"
                return nextState to StynoAiMessage(
                    text = "Theek hai, $occText. Stay ka main purpose kya hai?",
                    isUser = false,
                    quickReplies = listOf(
                        "College / Coaching 📚",
                        "Job / Office 💼",
                        "Exam / Interview 🎯",
                        "Medical / Treatment 🏥",
                        "Transit / Quick Travel ⚡"
                    )
                )
            }
            3 -> {
                // User provided stay purpose
                val purpose = cleanInput
                nextState = nextState.copy(
                    stayPurpose = purpose,
                    conversationStep = 4
                )
                return nextState to StynoAiMessage(
                    text = "Aapka budget kitna hai? (Monthly, Daily ya Hourly transit)",
                    isUser = false,
                    quickReplies = listOf(
                        "Under ₹5,000/mo",
                        "₹5,000 - ₹8,000/mo",
                        "₹8,000 - ₹12,000/mo",
                        "₹12,000+/mo",
                        "Daily / Per Night 🏨",
                        "Hourly Transit Pod ⚡"
                    )
                )
            }
            4 -> {
                // User provided budget
                nextState = nextState.copy(
                    budgetMax = extractedBudget,
                    budgetPeriod = parsedPeriod,
                    conversationStep = 5
                )
                return nextState to StynoAiMessage(
                    text = "Kaun si facilities aapke liye sabse zaroori hain?",
                    isUser = false,
                    quickReplies = listOf(
                        "Food & Mess Included 🍲",
                        "AC Room ❄️",
                        "Attached Washroom 🚿",
                        "High-Speed Wi-Fi 📶",
                        "No Sharing / Private Room 🛏️",
                        "Skip & Show Stays ✨"
                    )
                )
            }
            5 -> {
                // User provided facilities / finished preferences
                if (!lower.contains("skip")) {
                    val currentAmenities = nextState.preferredAmenities.toMutableSet()
                    if (lower.contains("food") || lower.contains("mess")) currentAmenities.add("Food")
                    if (lower.contains("ac")) currentAmenities.add("Air Conditioner")
                    if (lower.contains("washroom") || lower.contains("bath")) currentAmenities.add("Attached Washroom")
                    if (lower.contains("wi-fi") || lower.contains("wifi")) currentAmenities.add("High-Speed Wi-Fi")
                    if (lower.contains("private") || lower.contains("no sharing")) nextState = nextState.copy(isSharingOk = false)
                    nextState = nextState.copy(preferredAmenities = currentAmenities)
                }

                nextState = nextState.copy(conversationStep = 6)
                val matches = searchMatchingProperties(nextState, allProperties)

                val locText = nextState.selectedLocation ?: "aapke area"
                return if (matches.isNotEmpty()) {
                    nextState to StynoAiMessage(
                        text = "🎉 Mujhe aapke requirements ke mutabiq ${matches.size} verified stays mile hain! Yahan real photos, genuine prices aur amenities check karein:",
                        isUser = false,
                        quickReplies = listOf(
                            "Filter by Lower Price",
                            "Girls Safety Verified Only",
                            "Change Location",
                            "Search More"
                        ),
                        matchedProperties = matches
                    )
                } else {
                    nextState to StynoAiMessage(
                        text = "Khed hai, $locText mein is filter ke mutabiq koi verified stay available nahi mila.",
                        isUser = false,
                        quickReplies = listOf(
                            "Adjust Budget",
                            "View All in City",
                            "Reset Search"
                        ),
                        matchedProperties = emptyList()
                    )
                }
            }
            else -> {
                // Step 6+: user interacting with results or asking follow-ups
                if (lower.contains("reset") || lower.contains("new search") || lower.contains("shuru se")) {
                    val freshState = StynoAiConversationState(role = StynoAiRole.RESIDENT, conversationStep = 1)
                    return freshState to StynoAiMessage(
                        text = "Search reset ho gayi hai. Kis location par room chahiye?",
                        isUser = false,
                        quickReplies = listOf("Bagaha", "Delhi NCR", "Noida", "Patna", "Kota", "Bangalore")
                    )
                }

                if (lower.contains("budget") || lower.contains("price")) {
                    val matches = allProperties.sortedBy { it.startingPrice }.take(4)
                    return nextState to StynoAiMessage(
                        text = "Lowest starting price ke hisaab se sorted stays yeh rahein:",
                        isUser = false,
                        quickReplies = listOf("Under ₹5,000", "Under ₹8,000", "Reset Search"),
                        matchedProperties = matches
                    )
                }

                if (lower.contains("girl") || lower.contains("safety") || lower.contains("mahila")) {
                    val girlsStays = allProperties.filter {
                        it.genderSuitability == GenderSuitability.GIRLS_ONLY || it.girlsSafetyVerification.status.name == "VERIFIED"
                    }.take(4)
                    return nextState to StynoAiMessage(
                        text = "🛡️ Verified Girls Hostels & PGs with 24/7 Female Warden, CCTV & biometric safety:",
                        isUser = false,
                        quickReplies = listOf("View Safety Guidelines", "Book Verified Stay"),
                        matchedProperties = girlsStays
                    )
                }

                // Smart helpful response connecting to matching properties without echoing raw input
                val results = searchMatchingProperties(nextState, allProperties)
                return nextState to StynoAiMessage(
                    text = "Aapke liye available verified stays yeh rahein. Instant check-in aur 0% brokerage ke saath direct book karein:",
                    isUser = false,
                    quickReplies = listOf("Book a Room", "Contact Host", "Start New Search"),
                    matchedProperties = results.ifEmpty { allProperties.take(3) }
                )
            }
        }
    }

    fun processOwnerInput(
        input: String,
        currentState: StynoAiConversationState,
        allProperties: List<Property>
    ): Pair<StynoAiConversationState, StynoAiMessage> {
        val cleanInput = input.trim()
        val lower = cleanInput.lowercase()

        when {
            lower.contains("list") || lower.contains("add property") || lower.contains("naya") -> {
                return currentState to StynoAiMessage(
                    text = "Main aapko 10-step Property Listing Wizard par le chal raha hoon jahan aap basic info, room types, pricing aur amenities bhar sakte hain.",
                    isUser = false,
                    quickReplies = listOf("Open Step 1 (Type)", "Open Step 2 (Details)", "Back to Dashboard"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "Open Listing Wizard",
                        payload = "ADD_PROPERTY",
                        wizardStep = 1
                    )
                )
            }
            lower.contains("photo") || lower.contains("video") || lower.contains("upload") -> {
                return currentState to StynoAiMessage(
                    text = "Property photos aur video tours upload karne ke liye wizard ka Step 2 (Basic Info & Media) open karein:",
                    isUser = false,
                    quickReplies = listOf("Open Photo Upload Step", "Open My Properties"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "Go to Photo Upload",
                        payload = "ADD_PROPERTY",
                        wizardStep = 2
                    )
                )
            }
            lower.contains("price") || lower.contains("rent") || lower.contains("deposit") || lower.contains("kiraya") -> {
                return currentState to StynoAiMessage(
                    text = "Room pricing, sharing rates aur security deposit set karne ke liye Step 4 (Pricing) par chalein:",
                    isUser = false,
                    quickReplies = listOf("Open Pricing Step", "Quick Stay Slots"),
                    action = StynoAiAction(
                        type = StynoAiActionType.NAVIGATE_SCREEN,
                        title = "Set Room Pricing",
                        payload = "ADD_PROPERTY",
                        wizardStep = 4
                    )
                )
            }
            lower.contains("payment") || lower.contains("upi") || lower.contains("bank") || lower.contains("payout") -> {
                return currentState to StynoAiMessage(
                    text = "Apna Host Payout UPI ID aur Bank Account details setup karne ke liye tap karein:",
                    isUser = false,
                    quickReplies = listOf("Open Payment Setup", "Open Dashboard"),
                    action = StynoAiAction(
                        type = StynoAiActionType.OPEN_OWNER_TAB,
                        title = "Host Payment Setup",
                        ownerTabIndex = 3
                    )
                )
            }
            lower.contains("food") || lower.contains("menu") || lower.contains("mess") || lower.contains("khana") -> {
                return currentState to StynoAiMessage(
                    text = "Daily canteen schedule, meal timings aur menu items manage karne ke liye:",
                    isUser = false,
                    quickReplies = listOf("Open Food & Menu Tab", "Wizard Food Step"),
                    action = StynoAiAction(
                        type = StynoAiActionType.OPEN_OWNER_TAB,
                        title = "Manage Food Menu",
                        ownerTabIndex = 4
                    )
                )
            }
            lower.contains("quick") || lower.contains("transit") || lower.contains("pod") || lower.contains("hourly") -> {
                return currentState to StynoAiMessage(
                    text = "Quick Stay hourly slots (3h, 6h, 12h, 24h) manage karne ke liye:",
                    isUser = false,
                    quickReplies = listOf("Open Quick Stay Tab", "Back to Dashboard"),
                    action = StynoAiAction(
                        type = StynoAiActionType.OPEN_OWNER_TAB,
                        title = "Manage Quick Stay",
                        ownerTabIndex = 2
                    )
                )
            }
            else -> {
                return currentState to StynoAiMessage(
                    text = "Host Assistant aapko har step par guide kar sakta hai: Listing create karna, photos upload karna, pricing set karna, ya payout account jodna.",
                    isUser = false,
                    quickReplies = listOf(
                        "List New Property",
                        "Upload Photos",
                        "Pricing & Deposit",
                        "Payment Setup",
                        "My Properties"
                    )
                )
            }
        }
    }

    private fun cleanLocation(raw: String): String {
        val lower = raw.lowercase()
        val knownCities = listOf("bagaha", "delhi", "noida", "patna", "kota", "bengaluru", "bangalore", "pune", "gurugram", "gurgaon", "mumbai")
        val foundCity = knownCities.firstOrNull { lower.contains(it) }
        if (foundCity != null) {
            return foundCity.replaceFirstChar { it.uppercase() }
        }
        return raw.replace("📍", "").replace("Current Location", "Local Area").trim()
    }

    private fun parseBudget(raw: String): Pair<Float?, String> {
        val lower = raw.lowercase()
        val period = when {
            lower.contains("hour") -> "HOURLY"
            lower.contains("day") || lower.contains("night") -> "DAILY"
            else -> "MONTHLY"
        }

        val digitsRegex = Regex("""(?:\b|₹|rs\.?\s*)(\d{3,6})\b""", RegexOption.IGNORE_CASE)
        val extractedNum = digitsRegex.find(lower)?.groupValues?.get(1)?.toFloatOrNull()
        val kRegex = Regex("""(\d{1,2})\s*k\b""", RegexOption.IGNORE_CASE)
        val kMatch = kRegex.find(lower)?.groupValues?.get(1)?.toFloatOrNull()?.times(1000f)

        val budget = extractedNum ?: kMatch ?: when {
            lower.contains("5,000") || lower.contains("5000") -> 5000f
            lower.contains("8,000") || lower.contains("8000") -> 8000f
            lower.contains("12,000") || lower.contains("12000") -> 12000f
            lower.contains("15,000") || lower.contains("15000") -> 15000f
            else -> null
        }
        return budget to period
    }

    private fun searchMatchingProperties(
        state: StynoAiConversationState,
        allProperties: List<Property>
    ): List<Property> {
        var filtered = allProperties

        // Location match
        state.selectedLocation?.let { loc ->
            if (loc.isNotBlank() && !loc.equals("Local Area", ignoreCase = true)) {
                filtered = filtered.filter {
                    it.city.contains(loc, ignoreCase = true) ||
                    it.area.contains(loc, ignoreCase = true) ||
                    it.address.contains(loc, ignoreCase = true)
                }
            }
        }

        // Target Audience match
        state.targetAudience?.let { aud ->
            val lower = aud.lowercase()
            when {
                lower.contains("girl") || lower.contains("women") -> {
                    filtered = filtered.filter { it.genderSuitability == GenderSuitability.GIRLS_ONLY || it.genderSuitability == GenderSuitability.CO_ED }
                }
                lower.contains("boy") || lower.contains("men") -> {
                    filtered = filtered.filter { it.genderSuitability == GenderSuitability.BOYS_ONLY || it.genderSuitability == GenderSuitability.CO_ED }
                }
                lower.contains("family") -> {
                    filtered = filtered.filter { it.genderSuitability == GenderSuitability.FAMILY || it.genderSuitability == GenderSuitability.CO_ED || it.propertyType == PropertyType.FLAT }
                }
            }
        }

        // Budget match
        state.budgetMax?.let { maxPrice ->
            filtered = filtered.filter { it.startingPrice <= maxPrice * 1.15 }
        }

        // Amenities match with smart synonyms
        if (state.preferredAmenities.isNotEmpty()) {
            val amenityMatched = filtered.filter { prop ->
                state.preferredAmenities.any { required ->
                    val reqLower = required.lowercase()
                    prop.allAmenities.any { am ->
                        val amLower = am.lowercase()
                        when {
                            reqLower.contains("washroom") || reqLower.contains("bath") ->
                                amLower.contains("washroom") || amLower.contains("bath")
                            reqLower.contains("food") || reqLower.contains("mess") || reqLower.contains("meal") ->
                                amLower.contains("food") || amLower.contains("mess") || amLower.contains("meal")
                            reqLower.contains("ac") || reqLower.contains("air") ->
                                amLower.contains("ac") || amLower.contains("air") || amLower.contains("cooler")
                            reqLower.contains("wi-fi") || reqLower.contains("wifi") ->
                                amLower.contains("wifi") || amLower.contains("wi-fi") || amLower.contains("internet")
                            else -> amLower.contains(reqLower)
                        }
                    }
                }
            }
            if (amenityMatched.isNotEmpty()) {
                filtered = amenityMatched
            }
        }

        return filtered.take(6)
    }
}
