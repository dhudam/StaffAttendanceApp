package com.vatsalya.staffattendance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vatsalya.staffattendance.data.dao.DailyCount
import com.vatsalya.staffattendance.data.db.AppDatabase
import com.vatsalya.staffattendance.data.entity.AttendanceStatus
import com.vatsalya.staffattendance.data.repository.AttendanceRepository
import com.vatsalya.staffattendance.data.repository.EmployeeRepository
import com.vatsalya.staffattendance.data.repository.LeaveRepository
import com.vatsalya.staffattendance.util.ShiftUtils
import kotlinx.coroutines.launch

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    private val attRepo = AttendanceRepository(db.attendanceDao())
    private val empRepo = EmployeeRepository(db.employeeDao())
    private val leaveRepo = LeaveRepository(db.leaveDao())

    val today: String = ShiftUtils.todayDate()

    val totalActive: LiveData<Int> = empRepo.getActiveCount()
    val presentToday: LiveData<Int> = attRepo.getPresentCount(today)
    val lateToday: LiveData<Int> = attRepo.getCountByStatus(today, AttendanceStatus.LATE)
    val pendingLeaves: LiveData<Int> = leaveRepo.getPendingCount()

    private val _weeklyStats = MutableLiveData<List<DailyCount>>()
    val weeklyStats: LiveData<List<DailyCount>> = _weeklyStats

    init { loadWeeklyStats() }

    fun loadWeeklyStats() = viewModelScope.launch {
        _weeklyStats.value = attRepo.getWeeklyStats(ShiftUtils.daysAgo(6))
    }
}
