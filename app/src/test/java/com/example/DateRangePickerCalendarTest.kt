package com.example

import com.example.ui.components.DateRangeState
import com.example.ui.components.DateRangeTarget
import com.example.ui.components.StayDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateRangePickerCalendarTest {

    @Test
    fun stayDate_comparisonAndOrdering_isAccurate() {
        val date1 = StayDate(2026, 8, 28)
        val date2 = StayDate(2026, 9, 2)
        val date3 = StayDate(2026, 8, 28)
        val pastDate = StayDate(2026, 8, 20)

        assertTrue(date1.isBefore(date2))
        assertTrue(date2.isAfter(date1))
        assertTrue(date1.isOnOrAfter(date3))
        assertTrue(date1.isOnOrBefore(date3))
        assertTrue(pastDate.isBefore(date1))
    }

    @Test
    fun stayDate_daysBetween_calculatesNightsCorrectly() {
        val checkIn = StayDate(2026, 8, 28)
        val checkOut = StayDate(2026, 9, 2) // Aug 28, 29, 30, 31, Sep 1 -> Sep 2 is 5 nights

        val nights = StayDate.daysBetween(checkIn, checkOut)
        assertEquals(5, nights)
    }

    @Test
    fun stayDate_plusDays_handlesMonthTransitionsCorrectly() {
        val aug28 = StayDate(2026, 8, 28)
        val sep2 = aug28.plusDays(5)

        assertEquals(2026, sep2.year)
        assertEquals(9, sep2.month)
        assertEquals(2, sep2.day)
    }

    @Test
    fun stayDate_parsing_supportsMultipleFormats() {
        val parsed1 = StayDate.parse("28 Aug 2026")
        assertNotNull(parsed1)
        assertEquals(2026, parsed1!!.year)
        assertEquals(8, parsed1.month)
        assertEquals(28, parsed1.day)

        val parsed2 = StayDate.parse("2026-09-15")
        assertNotNull(parsed2)
        assertEquals(2026, parsed2!!.year)
        assertEquals(9, parsed2.month)
        assertEquals(15, parsed2.day)
    }

    @Test
    fun stayDate_toCalendar_roundtripWorks() {
        val original = StayDate(2026, 10, 15)
        val cal = original.toCalendar()
        val recovered = StayDate.fromCalendar(cal)

        assertEquals(original.year, recovered.year)
        assertEquals(original.month, recovered.month)
        assertEquals(original.day, recovered.day)
    }

    @Test
    fun dateRangeState_durationMetricsAndSummary_areAccurate() {
        val checkIn = StayDate(2026, 8, 28)
        val checkOut = StayDate(2026, 9, 4) // 7 nights = 1 week

        val state = DateRangeState(
            checkInDate = checkIn,
            checkOutDate = checkOut,
            activeTarget = DateRangeTarget.CHECK_OUT
        )

        assertTrue(state.isCompleteRange)
        assertEquals(7, state.durationNights)
        assertEquals("7 Nights", state.durationText)
    }

    @Test
    fun dateRangeState_singleNightStay_formatsGrammaticallyCorrect() {
        val checkIn = StayDate(2026, 8, 28)
        val checkOut = StayDate(2026, 8, 29) // 1 night

        val state = DateRangeState(checkInDate = checkIn, checkOutDate = checkOut)
        assertEquals(1, state.durationNights)
        assertEquals("1 Night", state.durationText)
    }
}
