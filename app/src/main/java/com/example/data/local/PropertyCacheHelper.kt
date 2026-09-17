package com.example.data.local

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.OwnerInfo
import com.example.data.model.Property
import com.example.data.model.PropertyRule
import com.example.data.model.PropertyType
import com.example.data.model.RoomOption
import com.example.data.model.VerificationStatus
import org.json.JSONArray
import org.json.JSONObject

object PropertyCacheHelper {

    fun propertyToEntity(
        property: Property,
        accessCount: Int = 1,
        isPreCached: Boolean = false,
        lastAccessedTimestamp: Long = System.currentTimeMillis()
    ): CachedPropertyEntity {
        return CachedPropertyEntity(
            id = property.id,
            name = property.name,
            propertyType = property.propertyType.name,
            genderSuitability = property.genderSuitability.name,
            address = property.address,
            city = property.city,
            area = property.area,
            nearbyLandmark = property.nearbyLandmark,
            distanceKm = property.distanceKm,
            latitude = property.latitude,
            longitude = property.longitude,
            startingPrice = property.startingPrice,
            durationType = property.durationType.name,
            rating = property.rating,
            reviewCount = property.reviewCount,
            verificationStatus = property.verificationStatus.name,
            isAvailable = property.isAvailable,
            featured = property.featured,
            imageDrawableNamesJson = serializeStringList(property.imageDrawableNames),
            shortFacilitiesJson = serializeStringList(property.shortFacilities),
            allAmenitiesJson = serializeStringList(property.allAmenities),
            roomOptionsJson = serializeRoomOptions(property.roomOptions),
            checkInTime = property.checkInTime,
            checkOutTime = property.checkOutTime,
            gateClosingTime = property.gateClosingTime,
            cancellationPolicy = property.cancellationPolicy,
            rulesJson = serializeRules(property.rules),
            ownerName = property.ownerInfo.name,
            ownerPhone = property.ownerInfo.phone,
            ownerEmail = property.ownerInfo.email,
            ownerResponseTime = property.ownerInfo.responseTime,
            ownerVerified = property.ownerInfo.verifiedHost,
            ownerJoinedDate = property.ownerInfo.joinedDate,
            hasCanteenMenu = property.hasCanteenMenu,
            quickStayHoursCsv = property.quickStayHours.joinToString(","),
            accessCount = accessCount,
            lastAccessedTimestamp = lastAccessedTimestamp,
            cachedAtTimestamp = System.currentTimeMillis(),
            isPreCached = isPreCached
        )
    }

