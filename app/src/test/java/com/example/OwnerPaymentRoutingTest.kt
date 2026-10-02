package com.example

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.OwnerInfo
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.payment.StripePaymentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnerPaymentRoutingTest {

    private fun createTestProperty(
        id: String,
        name: String,
        ownerId: String,
        ownerName: String,
        ownerPhone: String,
        paymentDetails: OwnerPaymentDetails
    ): Property {
        return Property(
            id = id,
            name = name,
            propertyType = PropertyType.PG,
            genderSuitability = GenderSuitability.ALL,
            address = "Test Street",
            city = "Delhi",
            area = "South",
            nearbyLandmark = "Test Metro",
            startingPrice = 5000.0,
            durationType = DurationType.MONTHLY,
            rating = 4.8f,
            reviewCount = 10,
            ownerId = ownerId,
            ownerInfo = OwnerInfo(
                name = ownerName,
                phone = ownerPhone,
                email = "$ownerId@test.com"
            ),
            ownerPaymentDetails = paymentDetails
        )
    }

    private fun resolveActivePaymentTypes(details: OwnerPaymentDetails): List<StripePaymentType> {
        val types = mutableListOf<StripePaymentType>()
        if (details.isUpiConfigured) types.add(StripePaymentType.UPI)
        if (details.isQrConfigured) types.add(StripePaymentType.QR)
        if (details.isBankConfigured) types.add(StripePaymentType.CARD)
        return types
    }

    @Test
    fun testScenario1_OwnerA_UpiIdOnly() {
        val detailsA = OwnerPaymentDetails(
            isUpiIdEnabled = true,
            upiId = "ownerA@okaxis",
            isQrCodeEnabled = false,
            qrCodeImageUrl = null,
            isBankAccountEnabled = false,
            accountNumber = "",
            accountHolderName = "Owner A",
            ifscCode = ""
        )

        assertTrue("UPI ID should be configured", detailsA.isUpiConfigured)
        assertFalse("QR Code should NOT be configured", detailsA.isQrConfigured)
        assertFalse("Bank Account should NOT be configured", detailsA.isBankConfigured)
        assertTrue("Overall setup is complete", detailsA.isConfigured)

        val activeTypes = resolveActivePaymentTypes(detailsA)
        assertEquals("Only 1 payment method should appear", 1, activeTypes.size)
        assertEquals(StripePaymentType.UPI, activeTypes[0])
        assertEquals("ownerA@okaxis", detailsA.upiId)
    }

    @Test
    fun testScenario2_OwnerB_QrCodeOnly() {
        val detailsB = OwnerPaymentDetails(
            isUpiIdEnabled = false,
            upiId = "",
            isQrCodeEnabled = true,
            qrCodeImageUrl = "content://media/external/images/media/999",
            qrCodeVpa = "ownerb.qr@okhdfcbank",
            isBankAccountEnabled = false,
            accountNumber = "",
            accountHolderName = "Owner B",
            ifscCode = ""
        )

        assertFalse("UPI ID should NOT be configured", detailsB.isUpiConfigured)
        assertTrue("QR Code should be configured", detailsB.isQrConfigured)
        assertFalse("Bank Account should NOT be configured", detailsB.isBankConfigured)
        assertTrue("Overall setup is complete", detailsB.isConfigured)

        val activeTypes = resolveActivePaymentTypes(detailsB)
        assertEquals("Only 1 payment method should appear", 1, activeTypes.size)
        assertEquals(StripePaymentType.QR, activeTypes[0])
        assertEquals("content://media/external/images/media/999", detailsB.qrCodeImageUrl)
    }

    @Test
    fun testScenario3_OwnerC_BankAccountOnly() {
        val detailsC = OwnerPaymentDetails(
            isUpiIdEnabled = false,
            upiId = "",
            isQrCodeEnabled = false,
            qrCodeImageUrl = null,
            isBankAccountEnabled = true,
            accountHolderName = "Owner C Enterprises",
            accountNumber = "98765432101234",
            ifscCode = "HDFC0001234",
            bankName = "HDFC Bank",
            accountType = "Current",
            payoutFrequency = "Next Day"
        )

        assertFalse("UPI ID should NOT be configured", detailsC.isUpiConfigured)
        assertFalse("QR Code should NOT be configured", detailsC.isQrConfigured)
        assertTrue("Bank Account should be configured", detailsC.isBankConfigured)
        assertTrue("Overall setup is complete", detailsC.isConfigured)

        val activeTypes = resolveActivePaymentTypes(detailsC)
        assertEquals("Only 1 payment method should appear", 1, activeTypes.size)
        assertEquals(StripePaymentType.CARD, activeTypes[0])
        assertEquals("Owner C Enterprises", detailsC.accountHolderName)
        assertEquals("98765432101234", detailsC.accountNumber)
    }

    @Test
    fun testScenario4_OwnerD_AllThreeMethods() {
        val detailsD = OwnerPaymentDetails(
            isUpiIdEnabled = true,
            upiId = "ownerD@icici",
            isQrCodeEnabled = true,
            qrCodeImageUrl = "content://media/external/images/media/888",
            qrCodeVpa = "ownerd.qr@icici",
            isBankAccountEnabled = true,
            accountHolderName = "Owner D",
            accountNumber = "123456789012",
            ifscCode = "ICIC0000001",
            bankName = "ICICI Bank"
        )

        assertTrue("UPI ID should be configured", detailsD.isUpiConfigured)
        assertTrue("QR Code should be configured", detailsD.isQrConfigured)
        assertTrue("Bank Account should be configured", detailsD.isBankConfigured)
        assertTrue("Overall setup is complete", detailsD.isConfigured)

        val activeTypes = resolveActivePaymentTypes(detailsD)
        assertEquals("All 3 payment methods should appear", 3, activeTypes.size)
        assertTrue(activeTypes.contains(StripePaymentType.UPI))
        assertTrue(activeTypes.contains(StripePaymentType.QR))
        assertTrue(activeTypes.contains(StripePaymentType.CARD))
    }

    @Test
    fun testScenario5_PropertyPaymentIsolation_NoDetailsLeakage() {
        val propA = createTestProperty(
            id = "hotel_a",
            name = "Hotel A Luxury Stay",
            ownerId = "owner_a",
            ownerName = "Rajesh Gupta",
            ownerPhone = "+91 9811111111",
            paymentDetails = OwnerPaymentDetails(
                isUpiIdEnabled = true,
                upiId = "rajesh.hotelA@okaxis",
                isQrCodeEnabled = false,
                isBankAccountEnabled = false,
                accountHolderName = "Rajesh Gupta"
            )
        )

        val propB = createTestProperty(
            id = "hotel_b",
            name = "Hotel B Grand Suite",
            ownerId = "owner_b",
            ownerName = "Sunita Rao",
            ownerPhone = "+91 9822222222",
            paymentDetails = OwnerPaymentDetails(
                isUpiIdEnabled = false,
                isQrCodeEnabled = true,
                qrCodeImageUrl = "file:///data/user/0/com.aistudio/qr_owner_b.jpg",
                qrCodeVpa = "sunita.hotelb@ybl",
                isBankAccountEnabled = true,
                accountHolderName = "Sunita Rao",
                accountNumber = "555544443333",
                ifscCode = "SBIN0001111"
            )
        )

        // Verify Property A strictly holds Owner A's payment details
        val bookedDetailsA = propA.ownerPaymentDetails
        assertEquals("Hotel A must have Owner A's UPI ID", "rajesh.hotelA@okaxis", bookedDetailsA.upiId)
        assertTrue("Hotel A must have UPI configured", bookedDetailsA.isUpiConfigured)
        assertFalse("Hotel A must NOT have QR configured", bookedDetailsA.isQrConfigured)
        assertFalse("Hotel A must NOT have Bank configured", bookedDetailsA.isBankConfigured)

        // Verify Property B details never appear in Property A
        assertNotEquals("Property A must never see Property B's account holder", "Sunita Rao", bookedDetailsA.accountHolderName)
        assertNotEquals("Property A must never see Property B's account number", "555544443333", bookedDetailsA.accountNumber)
        assertNotEquals("Property A must never see Property B's QR code", "file:///data/user/0/com.aistudio/qr_owner_b.jpg", bookedDetailsA.qrCodeImageUrl)

        // Verify Property B strictly holds Owner B's payment details
        val bookedDetailsB = propB.ownerPaymentDetails
        assertFalse("Hotel B must NOT have UPI ID enabled", bookedDetailsB.isUpiConfigured)
        assertTrue("Hotel B must have QR enabled", bookedDetailsB.isQrConfigured)
        assertTrue("Hotel B must have Bank enabled", bookedDetailsB.isBankConfigured)
        assertEquals("Hotel B QR image", "file:///data/user/0/com.aistudio/qr_owner_b.jpg", bookedDetailsB.qrCodeImageUrl)
    }

    @Test
    fun testScenario6_MultiplePropertiesSameOwnerPreservesPaymentProfile() {
        val sharedOwnerDetails = OwnerPaymentDetails(
            isUpiIdEnabled = true,
            upiId = "anand.group@okhdfcbank",
            isQrCodeEnabled = true,
            qrCodeImageUrl = "file:///data/user/0/com.aistudio/qr_owner_anand.jpg",
            qrCodeVpa = "anand.group@okhdfcbank",
            isBankAccountEnabled = false,
            accountHolderName = "Anand Hostels Group"
        )

        val prop1 = createTestProperty(
            id = "anand_pg_1",
            name = "Anand Boys PG 1",
            ownerId = "owner_anand",
            ownerName = "Anand Sharma",
            ownerPhone = "+91 9833333333",
            paymentDetails = sharedOwnerDetails
        )

        val prop2 = createTestProperty(
            id = "anand_pg_2",
            name = "Anand Girls PG 2",
            ownerId = "owner_anand",
            ownerName = "Anand Sharma",
            ownerPhone = "+91 9833333333",
            paymentDetails = sharedOwnerDetails
        )

        // Both properties belong to same owner
        assertEquals(prop1.resolvedOwnerId, prop2.resolvedOwnerId)
        assertEquals("anand.group@okhdfcbank", prop1.ownerPaymentDetails.upiId)
        assertEquals("anand.group@okhdfcbank", prop2.ownerPaymentDetails.upiId)
        assertTrue(prop1.ownerPaymentDetails.isUpiConfigured)
        assertTrue(prop2.ownerPaymentDetails.isUpiConfigured)
        assertTrue(prop1.ownerPaymentDetails.isQrConfigured)
        assertTrue(prop2.ownerPaymentDetails.isQrConfigured)
        assertFalse(prop1.ownerPaymentDetails.isBankConfigured)
        assertFalse(prop2.ownerPaymentDetails.isBankConfigured)
    }

    @Test
    fun testStatusCompletenessRequiresValidDetails() {
        // Empty fields with toggle ON should NOT be considered configured
        val emptyUpiToggleOn = OwnerPaymentDetails(
            isUpiIdEnabled = true,
            upiId = "", // invalid/empty
            isQrCodeEnabled = false,
            isBankAccountEnabled = false
        )
        assertFalse("Empty UPI should not be valid", emptyUpiToggleOn.isUpiConfigured)
        assertFalse("Empty UPI should not be configured", emptyUpiToggleOn.isConfigured)

        val invalidBankToggleOn = OwnerPaymentDetails(
            isUpiIdEnabled = false,
            isQrCodeEnabled = false,
            isBankAccountEnabled = true,
            accountHolderName = "",
            accountNumber = "12", // too short
            ifscCode = "INVALID"
        )
        assertFalse("Invalid Bank should not be valid", invalidBankToggleOn.isBankConfigured)
        assertFalse("Invalid Bank should not be configured", invalidBankToggleOn.isConfigured)

        val emptyQrToggleOn = OwnerPaymentDetails(
            isUpiIdEnabled = false,
            isQrCodeEnabled = true,
            qrCodeImageUrl = null,
            qrCodeVpa = ""
        )
        assertFalse("Empty QR should not be valid", emptyQrToggleOn.isQrConfigured)
        assertFalse("Empty QR should not be configured", emptyQrToggleOn.isConfigured)
    }
}
