package com.vatsalya.staffattendance.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class LeaveType { SICK, CASUAL, EARNED, UNPAID, COMPENSATORY }
enum class LeaveStatus { PENDING, APPROVED, REJECTED, CANCELLED }

@Entity(
    tableName = "leave_requests",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = ["id"],
        childColumns = ["employeeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("employeeId")]
)
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val leaveType: LeaveType = LeaveType.CASUAL,
    val fromDate: String,   // "yyyy-MM-dd"
    val toDate: String,     // "yyyy-MM-dd"
    val reason: String,
    val status: LeaveStatus = LeaveStatus.PENDING,
    val appliedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val reviewNote: String = ""
)