    fun entityToProperty(entity: CachedPropertyEntity): Property {
        val pType = runCatching { PropertyType.valueOf(entity.propertyType) }.getOrDefault(PropertyType.HOSTEL)
        val gSuit = runCatching { GenderSuitability.valueOf(entity.genderSuitability) }.getOrDefault(GenderSuitability.ALL)
        val dType = runCatching { DurationType.valueOf(entity.durationType) }.getOrDefault(DurationType.MONTHLY)
        val vStat = runCatching { VerificationStatus.valueOf(entity.verificationStatus) }.getOrDefault(VerificationStatus.VERIFIED)

        val imageDrawables = deserializeStringList(entity.imageDrawableNamesJson).ifEmpty {
            listOf("img_hostel_modern", "img_pg_room", "img_canteen_food")
        }
        val shortFacilities = deserializeStringList(entity.shortFacilitiesJson).ifEmpty {
            listOf("High-Speed Wi-Fi", "Daily Meals", "AC Rooms", "24/7 Security", "Biometric Gate")
        }
        val allAmenities = deserializeStringList(entity.allAmenitiesJson).ifEmpty {
            listOf("High-Speed Wi-Fi", "3-Time Meals Included", "Air Conditioner", "Attached Washroom", "RO Purified Water", "24/7 Power Backup")
        }
        val roomOptions = deserializeRoomOptions(entity.roomOptionsJson).ifEmpty {
            listOf(
                RoomOption("rm-default-1", "Standard Sharing", "2 Sharing", entity.startingPrice, dType, true, true, entity.startingPrice * 0.5, true, 2)
            )
        }
        val rules = deserializeRules(entity.rulesJson).ifEmpty {
            listOf(
                PropertyRule("Gate Timing", "Main gate locks at ${entity.gateClosingTime} for resident safety."),
                PropertyRule("Visitors Policy", "Guests allowed in common lounge during visitor hours."),
                PropertyRule("ID Verification", "Govt ID required at move-in.")
            )
        }

        val quickHours = entity.quickStayHoursCsv.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .ifEmpty { listOf(3, 6, 12, 24) }

        return Property(
            id = entity.id,
            name = entity.name,
            propertyType = pType,
            genderSuitability = gSuit,
            address = entity.address,
            city = entity.city,
            area = entity.area,
            nearbyLandmark = entity.nearbyLandmark,
            distanceKm = entity.distanceKm,
            latitude = entity.latitude,
            longitude = entity.longitude,
            startingPrice = entity.startingPrice,
            durationType = dType,
            rating = entity.rating,
            reviewCount = entity.reviewCount,
            verificationStatus = vStat,
            isAvailable = entity.isAvailable,
            featured = entity.featured,
            imageDrawableNames = imageDrawables,
            shortFacilities = shortFacilities,
            allAmenities = allAmenities,
            roomOptions = roomOptions,
            checkInTime = entity.checkInTime,
            checkOutTime = entity.checkOutTime,
            gateClosingTime = entity.gateClosingTime,
            cancellationPolicy = entity.cancellationPolicy,
            rules = rules,
            ownerInfo = OwnerInfo(
                name = entity.ownerName,
                phone = entity.ownerPhone,
                email = entity.ownerEmail,
                responseTime = entity.ownerResponseTime,
                verifiedHost = entity.ownerVerified,
                joinedDate = entity.ownerJoinedDate
            ),
            hasCanteenMenu = entity.hasCanteenMenu,
            quickStayHours = quickHours
        )
    }

    private fun serializeStringList(list: List<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    private fun deserializeStringList(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            // Fallback comma-separated
            json.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }

    private fun serializeRoomOptions(list: List<RoomOption>): String {
        val array = JSONArray()
        list.forEach { opt ->
            val obj = JSONObject()
            obj.put("id", opt.id)
            obj.put("name", opt.name)
            obj.put("sharingType", opt.sharingType)
            obj.put("price", opt.price)
            obj.put("durationType", opt.durationType.name)
            obj.put("isAc", opt.isAc)
            obj.put("hasAttachedBathroom", opt.hasAttachedBathroom)
            obj.put("securityDeposit", opt.securityDeposit)
            obj.put("isAvailable", opt.isAvailable)
            obj.put("availableBeds", opt.availableBeds)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeRoomOptions(json: String): List<RoomOption> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<RoomOption>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val dTypeStr = obj.optString("durationType", "MONTHLY")
                val dType = runCatching { DurationType.valueOf(dTypeStr) }.getOrDefault(DurationType.MONTHLY)
                list.add(
                    RoomOption(
                        id = obj.optString("id", "rm-$i"),
                        name = obj.optString("name", "Standard Room"),
                        sharingType = obj.optString("sharingType", "2 Sharing"),
                        price = obj.optDouble("price", 5000.0),
                        durationType = dType,
                        isAc = obj.optBoolean("isAc", true),
                        hasAttachedBathroom = obj.optBoolean("hasAttachedBathroom", true),
                        securityDeposit = obj.optDouble("securityDeposit", 2000.0),
                        isAvailable = obj.optBoolean("isAvailable", true),
                        availableBeds = obj.optInt("availableBeds", 2)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeRules(rules: List<PropertyRule>): String {
        val array = JSONArray()
        rules.forEach { rule ->
            val obj = JSONObject()
            obj.put("title", rule.title)
            obj.put("description", rule.description)
            obj.put("iconName", rule.iconName)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeRules(json: String): List<PropertyRule> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<PropertyRule>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PropertyRule(
                        title = obj.optString("title", "Rule"),
                        description = obj.optString("description", ""),
                        iconName = obj.optString("iconName", "info")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
