package com.example.data.model

import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.PropertyType
import kotlin.math.roundToInt

data class MonthlyPricePoint(
    val monthName: String,           // e.g. "Mar", "Apr", "May"
    val fullMonthName: String,       // e.g. "March 2026"
    val averagePrice: Double,        // e.g. 5200.0
    val occupancyRate: Int,          // e.g. 88%
    val seasonType: SeasonType,      // OFF_PEAK, REGULAR, PEAK, ADMISSION_SURGE
    val note: String                 // e.g. "Lowest price of the year" or "Peak student intake"
)

enum class SeasonType(val label: String, val badgeColorHex: Long) {
    OFF_PEAK("Best Value / Off-Peak", 0xFF10B981),      // Green
    REGULAR("Normal Demand", 0xFF3B82F6),              // Blue
    PEAK("High Demand", 0xFFF59E0B),                   // Amber
    ADMISSION_SURGE("Surge Intake Period", 0xFFEF4444) // Red
}

data class PropertyPriceTrendAnalysis(
    val propertyId: String,
    val basePrice: Double,
    val durationType: DurationType,
    val monthlyHistory: List<MonthlyPricePoint>,
    val currentMonthIndex: Int,
    val lowestHistoricalPrice: Double,
    val highestHistoricalPrice: Double,
    val averagePrice: Double,
    val bestMonthToBook: String,
    val priceChangeVsPeakPct: Int,
    val forecastTrend: String,       // e.g. "Prices expected to rise +12% next quarter"
    val bookingAdvice: String        // e.g. "Great time to book! Rates are 15% lower than the annual average."
)

object PriceTrendProvider {

