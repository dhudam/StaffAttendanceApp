package com.vatsalya.staffattendance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vatsalya.staffattendance.data.entity.LeaveRequest
import com.vatsalya.staffattendance.data.entity.LeaveStatus

@Dao
interface LeaveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(leave: LeaveRequest): Long

    @Update
    suspend fun update(leave: LeaveRequest)

    @Delete
    suspend fun delete(leave: LeaveRequest)

    @Query("SELECT * FROM leave_requests ORDER BY appliedAt DESC")
    fun getAll(): LiveData<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE employeeId = :empId ORDER BY appliedAt DESC")
    fun getByEmployee(empId: Long): LiveData<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE status = :status ORDER BY appliedAt DESC")
    fun getByStatus(status: LeaveStatus): LiveData<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LeaveRequest?

    // Check if employee is on approved leave for a given date
    @Query("""
        SELECT COUNT(*) FROM leave_requests 
        WHERE employeeId = :empId 
        AND status = 'APPROVED' 
        AND :date BETWEEN fromDate AND toDate
    """)
    suspend fun isOnLeave(empId: Long, date: String): Int

    @Query("SELECT COUNT(*) FROM leave_requests WHERE status = 'PENDING'")
    fun getPendingCount(): LiveData<Int>
}
