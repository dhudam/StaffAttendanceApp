package com.vatsalya.staffattendance.util

import com.vatsalya.staffattendance.data.entity.Shift
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ShiftUtils {

    data class ShiftWindow(val startHour: Int, val startMin: Int, val endHour: Int, val endMin: Int, val graceMinutes: Int = 15)

    fun getWindow(shift: Shift): ShiftWindow = when (shift) {
        Shift.MORNING    -> ShiftWindow( 7,  0, 15,  0, 15)
        Shift.AFTERNOON  -> ShiftWindow(14,  0, 22,  0, 15)
        Shift.NIGHT      -> ShiftWindow(22,  0,  6,  0, 15)
        Shift.GENERAL    -> ShiftWindow( 9,  0, 18,  0, 15)
    }

    fun isLate(shift: Shift, checkInMillis: Long): Boolean {
        val w = getWindow(shift)
        val cal = Calendar.getInstance().apply { timeInMillis = checkInMillis }
        val checkIn = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val shiftStart = w.startHour * 60 + w.startMin
        return checkIn > shiftStart + w.graceMinutes
    }

    fun isEarlyDeparture(shift: Shift, checkOutMillis: Long): Boolean {
        val w = getWindow(shift)
        val cal = Calendar.getInstance().apply { timeInMillis = checkOutMillis }
        val checkOut = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val shiftEnd = w.endHour * 60 + w.endMin
        return checkOut < shiftEnd - 15
    }

    fun calcWorkingHours(checkIn: Long, checkOut: Long): Float {
        val diff = checkOut - checkIn
        return diff / (1000f * 60 * 60)
    }

    fun todayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun formatTime(millis: Long): String =
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(millis))

    fun formatDate(millis: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))

    fun daysAgo(n: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -n)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
}