    fun generateTrendsForProperty(property: Property): PropertyPriceTrendAnalysis {
        val base = property.startingPrice
        val propType = property.propertyType

        // Seasonal multiplier modifiers across 12 months (Jan - Dec)
        // For student hostels/PGs: Peak in July-September (college admissions), dip in Dec-Jan
        // For Hotels/Quick Stays: Peak in Oct-Dec & Mar-May
        val monthlyMultipliers = when (propType) {
            PropertyType.HOSTEL, PropertyType.PG, PropertyType.FLAT -> listOf(
                // Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
                0.90 to (SeasonType.OFF_PEAK to "Semester break discount"),
                0.92 to (SeasonType.OFF_PEAK to "Pre-semester steady rates"),
                0.96 to (SeasonType.REGULAR to "Mid-term occupancy"),
                1.00 to (SeasonType.REGULAR to "Standard rates"),
                1.08 to (SeasonType.PEAK to "Early college bookings open"),
                1.20 to (SeasonType.ADMISSION_SURGE to "Entrance exams & admission rush"),
                1.28 to (SeasonType.ADMISSION_SURGE to "Peak admission season (High surge)"),
                1.30 to (SeasonType.ADMISSION_SURGE to "Max occupancy & premium rates"),
                1.18 to (SeasonType.PEAK to "Late semester intake"),
                1.04 to (SeasonType.REGULAR to "Mid-semester steady demand"),
                0.95 to (SeasonType.OFF_PEAK to "Pre-winter discount slots"),
                0.88 to (SeasonType.OFF_PEAK to "Annual lowest pricing")
            )
            PropertyType.HOTEL, PropertyType.QUICK_STAY -> listOf(
                1.15 to (SeasonType.PEAK to "New Year & holiday surge"),
                1.05 to (SeasonType.REGULAR to "Business travel standard"),
                0.98 to (SeasonType.REGULAR to "Spring rates"),
                0.92 to (SeasonType.OFF_PEAK to "Early summer saver"),
                0.90 to (SeasonType.OFF_PEAK to "Off-peak travel season"),
                0.94 to (SeasonType.OFF_PEAK to "Monsoon special offers"),
                0.96 to (SeasonType.REGULAR to "Standard weekday/weekend mix"),
                1.02 to (SeasonType.REGULAR to "Independence Day travel"),
                1.08 to (SeasonType.PEAK to "Autumn corporate rush"),
                1.22 to (SeasonType.ADMISSION_SURGE to "Festive season (Diwali/Dussehra)"),
                1.25 to (SeasonType.ADMISSION_SURGE to "Wedding & winter holidays"),
                1.30 to (SeasonType.ADMISSION_SURGE to "Year-end peak holiday peak")
            )
            else -> listOf(
                0.92 to (SeasonType.OFF_PEAK to "Early year saver"),
                0.95 to (SeasonType.OFF_PEAK to "Stable demand"),
                1.00 to (SeasonType.REGULAR to "Regular market rate"),
                1.02 to (SeasonType.REGULAR to "Standard rate"),
                1.10 to (SeasonType.PEAK to "Summer relocations"),
                1.18 to (SeasonType.PEAK to "Mid-year corporate shift"),
                1.25 to (SeasonType.ADMISSION_SURGE to "High demand quarter"),
                1.22 to (SeasonType.PEAK to "Sustained high occupancy"),
                1.12 to (SeasonType.REGULAR to "Autumn stability"),
                1.06 to (SeasonType.REGULAR to "Post-monsoon norm"),
                0.98 to (SeasonType.OFF_PEAK to "Winter savings"),
                0.90 to (SeasonType.OFF_PEAK to "Lowest rate of the year")
            )
        }

        val monthLabels = listOf(
            "Jan" to "January", "Feb" to "February", "Mar" to "March",
            "Apr" to "April", "May" to "May", "Jun" to "June",
            "Jul" to "July", "Aug" to "August", "Sep" to "September",
            "Oct" to "October", "Nov" to "November", "Dec" to "December"
        )

        val history = monthlyMultipliers.mapIndexed { index, (multiplier, seasonPair) ->
            val (seasonType, note) = seasonPair
            val (shortMonth, fullMonth) = monthLabels[index]
            val calculatedPrice = (base * multiplier).roundToInt().toDouble()
            val occupancy = when (seasonType) {
                SeasonType.OFF_PEAK -> 60 + (index * 2) % 15
                SeasonType.REGULAR -> 75 + (index * 3) % 12
                SeasonType.PEAK -> 88 + (index * 2) % 7
                SeasonType.ADMISSION_SURGE -> 95 + (index % 5)
            }

            MonthlyPricePoint(
                monthName = shortMonth,
                fullMonthName = "$fullMonth 2026",
                averagePrice = calculatedPrice,
                occupancyRate = occupancy,
                seasonType = seasonType,
                note = note
            )
        }

        val lowest = history.minOf { it.averagePrice }
        val highest = history.maxOf { it.averagePrice }
        val avg = history.map { it.averagePrice }.average()
        val bestMonth = history.minByOrNull { it.averagePrice }?.fullMonthName ?: "December"
        val currentMonthIdx = 7 // August (current context)

        val currentPrice = history.getOrNull(currentMonthIdx)?.averagePrice ?: base
        val discountFromPeak = (((highest - currentPrice) / highest) * 100).roundToInt()

        val advice = if (currentPrice <= avg) {
            "⚡ Great time to book! Current prices are ${((avg - currentPrice) / avg * 100).roundToInt()}% below annual average. Lock in your stay now."
        } else {
            "📈 High demand season currently. Booking in advance protects against last-minute surge of +${discountFromPeak}%."
        }

        val forecast = if (currentMonthIdx in 5..8) {
            "Prices peak during admissions (Jul-Aug) before stabilizing in Oct-Nov."
        } else {
            "Prices tend to drop during off-peak winter months (Nov-Jan)."
        }

        return PropertyPriceTrendAnalysis(
            propertyId = property.id,
            basePrice = base,
            durationType = property.durationType,
            monthlyHistory = history,
            currentMonthIndex = currentMonthIdx,
            lowestHistoricalPrice = lowest,
            highestHistoricalPrice = highest,
            averagePrice = avg,
            bestMonthToBook = bestMonth,
            priceChangeVsPeakPct = discountFromPeak,
            forecastTrend = forecast,
            bookingAdvice = advice
        )
    }
}
