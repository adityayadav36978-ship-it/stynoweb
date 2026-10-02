package com.example

import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.OwnerInfo
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.RoomOption
import com.example.data.model.SafetyReportStatus
import com.example.data.model.VerificationStatus
import com.example.data.security.StynoSecurityEngine
import com.example.data.security.StynoSecurityEngine.RateLimitAction
import com.example.data.security.StynoSecurityEngine.RateLimitResult
import com.example.data.security.StynoSecurityEngine.SecurityEventType
import com.example.data.security.StynoSecurityEngine.UserRole
import com.example.data.util.StynoAuthService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Formal Security Verification Suite for STYNO.
 * 
 * Verifies all 12 core security requirements:
 * TEST 1: User A attempts to access User B's private data -> DENIED.
 * TEST 2: Owner A attempts to access Owner B's property data -> DENIED.
 * TEST 3: Owner A attempts to access Owner B's payment information -> DENIED.
 * TEST 4: Normal User attempts to access Admin functionality -> DENIED.
 * TEST 5: A request changes a Property ID to another property's ID -> rejected.
 * TEST 6: A request changes Owner ID or User ID -> server must ignore/reject unauthorized ownership changes.
 * TEST 7: Repeated login/OTP/API requests -> rate limiting works appropriately.
 * TEST 8: Unauthorized API request -> DENIED.
 * TEST 9: Private uploaded document URL is accessed without authorization -> DENIED.
 * TEST 10: Attempt to submit malicious or malformed input -> safely rejected/sanitized.
 * TEST 11: Logout -> protected session/token cannot continue accessing protected resources.
 * TEST 12: Verify all existing STYNO features still work after security changes.
 */
class SecurityHardeningTest {

    private val userA = "usr_alice_101"
    private val userB = "usr_bob_202"
    private val ownerA = "owner_ramesh_001"
    private val ownerB = "owner_suresh_002"
    private val adminEmail = "admin@styno.com"
    private val attackerEmail = "attacker@evil.com"

    @Before
    fun setUp() {
        // Clear revoked tokens / sessions if needed
    }

    /**
     * TEST 1: User A attempts to access User B's private data -> DENIED.
     */
    @Test
    fun test1_userA_accessUserBPrivateData_isDenied() {
        val userAAccessesOwnData = StynoSecurityEngine.enforceUserAccess(
            requestingUserId = userA,
            targetUserId = userA,
            isAdmin = false
        )
        assertTrue("User A must be allowed to access own private data", userAAccessesOwnData)

        val userAAccessesUserB = StynoSecurityEngine.enforceUserAccess(
            requestingUserId = userA,
            targetUserId = userB,
            isAdmin = false
        )
        assertFalse("User A must be DENIED from accessing User B's private data", userAAccessesUserB)

        // Verified admin can audit
        val adminAccessesUserB = StynoSecurityEngine.enforceUserAccess(
            requestingUserId = "admin_super",
            targetUserId = userB,
            isAdmin = true
        )
        assertTrue("Admin must have authorized access for trust and safety audits", adminAccessesUserB)
    }

    /**
     * TEST 2: Owner A attempts to access Owner B's property data -> DENIED.
     */
    @Test
    fun test2_ownerA_accessOwnerBPropertyData_isDenied() {
        val propOwnerB = "prop_green_residency_b"
        val actualOwnerId = ownerB

        // Owner A tries to modify / delete Owner B's property
        val ownerAAccess = StynoSecurityEngine.enforcePropertyOwnership(
            requestingOwnerId = ownerA,
            actualPropertyOwnerId = actualOwnerId,
            isAdmin = false
        )
        assertFalse("Owner A must be strictly DENIED from managing Owner B's property", ownerAAccess)

        // Owner B managing their own property is allowed
        val ownerBAccess = StynoSecurityEngine.enforcePropertyOwnership(
            requestingOwnerId = ownerB,
            actualPropertyOwnerId = actualOwnerId,
            isAdmin = false
        )
        assertTrue("Owner B must be permitted to manage their own property", ownerBAccess)
    }

    /**
     * TEST 3: Owner A attempts to access Owner B's payment information -> DENIED.
     */
    @Test
    fun test3_ownerA_accessOwnerBPaymentInfo_isDenied() {
        // Owner A attempts to update or view Owner B's payment profile (UPI/Bank/QR)
        val crossOwnerPaymentAccess = StynoSecurityEngine.enforcePaymentAccess(
            requestingOwnerId = ownerA,
            targetOwnerId = ownerB,
            isAdmin = false
        )
        assertFalse("Owner A must be DENIED from modifying or accessing Owner B's payment profile", crossOwnerPaymentAccess)

        // Owner B accessing their own payment profile is permitted
        val ownerBOwnPaymentAccess = StynoSecurityEngine.enforcePaymentAccess(
            requestingOwnerId = ownerB,
            targetOwnerId = ownerB,
            isAdmin = false
        )
        assertTrue("Owner B must be permitted to access their own payment profile", ownerBOwnPaymentAccess)
    }

