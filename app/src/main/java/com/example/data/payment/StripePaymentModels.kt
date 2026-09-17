package com.example.data.payment

enum class PaymentGatewayOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val isRecommended: Boolean = false
) {
    STRIPE_CARD(
        id = "stripe_card",
        title = "Credit / Debit Card (Stripe SDK)",
        subtitle = "Visa, Mastercard, RuPay, Amex • 256-bit Encrypted",
        isRecommended = true
    ),
    UPI_INTENT(
        id = "upi_intent",
        title = "UPI (Google Pay / PhonePe / Paytm)",
        subtitle = "Instant UPI transfer via secure gateway"
    ),
    NET_BANKING(
        id = "net_banking",
        title = "Net Banking (All Major Banks)",
        subtitle = "HDFC, ICICI, SBI, Axis, Kotak & more"
    ),
    PAY_TOKEN(
        id = "pay_token",
        title = "Pay Token Online (Balance on Check-In)",
        subtitle = "Reserve room with initial safety deposit"
    )
}

enum class CardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    RUPAY("RuPay"),
    AMEX("American Express"),
    DISCOVER("Discover"),
    UNKNOWN("Card")
}

data class StripeCardInput(
    val cardNumber: String = "",
    val cardholderName: String = "",
    val expiryMonthYear: String = "",
    val cvc: String = "",
    val saveCard: Boolean = true
) {
    val cleanCardNumber: String get() = cardNumber.replace(" ", "").replace("-", "")
    
    val brand: CardBrand get() {
        val clean = cleanCardNumber
        return when {
            clean.startsWith("4") -> CardBrand.VISA
            clean.startsWith("51") || clean.startsWith("52") || clean.startsWith("53") || clean.startsWith("54") || clean.startsWith("55") || clean.startsWith("22") -> CardBrand.MASTERCARD
            clean.startsWith("60") || clean.startsWith("6521") || clean.startsWith("6522") || clean.startsWith("508") -> CardBrand.RUPAY
            clean.startsWith("34") || clean.startsWith("37") -> CardBrand.AMEX
            clean.startsWith("6011") || clean.startsWith("65") -> CardBrand.DISCOVER
            else -> CardBrand.UNKNOWN
        }
    }

    val isValidCardNumber: Boolean get() {
        val clean = cleanCardNumber
        if (clean.length < 15 || clean.length > 19) return false
        // Luhn Algorithm validation
        var sum = 0
        var alternate = false
        for (i in clean.length - 1 downTo 0) {
            var n = clean[i].digitToIntOrNull() ?: return false
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }

    val expiryMonth: Int? get() {
        val parts = expiryMonthYear.split("/")
        if (parts.isNotEmpty()) {
            return parts[0].trim().toIntOrNull()
        }
        return null
    }

    val expiryYear: Int? get() {
        val parts = expiryMonthYear.split("/")
        if (parts.size >= 2) {
            val yr = parts[1].trim().toIntOrNull() ?: return null
            return if (yr < 100) 2000 + yr else yr
        }
        return null
    }

    val isValidExpiry: Boolean get() {
        val m = expiryMonth ?: return false
        val y = expiryYear ?: return false
        if (m !in 1..12) return false
        if (y < 2026) return false
        return true
    }

    val isValidCvc: Boolean get() = cvc.length in 3..4 && cvc.all { it.isDigit() }

    val isValid: Boolean get() = isValidCardNumber && isValidExpiry && isValidCvc && cardholderName.trim().isNotBlank()
}

data class StripeTestCard(
    val title: String,
    val number: String,
    val expiry: String,
    val cvc: String,
    val name: String,
    val description: String,
    val requires3ds: Boolean = false,
    val willDecline: Boolean = false
)

enum class StripePaymentStage {
    IDLE,
    CREATING_INTENT,
    TOKENIZING_CARD,
    AUTHENTICATING_3DS,
    CONFIRMING_WITH_STRIPE,
    PROCESSING_UPI_REQUEST,
    WAITING_UPI_APPROVAL,
    VERIFYING_QR_PAYMENT,
    SUCCESS,
    FAILED
}

enum class StripePaymentType(val label: String, val subtitle: String) {
    CARD("Card", "Credit & Debit"),
    UPI("UPI", "GPay / PhonePe / ID"),
    QR("QR Code", "Scan & Pay"),
    NET_BANKING("Net Banking", "All Banks")
}

data class StripePaymentResult(
    val isSuccess: Boolean,
    val transactionId: String = "",
    val paymentIntentId: String = "",
    val chargeId: String = "",
    val amountPaid: Double = 0.0,
    val currency: String = "INR",
    val paymentMethodSummary: String = "",
    val cardLast4: String = "",
    val cardBrand: String = "",
    val upiId: String = "",
    val authCode: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    val clientSecret: String = "",
    val receiptNumber: String = ""
)
