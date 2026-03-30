package com.vatsalya.staffattendance.data.repository

import androidx.lifecycle.LiveData
import com.vatsalya.staffattendance.data.dao.LeaveDao
import com.vatsalya.staffattendance.data.entity.LeaveRequest
import com.vatsalya.staffattendance.data.entity.LeaveStatus

class LeaveRepository(private val dao: LeaveDao) {
    fun getAll(): LiveData<List<LeaveRequest>> = dao.getAll()
    fun getByEmployee(empId: Long): LiveData<List<LeaveRequest>> = dao.getByEmployee(empId)
    fun getByStatus(status: LeaveStatus): LiveData<List<LeaveRequest>> = dao.getByStatus(status)
    fun getPendingCount(): LiveData<Int> = dao.getPendingCount()
    suspend fun getById(id: Long): LeaveRequest? = dao.getById(id)
    suspend fun isOnLeave(empId: Long, date: String): Boolean = dao.isOnLeave(empId, date) > 0
    suspend fun insert(leave: LeaveRequest): Long = dao.insert(leave)
    suspend fun update(leave: LeaveRequest) = dao.update(leave)
    suspend fun delete(leave: LeaveRequest) = dao.delete(leave)
}
