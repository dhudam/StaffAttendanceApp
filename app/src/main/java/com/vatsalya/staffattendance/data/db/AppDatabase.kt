package com.vatsalya.staffattendance.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vatsalya.staffattendance.data.dao.AttendanceDao
import com.vatsalya.staffattendance.data.dao.EmployeeDao
import com.vatsalya.staffattendance.data.dao.LeaveDao
import com.vatsalya.staffattendance.data.entity.AttendanceRecord
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.data.entity.LeaveRequest

@Database(
    entities = [Employee::class, AttendanceRecord::class, LeaveRequest::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveDao(): LeaveDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "staff_attendance_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
