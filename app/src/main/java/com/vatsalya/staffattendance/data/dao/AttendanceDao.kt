package com.vatsalya.staffattendance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vatsalya.staffattendance.data.entity.AttendanceRecord
import com.vatsalya.staffattendance.data.entity.AttendanceStatus

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AttendanceRecord): Long

    @Update
    suspend fun update(record: AttendanceRecord)

    @Delete
    suspend fun delete(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY checkInTime ASC")
    fun getByDate(date: String): LiveData<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :empId ORDER BY date DESC")
    fun getByEmployee(empId: Long): LiveData<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :empId AND date = :date LIMIT 1")
    suspend fun getByEmployeeAndDate(empId: Long, date: String): AttendanceRecord?

    @Query("SELECT * FROM attendance_records WHERE date BETWEEN :fromDate AND :toDate ORDER BY date DESC")
    fun getByDateRange(fromDate: String, toDate: String): LiveData<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :empId AND date BETWEEN :fromDate AND :toDate")
    suspend fun getByEmployeeAndRange(empId: Long, fromDate: String, toDate: String): List<AttendanceRecord>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND status = :status")
    fun getCountByDateAndStatus(date: String, status: AttendanceStatus): LiveData<Int>

    @Query("SELECT COUNT(DISTINCT employeeId) FROM attendance_records WHERE date = :date")
    fun getPresentCountForDate(date: String): LiveData<Int>

    // For weekly bar chart: count present per day for last 7 days
    @Query("""
        SELECT date, COUNT(*) as count 
        FROM attendance_records 
        WHERE date >= :fromDate AND status != 'ABSENT' AND status != 'ON_LEAVE'
        GROUP BY date 
        ORDER BY date ASC
    """)
    suspend fun getWeeklyStats(fromDate: String): List<DailyCount>

    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY checkInTime ASC")
    suspend fun getByDateSync(date: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE date BETWEEN :fromDate AND :toDate ORDER BY date DESC")
    suspend fun getByDateRangeSync(fromDate: String, toDate: String): List<AttendanceRecord>
}

data class DailyCount(val date: String, val count: Int)
