package com.example.data.payment

import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.Property

data class OwnerPayoutSummary(
    val totalGrossVolume: Double,
    val stynoCommissionRate: Double = 0.05,
    val totalStynoCommission: Double,
    val netHostEarnings: Double,
    val pendingPayoutsAmount: Double,
    val completedPayoutsAmount: Double,
    val totalBookingsCount: Int,
    val pendingBookingsCount: Int,
    val completedBookingsCount: Int,
    val gatewayStatus: String = "Not Connected (Direct Host Settlement Mode)",
    val isGatewayConnected: Boolean = false
)

data class OwnerTransactionRecord(
    val bookingId: String,
    val propertyId: String,
    val propertyName: String,
    val guestName: String,
    val roomOrSlotName: String,
    val durationText: String,
    val bookedTimestamp: Long,
    val checkInDate: String,
    val grossAmount: Double,
    val stynoCommissionRate: Double = 0.05,
    val stynoCommissionAmount: Double,
    val netHostPayoutAmount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val payoutStatus: String,
    val gatewayConnectionNote: String,
    val ownerDestinationAccount: String
)

object OwnerPayoutCalculator {
    const val STYNO_COMMISSION_PERCENT = 5.0
    const val STYNO_COMMISSION_RATE = 0.05

    /**
     * Accurately calculates host earnings, platform commission, and transaction history
     * strictly for the owner's properties. Ensures no cross-routing between different owners.
     */
    fun calculate(
        ownerProperties: List<Property>,
        allBookings: List<Booking>,
        selectedPropertyId: String? = null
    ): Pair<OwnerPayoutSummary, List<OwnerTransactionRecord>> {
        val ownerPropMap = ownerProperties.associateBy { it.id }
        val targetPropIds = if (selectedPropertyId != null && selectedPropertyId.isNotBlank() && selectedPropertyId != "all") {
            setOf(selectedPropertyId)
        } else {
            ownerPropMap.keys
        }

        // Strictly filter bookings associated with this owner's properties - never route other owners' bookings
        val ownerBookings = allBookings.filter { it.propertyId in targetPropIds }

        var totalGross = 0.0
        var totalCommission = 0.0
        var netEarnings = 0.0
        var pendingAmount = 0.0
        var completedAmount = 0.0
        var pendingCount = 0
        var completedCount = 0

        val transactions = ownerBookings.sortedByDescending { it.bookedAtTimestamp }.map { booking ->
            val prop = ownerPropMap[booking.propertyId]
            val propName = prop?.name ?: "Verified Styno Property"
            val gross = booking.finalAmount
            val commission = gross * STYNO_COMMISSION_RATE
            val net = (gross - commission).coerceAtLeast(0.0)

            val isCompleted = booking.status == BookingStatus.COMPLETED
            val isCancelled = booking.status == BookingStatus.CANCELLED

            if (!isCancelled) {
                totalGross += gross
                totalCommission += commission
                netEarnings += net
                if (isCompleted) {
                    completedAmount += net
                    completedCount++
                } else {
                    pendingAmount += net
                    pendingCount++
                }
            }

            val maskedDest = prop?.ownerPaymentDetails?.let { details ->
                if (details.upiId.isNotBlank()) "UPI: ${details.upiId}"
                else if (details.bankName.isNotBlank()) "${details.bankName} (${details.maskedAccountNumber})"
                else "Configured Payout Account"
            } ?: "Configured Payout Account"

            OwnerTransactionRecord(
                bookingId = booking.id,
                propertyId = booking.propertyId,
                propertyName = propName,
                guestName = booking.guestName.ifBlank { "Verified Guest" },
                roomOrSlotName = booking.roomTypeName.ifBlank { "Standard Room" },
                durationText = booking.durationText.ifBlank { "Standard Stay" },
                bookedTimestamp = booking.bookedAtTimestamp,
                checkInDate = booking.checkInDate,
                grossAmount = gross,
                stynoCommissionRate = STYNO_COMMISSION_RATE,
                stynoCommissionAmount = commission,
                netHostPayoutAmount = net,
                paymentMethod = booking.paymentMethod,
                paymentStatus = booking.paymentStatus,
                payoutStatus = when {
                    isCancelled -> "Cancelled & Refunded"
                    isCompleted -> "Disbursed to Host Account"
                    else -> "Pending Check-In Verification"
                },
                gatewayConnectionNote = "Payment Gateway: Not Connected (Direct Host Escrow)",
                ownerDestinationAccount = maskedDest
            )
        }

        val summary = OwnerPayoutSummary(
            totalGrossVolume = totalGross,
            stynoCommissionRate = STYNO_COMMISSION_RATE,
            totalStynoCommission = totalCommission,
            netHostEarnings = netEarnings,
            pendingPayoutsAmount = pendingAmount,
            completedPayoutsAmount = completedAmount,
            totalBookingsCount = ownerBookings.size,
            pendingBookingsCount = pendingCount,
            completedBookingsCount = completedCount,
            gatewayStatus = "Not Connected (Direct Host Settlement Mode)",
            isGatewayConnected = false
        )

        return Pair(summary, transactions)
    }
}