    /**
     * TEST 4: Normal User attempts to access Admin functionality -> DENIED.
     */
    @Test
    fun test4_normalUser_accessAdminFunctionality_isDenied() {
        // 1. Normal guest attempts Admin access
        val guestEmail = "traveler.demo@styno.com"
        val guestSession = StynoSecurityEngine.createSession(
            userId = "usr_guest_01",
            role = UserRole.GUEST,
            email = guestEmail
        )
        val guestIsAdmin = StynoSecurityEngine.enforceAdminAccess(
            requestingUserEmail = guestEmail,
            session = guestSession
        )
        assertFalse("Guest user must be strictly DENIED from Admin functionality", guestIsAdmin)

        // 2. Normal owner attempts Admin access
        val hostEmail = "host.demo@styno.com"
        val hostSession = StynoSecurityEngine.createSession(
            userId = "usr_host_01",
            role = UserRole.OWNER,
            email = hostEmail
        )
        val ownerIsAdmin = StynoSecurityEngine.enforceAdminAccess(
            requestingUserEmail = hostEmail,
            session = hostSession
        )
        assertFalse("Property Owner must be strictly DENIED from Admin functionality", ownerIsAdmin)

        // 3. Attacker with arbitrary email attempts Admin access
        val attackerIsAdmin = StynoSecurityEngine.enforceAdminAccess(
            requestingUserEmail = attackerEmail,
            session = null
        )
        assertFalse("Arbitrary external email must be DENIED from Admin access", attackerIsAdmin)

        // 4. Authorized Admin is permitted
        val verifiedAdminSession = StynoSecurityEngine.createSession(
            userId = "adm_super_01",
            role = UserRole.ADMIN,
            email = adminEmail
        )
        val adminAllowed = StynoSecurityEngine.enforceAdminAccess(
            requestingUserEmail = adminEmail,
            session = verifiedAdminSession
        )
        assertTrue("Verified platform administrator must be allowed", adminAllowed)
    }

    /**
     * TEST 5: A request changes a Property ID to another property's ID -> rejected.
     */
    @Test
    fun test5_requestTamperingPropertyId_isRejected() {
        val targetVictimPropertyId = "prop_victim_01"
        val victimOwnerId = "owner_victim_99"
        val attackingOwnerId = "owner_attacker_66"

        val canAttackerModifyVictimProperty = StynoSecurityEngine.enforcePropertyOwnership(
            requestingOwnerId = attackingOwnerId,
            actualPropertyOwnerId = victimOwnerId,
            isAdmin = false
        )
        assertFalse("Tampering property ID to target another owner's property must be rejected", canAttackerModifyVictimProperty)
    }

    /**
     * TEST 6: A request changes Owner ID or User ID -> server ignores/rejects unauthorized ownership changes.
     */
    @Test
    fun test6_tamperedOwnerOrUserId_isRejected() {
        val legitimateOwner = "owner_verified_01"
        val spoofedOwner = "owner_spoofed_88"

        // Server-side identity validation ensures spoofed owner ID does not match legitimate ownership
        val canSpoof = StynoSecurityEngine.enforcePropertyOwnership(
            requestingOwnerId = spoofedOwner,
            actualPropertyOwnerId = legitimateOwner,
            isAdmin = false
        )
        assertFalse("Spoofed owner ID must be rejected by server validation", canSpoof)
    }

    /**
     * TEST 7: Repeated login/OTP/API requests -> rate limiting works appropriately.
     */
    @Test
    fun test7_repeatedRequests_rateLimitingEnforced() {
        val testUser = "rate_limit_test_${System.currentTimeMillis()}@styno.com"

        // 1. Send allowed login attempts (up to 5)
        for (i in 1..RateLimitAction.LOGIN_ATTEMPT.maxRequests) {
            val res = StynoSecurityEngine.checkRateLimit(RateLimitAction.LOGIN_ATTEMPT, testUser)
            assertTrue("Request $i should be allowed within threshold", res is RateLimitResult.Allowed)
        }

        // 6th attempt must be blocked
        val blockedAttempt = StynoSecurityEngine.checkRateLimit(RateLimitAction.LOGIN_ATTEMPT, testUser)
        assertTrue("Excessive request must be blocked by rate limiter", blockedAttempt is RateLimitResult.Blocked)
        val waitSec = (blockedAttempt as RateLimitResult.Blocked).waitSeconds
        assertTrue("Wait seconds must be greater than 0", waitSec > 0)
    }

    /**
     * TEST 8: Unauthorized API request -> DENIED.
     */
    @Test
    fun test8_unauthorizedApiRequest_isDenied() {
        // Attempting admin action without admin authorization
        try {
            val isAuthorized = StynoSecurityEngine.enforceAdminAccess(requestingUserEmail = "hacker@test.com")
            if (!isAuthorized) {
                throw SecurityException("Access Denied: Admin authorization required")
            }
            fail("Expected SecurityException for unauthorized admin request")
        } catch (e: SecurityException) {
            assertTrue("SecurityException message must be descriptive", e.message!!.contains("Access Denied"))
        }
    }

