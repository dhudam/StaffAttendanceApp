package com.vatsalya.staffattendance.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AttendanceStatus { PRESENT, LATE, EARLY_DEPARTURE, ABSENT, ON_LEAVE, HALF_DAY }

@Entity(
    tableName = "attendance_records",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = ["id"],
        childColumns = ["employeeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("employeeId"), Index("date")]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val date: String,               // "yyyy-MM-dd"
    val checkInTime: Long? = null,  // epoch millis
    val checkOutTime: Long? = null, // epoch millis
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val photoPath: String = "",     // selfie photo path
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String = "",
    val workingHours: Float = 0f
)
