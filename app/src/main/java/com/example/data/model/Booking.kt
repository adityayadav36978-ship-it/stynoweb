package com.example.data.model

enum class BookingStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    ACTIVE("Active / Checked-In"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class Booking(
    val id: String,                         // e.g. "STY-2026-8942"
    val propertyId: String,
    val propertyName: String,
    val propertyType: PropertyType,
    val propertyImage: String,
    val address: String,
    val roomTypeName: String,              // e.g. "2-Sharing AC Room"
    val checkInDate: String,
    val checkInTime: String = "12:00 PM",
    val checkOutDate: String,
    val durationText: String,              // e.g. "1 Month" or "6 Hours" or "3 Days"
    val guestName: String,
    val guestPhone: String,
    val guestEmail: String,
    val guestIdProofType: String = "Aadhaar Card",
    val basePrice: Double,
    val serviceFee: Double = 149.0,
    val taxes: Double = 0.0,
    val discount: Double = 0.0,
    val finalAmount: Double,
    val securityDeposit: Double = 0.0,
    val digitalPasscode: String = "STY-9941",
    val status: BookingStatus = BookingStatus.UPCOMING,
    val paymentMethod: String = "UPI (Google Pay)",
    val paymentStatus: String = "Paid Online (Verified)",
    val transactionId: String = "txn_stripe_" + (100000..999999).random(),
    val paymentGateway: String = "Stripe",
    val bookedAtTimestamp: Long = System.currentTimeMillis(),
    val propertyContactPhone: String = "+91 98112 34567",
    val ownerUpiId: String = "sharma.stays@icici",
    val ownerAccountHolderName: String = "Vikram Sharma (Host)",
    val ownerBankName: String = "HDFC Bank",
    val ownerPayoutStatus: String = "Credited to Host Account"
)