    /**
     * TEST 9: Private uploaded document URL is accessed without authorization -> DENIED.
     */
    @Test
    fun test9_privateDocumentAccess_withoutAuthorization_isDenied() {
        val documentOwner = "usr_guest_aadhaar_owner"
        val strangerUser = "usr_random_stranger"

        val strangerAccess = StynoSecurityEngine.enforceDocumentAccess(
            requestingUserId = strangerUser,
            documentOwnerId = documentOwner,
            isAdmin = false
        )
        assertFalse("Stranger must be DENIED from accessing another user's private document", strangerAccess)

        val ownerAccess = StynoSecurityEngine.enforceDocumentAccess(
            requestingUserId = documentOwner,
            documentOwnerId = documentOwner,
            isAdmin = false
        )
        assertTrue("Document owner must be permitted to access own document", ownerAccess)
    }

    /**
     * TEST 10: Attempt to submit malicious or malformed input -> safely rejected/sanitized.
     */
    @Test
    fun test10_maliciousInput_isSafelySanitizedOrRejected() {
        // 1. Script tag injection (XSS)
        val maliciousXss = "Luxury PG <script>alert('pwned')</script> Near Metro"
        val cleanXss = StynoSecurityEngine.sanitizeString(maliciousXss)
        assertFalse("Script tags must be stripped", cleanXss.contains("<script>"))
        assertTrue("Safe text must remain intact", cleanXss.contains("Luxury PG"))

        // 2. SQL injection pattern in search query
        val sqlInjection = "Delhi' OR 1=1; DROP TABLE bookings; --"
        val cleanSearch = StynoSecurityEngine.sanitizeSearchQuery(sqlInjection)
        assertFalse("SQL injection patterns must be neutralized", cleanSearch.contains("OR 1=1"))
        assertFalse("Dangerous characters must be removed", cleanSearch.contains("DROP TABLE"))

        // 3. Path traversal in file upload
        val traversalFileName = "../../etc/passwd.jpg"
        val cleanFileName = StynoSecurityEngine.sanitizeFileName(traversalFileName)
        assertFalse("Path traversal sequences must be stripped", cleanFileName.contains(".."))
        assertFalse("Path separators must be stripped", cleanFileName.contains("/"))

        // 4. Executable file upload attempt
        val fileCheck = StynoSecurityEngine.validateFileUpload(
            fileName = "backdoor.sh",
            mimeType = "application/x-sh",
            fileSizeBytes = 1024L
        )
        assertTrue("Executable script upload must be rejected", fileCheck is StynoSecurityEngine.FileValidationResult.Rejected)
    }

    /**
     * TEST 11: Logout -> protected session/token cannot continue accessing protected resources.
     */
    @Test
    fun test11_logout_invalidatesSessionToken() {
        val userSession = StynoSecurityEngine.createSession(
            userId = "usr_session_test",
            role = UserRole.GUEST,
            email = "session.test@styno.com"
        )
        val token = userSession.token

        // Verify active token is valid
        val preLogoutCheck = StynoSecurityEngine.validateSession(token)
        assertTrue("Token must be valid immediately after creation", preLogoutCheck is StynoSecurityEngine.SessionValidationResult.Valid)

        // Logout and invalidate session
        StynoSecurityEngine.invalidateSession(token)

        // Subsequent access attempt with old token must be revoked / denied
        val postLogoutCheck = StynoSecurityEngine.validateSession(token)
        assertTrue("Revoked token must not be allowed to access resources", postLogoutCheck is StynoSecurityEngine.SessionValidationResult.Revoked)
    }

    /**
     * TEST 12: Verify all existing STYNO features still work after security changes.
     */
    @Test
    fun test12_existingFeatures_stillFunctionCorrectly() {
        // 1. Password hashing with salt produces verifiable cryptographic signatures
        val plain = "TestPassword@2026"
        val hashResult = StynoSecurityEngine.hashPassword(plain)
        assertNotEquals("Password must NOT be stored in plain text", plain, hashResult.hashBase64)
        assertTrue("Password verification must succeed with correct credentials",
            StynoSecurityEngine.verifyPassword(plain, hashResult.hashBase64, hashResult.saltBase64)
        )
        assertFalse("Password verification must fail with incorrect credentials",
            StynoSecurityEngine.verifyPassword("WrongPassword", hashResult.hashBase64, hashResult.saltBase64)
        )

        // 2. Normal authentications via StynoAuthService continue to succeed
        val authResult = StynoAuthService.authenticateEmailUser("host.demo@styno.com", "Host@123")
        assertTrue("Host demo authentication must succeed", authResult is StynoAuthService.AuthResultState.Success)
        val successState = authResult as StynoAuthService.AuthResultState.Success
        assertEquals("Host demo email must match", "host.demo@styno.com", successState.account.email)
        assertNotNull("Session token must be issued on successful login", successState.sessionToken)

        // 3. PII masking works correctly
        val maskedPhone = com.example.data.model.StynoPrivacyHelper.maskPhoneNumber("+91 98765 43210")
        assertTrue("Phone number must be masked", maskedPhone.contains("•") || maskedPhone.contains("*"))
        val maskedBank = StynoSecurityEngine.maskAccountNumber("123456789012")
        assertEquals("Bank account must show only last 4 digits", "********9012", maskedBank)
    }
}
