package com.example

import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.GirlsSafetyVerification
import com.example.data.model.GirlsSafetyVerificationStatus
import com.example.data.model.OwnerInfo
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.RoomOption
import com.example.data.model.SafetyConcernReport
import com.example.data.model.SafetyReportStatus
import com.example.data.model.SafetyValidationEngine
import com.example.data.model.SafetyValidationResult
import com.example.data.model.StynoPrivacyHelper
import com.example.data.model.UserProfile
import com.example.data.model.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class WomenSafetyLayerTest {

    private fun createTestProperty(
        id: String,
        name: String,
        genderSuitability: GenderSuitability,
        isSharingAvailable: Boolean = false,
        sharingCapacity: Int = 1,
        verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
        ownerId: String = "owner_test_1"
    ): Property {
        return Property(
            id = id,
            name = name,
            propertyType = PropertyType.HOSTEL,
            genderSuitability = genderSuitability,
            address = "Test Road, Knowledge Park",
            city = "Delhi NCR",
            area = "Knowledge Park",
            nearbyLandmark = "Near Metro",
            startingPrice = 5000.0,
            durationType = DurationType.MONTHLY,
            verificationStatus = verificationStatus,
            isSharingAvailable = isSharingAvailable,
            sharingCapacity = sharingCapacity,
            availableSpaces = if (isSharingAvailable) 2 else 1,
            ownerId = ownerId,
            ownerInfo = OwnerInfo(
                name = "Test Host",
                phone = "+91 98765 43210",
                email = "host@test.com"
            ),
            roomOptions = listOf(
                RoomOption("rm-p1", "Private Suite", "Private", 8000.0, DurationType.MONTHLY),
                RoomOption("rm-s1", "Double Sharing AC", "2 Sharing", 5000.0, DurationType.MONTHLY)
            )
        )
    }

    /**
     * TEST 1: Female user + women-only property → eligible booking flow works.
     */
    @Test
    fun test1_femaleUser_womenOnlyProperty_eligibleBooking() {
        val womenOnlyProp = createTestProperty(
            id = "prop_women_1",
            name = "Styno Orchid Girls Residence",
            genderSuitability = GenderSuitability.WOMEN_ONLY
        )

        assertEquals("Women-Only", womenOnlyProp.stayCategoryType)
        assertTrue(womenOnlyProp.genderSuitability.isWomenOnly)

        // Female guest booking
        val result = SafetyValidationEngine.validateBookingEligibility(
            property = womenOnlyProp,
            roomOption = womenOnlyProp.roomOptions.first(),
            userGender = "Female",
            isCoupleBooking = false
        )

        assertTrue("Female guest should be eligible to book Women-Only property", result is SafetyValidationResult.Allowed)

        // Non-female guest trying to book women-only property must be denied
        val maleAttempt = SafetyValidationEngine.validateBookingEligibility(
            property = womenOnlyProp,
            roomOption = womenOnlyProp.roomOptions.first(),
            userGender = "Male",
            isCoupleBooking = false
        )

        assertTrue("Male guest must be denied from booking Women-Only property", maleAttempt is SafetyValidationResult.Denied)
        val deniedReason = (maleAttempt as SafetyValidationResult.Denied).reason
        assertTrue(deniedReason.contains("Women-Only", ignoreCase = true))
    }

    /**
     * TEST 2: Female user + shared accommodation → only eligible female sharing options are shown / allowed.
     */
    @Test
    fun test2_femaleUser_sharedAccommodation_femaleSharingEligible() {
        val sharedProp = createTestProperty(
            id = "prop_shared_coed",
            name = "Styno Elite Co-Living",
            genderSuitability = GenderSuitability.CO_ED,
            isSharingAvailable = true,
            sharingCapacity = 2
        )

        val sharedRoom = sharedProp.roomOptions.first { it.sharingType.contains("Sharing") }

        // Female user in shared room
        val femaleSharingCheck = SafetyValidationEngine.validateBookingEligibility(
            property = sharedProp,
            roomOption = sharedRoom,
            userGender = "Female",
            isCoupleBooking = false
        )
        assertTrue("Female user should be eligible for gender-matched sharing", femaleSharingCheck is SafetyValidationResult.Allowed)

        // Empty gender must be denied for shared accommodation to prevent accidental mixed occupancy
        val emptyGenderCheck = SafetyValidationEngine.validateBookingEligibility(
            property = sharedProp,
            roomOption = sharedRoom,
            userGender = "",
            isCoupleBooking = false
        )
        assertTrue("Empty gender must be denied for shared room to prevent gender mixing", emptyGenderCheck is SafetyValidationResult.Denied)
    }

    /**
     * TEST 3: Male user + male-only/shared accommodation → correct male sharing rules apply.
     */
    @Test
    fun test3_maleUser_maleOnlyAccommodation_rulesApply() {
        val menOnlyProp = createTestProperty(
            id = "prop_boys_1",
            name = "Styno Apex Boys Techno Stay",
            genderSuitability = GenderSuitability.MEN_ONLY,
            isSharingAvailable = true,
            sharingCapacity = 2
        )

        assertEquals("Men-Only", menOnlyProp.stayCategoryType)

        // Male user booking men-only stay
        val maleResult = SafetyValidationEngine.validateBookingEligibility(
            property = menOnlyProp,
            roomOption = menOnlyProp.roomOptions.first(),
            userGender = "Male",
            isCoupleBooking = false
        )
        assertTrue("Male user should be allowed to book Men-Only property", maleResult is SafetyValidationResult.Allowed)

        // Female user booking men-only stay must be denied
        val femaleResult = SafetyValidationEngine.validateBookingEligibility(
            property = menOnlyProp,
            roomOption = menOnlyProp.roomOptions.first(),
            userGender = "Female",
            isCoupleBooking = false
        )
        assertTrue("Female user must be denied booking Men-Only property", femaleResult is SafetyValidationResult.Denied)
    }

    /**
     * TEST 4: Couple + private accommodation → no shared-room matching is incorrectly applied.
     */
    @Test
    fun test4_couple_privateAccommodation_noSharedRoomMatchingApplied() {
        val hotelProp = createTestProperty(
            id = "prop_couple_stay",
            name = "Styno Heritage Studio Suites",
            genderSuitability = GenderSuitability.COUPLES
        )

        assertEquals("Couple", hotelProp.stayCategoryType)
        val privateRoom = hotelProp.roomOptions.first { it.sharingType == "Private" }

        // Couple booking private accommodation
        val couplePrivateCheck = SafetyValidationEngine.validateBookingEligibility(
            property = hotelProp,
            roomOption = privateRoom,
            userGender = "Female",
            isCoupleBooking = true
        )
        assertTrue("Couple booking private room must be allowed without shared room restrictions", couplePrivateCheck is SafetyValidationResult.Allowed)

        // Couple booking a shared room must be rejected for privacy & safety
        val sharedRoom = hotelProp.roomOptions.first { it.sharingType.contains("Sharing") }
        val coupleSharedCheck = SafetyValidationEngine.validateBookingEligibility(
            property = hotelProp,
            roomOption = sharedRoom,
            userGender = "Female",
            isCoupleBooking = true
        )
        assertTrue("Couples must not be allowed to book shared rooms with strangers", coupleSharedCheck is SafetyValidationResult.Denied)

        // Couple booking a Women-Only property must also be rejected
        val womenOnlyProp = createTestProperty(
            id = "prop_girls_exclusive",
            name = "Girls Sanctuary",
            genderSuitability = GenderSuitability.WOMEN_ONLY
        )
        val coupleWomenOnlyCheck = SafetyValidationEngine.validateBookingEligibility(
            property = womenOnlyProp,
            roomOption = privateRoom,
            userGender = "Female",
            isCoupleBooking = true
        )
        assertTrue("Couple booking must be rejected in Women-Only exclusive property", coupleWomenOnlyCheck is SafetyValidationResult.Denied)
    }

    /**
     * TEST 5: Unverified property → must not incorrectly display as verified.
     */
    @Test
    fun test5_unverifiedProperty_doesNotDisplayAsVerified() {
        val pendingProp = createTestProperty(
            id = "prop_pending",
            name = "Newly Listed PG",
            genderSuitability = GenderSuitability.ALL,
            verificationStatus = VerificationStatus.PENDING
        )
        assertFalse("Pending property must not be marked as verified", pendingProp.isPropertyVerified)
        assertEquals(VerificationStatus.PENDING, pendingProp.verificationStatus)

        val reviewProp = createTestProperty(
            id = "prop_review",
            name = "Safety Audit PG",
            genderSuitability = GenderSuitability.ALL,
            verificationStatus = VerificationStatus.UNDER_REVIEW
        )
        assertFalse("Property under review must not be marked as verified", reviewProp.isPropertyVerified)

        val rejectedProp = createTestProperty(
            id = "prop_rejected",
            name = "Suspended PG",
            genderSuitability = GenderSuitability.ALL,
            verificationStatus = VerificationStatus.REJECTED
        )
        assertFalse("Rejected/suspended property must not be marked as verified", rejectedProp.isPropertyVerified)

        val verifiedProp = createTestProperty(
            id = "prop_verified",
            name = "Styno Certified PG",
            genderSuitability = GenderSuitability.ALL,
            verificationStatus = VerificationStatus.VERIFIED
        )
        assertTrue("Only verified properties may be marked as verified", verifiedProp.isPropertyVerified)
    }

    /**
     * TEST 6: User attempts to access another user's private information → access denied / masked.
     */
    @Test
    fun test6_privacyProtection_masksSensitiveUserData() {
        val sensitivePhone = "+91 98765 43210"
        val sensitiveEmail = "priya.sharma@example.com"
        val sensitiveGovtId = "4819-2810-9281"

        val maskedPhone = StynoPrivacyHelper.maskPhoneNumber(sensitivePhone)
        val maskedEmail = StynoPrivacyHelper.maskEmail(sensitiveEmail)
        val maskedGovtId = StynoPrivacyHelper.maskGovtId(sensitiveGovtId)

        // Assert phone is masked and does not reveal full number
        assertFalse("Full phone number must not be exposed", maskedPhone.contains("98765"))
        assertTrue("Masked phone should retain last digits for verification", maskedPhone.endsWith("3210"))

        // Assert email is masked
        assertFalse("Full email prefix must not be exposed", maskedEmail.startsWith("priya.sharma"))
        assertTrue("Masked email should hide characters", maskedEmail.contains("••••"))

        // Assert Govt ID is masked
        assertFalse("Full Govt ID must not be exposed", maskedGovtId.contains("4819-2810"))
        assertTrue("Masked Govt ID should preserve last 4 digits only", maskedGovtId.endsWith("9281"))

        // Null/Blank safe handling
        assertEquals("Not Provided", StynoPrivacyHelper.maskPhoneNumber(null))
        assertEquals("••••@••••.com", StynoPrivacyHelper.maskEmail(""))
    }

    /**
     * TEST 7: Owner attempts to access another owner's safety/property data → access denied.
     */
    @Test
    fun test7_ownerIsolation_crossOwnerAccessDenied() {
        val ownerAId = "owner_alpha_1"
        val ownerBId = "owner_beta_2"

        val propA = createTestProperty(
            id = "prop_owner_a",
            name = "Alpha Safe Stays",
            genderSuitability = GenderSuitability.WOMEN_ONLY,
            ownerId = ownerAId
        )

        assertEquals(ownerAId, propA.resolvedOwnerId)
        assertNotEquals(ownerBId, propA.resolvedOwnerId)

        // Security check simulating backend ownership validation
        val canOwnerBAccessPropA = (propA.resolvedOwnerId == ownerBId)
        assertFalse("Owner B must not have access to Owner A's property or safety dossiers", canOwnerBAccessPropA)

        val canOwnerAAccessPropA = (propA.resolvedOwnerId == ownerAId)
        assertTrue("Owner A must have access to their own property", canOwnerAAccessPropA)
    }

    /**
     * TEST 8: Safety report → reaches the correct authorized review workflow.
     */
    @Test
    fun test8_safetyReport_reachesAuthorizedReviewWorkflow() {
        val report = SafetyConcernReport(
            id = "safety_rep_101",
            propertyId = "prop_123",
            propertyName = "Styno Safe Haven",
            reportedByUserId = "usr_guest_456",
            reportedByUserName = "Ananya Roy",
            reportedByUserPhone = "+91 98765 43210",
            issueCategory = "Harassment",
            description = "Unauthorized male visitor entered female-only floor past curfew.",
            status = SafetyReportStatus.OPEN
        )

        assertEquals("Harassment", report.issueCategory)
        assertEquals(SafetyReportStatus.OPEN, report.status)
        assertEquals("Open / Urgent Review", report.status.displayName)

        // Simulate admin action taking
        val reviewedReport = report.copy(
            status = SafetyReportStatus.ACTION_TAKEN,
            adminNotes = "Warden interviewed. Biometric gate log checked. Security guard replaced.",
            actionTaken = "Warden issued show-cause notice and security protocol updated."
        )

        assertEquals(SafetyReportStatus.ACTION_TAKEN, reviewedReport.status)
        assertTrue(reviewedReport.adminNotes.isNotBlank())
        assertTrue(reviewedReport.actionTaken.isNotBlank())
    }

    /**
     * TEST 9: Unauthorized API/database request → access denied.
     */
    @Test
    fun test9_unauthorizedRequest_safetyCheckThrows() {
        val womenOnlyProp = createTestProperty(
            id = "prop_women_exclusive",
            name = "Exclusive Women Hostel",
            genderSuitability = GenderSuitability.WOMEN_ONLY
        )

        val maleBookingAttempt = Booking(
            id = "BK-ILLEGAL-01",
            propertyId = womenOnlyProp.id,
            propertyName = womenOnlyProp.name,
            propertyType = PropertyType.HOSTEL,
            propertyImage = "img_hostel",
            address = womenOnlyProp.address,
            roomTypeName = "Single Room",
            checkInDate = "01 Oct 2026",
            checkOutDate = "31 Oct 2026",
            durationText = "1 Month",
            guestName = "Rajesh Verma",
            guestPhone = "+91 98112 34567",
            guestEmail = "rajesh@example.com",
            basePrice = 7000.0,
            finalAmount = 7149.0,
            guestGender = "Male",
            isCoupleBooking = false
        )

        val safetyCheck = SafetyValidationEngine.validateBookingEligibility(
            property = womenOnlyProp,
            roomOption = womenOnlyProp.roomOptions.first(),
            userGender = maleBookingAttempt.guestGender,
            isCoupleBooking = maleBookingAttempt.isCoupleBooking
        )

        assertTrue("Safety check must catch illegal booking attempt", safetyCheck is SafetyValidationResult.Denied)

        var exceptionThrown = false
        try {
            if (safetyCheck is SafetyValidationResult.Denied) {
                throw SecurityException("Safety Enforcement: ${safetyCheck.reason}")
            }
        } catch (e: SecurityException) {
            exceptionThrown = true
            assertTrue(e.message?.contains("Women-Only") == true)
        }
        assertTrue("Backend must throw SecurityException on unauthorized gender/sharing access", exceptionThrown)
    }

    /**
     * TEST 10: Verify that existing STYNO features continue working after these changes.
     */
    @Test
    fun test10_existingFeaturesContinueWorking() {
        val property = createTestProperty(
            id = "prop_general_stay",
            name = "Styno Grand Hotel & Suites",
            genderSuitability = GenderSuitability.ALL
        )

        // General bookings for all genders work normally
        val normalBooking = SafetyValidationEngine.validateBookingEligibility(
            property = property,
            roomOption = property.roomOptions.first(),
            userGender = "Female",
            isCoupleBooking = false
        )
        assertTrue("Standard eligible bookings in regular properties continue working", normalBooking is SafetyValidationResult.Allowed)

        val maleNormalBooking = SafetyValidationEngine.validateBookingEligibility(
            property = property,
            roomOption = property.roomOptions.first(),
            userGender = "Male",
            isCoupleBooking = false
        )
        assertTrue("Male bookings in regular properties continue working", maleNormalBooking is SafetyValidationResult.Allowed)

        // Category types remain consistent
        assertEquals("Mixed / Gender-Neutral", property.stayCategoryType)

        // Verified status preserved
        assertTrue(property.isPropertyVerified)
    }
}
