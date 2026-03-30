package com.vatsalya.staffattendance.util

import android.content.Context
import com.opencsv.CSVWriter
import com.vatsalya.staffattendance.data.entity.AttendanceRecord
import com.vatsalya.staffattendance.data.entity.Employee
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportAttendance(
        context: Context,
        records: List<AttendanceRecord>,
        employees: Map<Long, Employee>,
        fileName: String = "attendance_${System.currentTimeMillis()}.csv"
    ): File? {
        return try {
            val dir = File(context.getExternalFilesDir(null), "Reports").apply { mkdirs() }
            val file = File(dir, fileName)
            CSVWriter(FileWriter(file)).use { writer ->
                writer.writeNext(arrayOf(
                    "Employee Code", "Name", "Department", "Date",
                    "Check In", "Check Out", "Status", "Working Hours", "Notes"
                ))
                records.forEach { r ->
                    val emp = employees[r.employeeId]
                    writer.writeNext(arrayOf(
                        emp?.employeeCode ?: "",
                        emp?.name ?: "",
                        emp?.department ?: "",
                        r.date,
                        r.checkInTime?.let { ShiftUtils.formatTime(it) } ?: "-",
                        r.checkOutTime?.let { ShiftUtils.formatTime(it) } ?: "-",
                        r.status.name,
                        String.format("%.2f", r.workingHours),
                        r.notes
                    ))
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
