package com.vatsalya.staffattendance.data.repository

import androidx.lifecycle.LiveData
import com.vatsalya.staffattendance.data.dao.AttendanceDao
import com.vatsalya.staffattendance.data.dao.DailyCount
import com.vatsalya.staffattendance.data.entity.AttendanceRecord
import com.vatsalya.staffattendance.data.entity.AttendanceStatus

class AttendanceRepository(private val dao: AttendanceDao) {

    fun getByDate(date: String): LiveData<List<AttendanceRecord>> = dao.getByDate(date)
    fun getByEmployee(empId: Long): LiveData<List<AttendanceRecord>> = dao.getByEmployee(empId)
    fun getByDateRange(from: String, to: String) = dao.getByDateRange(from, to)
    fun getPresentCount(date: String) = dao.getPresentCountForDate(date)
    fun getCountByStatus(date: String, status: AttendanceStatus) =
        dao.getCountByDateAndStatus(date, status)

    suspend fun getByEmployeeAndDate(empId: Long, date: String) =
        dao.getByEmployeeAndDate(empId, date)

    suspend fun insert(record: AttendanceRecord) = dao.insert(record)
    suspend fun update(record: AttendanceRecord) = dao.update(record)
    suspend fun delete(record: AttendanceRecord) = dao.delete(record)
    suspend fun getWeeklyStats(fromDate: String): List<DailyCount> = dao.getWeeklyStats(fromDate)
    suspend fun getByDateSync(date: String) = dao.getByDateSync(date)
    suspend fun getByDateRangeSync(from: String, to: String) = dao.getByDateRangeSync(from, to)
    suspend fun getByEmployeeAndRange(empId: Long, from: String, to: String) =
        dao.getByEmployeeAndRange(empId, from, to)
}
