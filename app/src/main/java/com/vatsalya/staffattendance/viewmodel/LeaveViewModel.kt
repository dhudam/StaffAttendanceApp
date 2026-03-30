package com.vatsalya.staffattendance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vatsalya.staffattendance.data.db.AppDatabase
import com.vatsalya.staffattendance.data.entity.LeaveRequest
import com.vatsalya.staffattendance.data.entity.LeaveStatus
import com.vatsalya.staffattendance.data.repository.LeaveRepository
import kotlinx.coroutines.launch

class LeaveViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = LeaveRepository(AppDatabase.getInstance(app).leaveDao())

    val allLeaves: LiveData<List<LeaveRequest>> = repo.getAll()
    val pendingLeaves: LiveData<List<LeaveRequest>> = repo.getByStatus(LeaveStatus.PENDING)
    val pendingCount: LiveData<Int> = repo.getPendingCount()

    fun applyLeave(leave: LeaveRequest) = viewModelScope.launch { repo.insert(leave) }

    fun updateStatus(id: Long, status: LeaveStatus, note: String = "") = viewModelScope.launch {
        val leave = repo.getById(id) ?: return@launch
        repo.update(leave.copy(
            status = status,
            reviewedAt = System.currentTimeMillis(),
            reviewNote = note
        ))
    }
}
