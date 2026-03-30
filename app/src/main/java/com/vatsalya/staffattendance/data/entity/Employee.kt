package com.vatsalya.staffattendance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EmployeeRole { ADMIN, STAFF }
enum class Shift { MORNING, AFTERNOON, NIGHT, GENERAL }

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val employeeCode: String,
    val department: String,
    val designation: String,
    val shift: Shift = Shift.GENERAL,
    val role: EmployeeRole = EmployeeRole.STAFF,
    val phone: String = "",
    val email: String = "",
    val photoPath: String = "",
    val isActive: Boolean = true,
    val joinDate: Long = System.currentTimeMillis()
)
