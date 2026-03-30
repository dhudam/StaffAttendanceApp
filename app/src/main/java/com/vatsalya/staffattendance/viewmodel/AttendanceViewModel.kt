package com.vatsalya.staffattendance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vatsalya.staffattendance.data.db.AppDatabase
import com.vatsalya.staffattendance.data.entity.AttendanceRecord
import com.vatsalya.staffattendance.data.entity.AttendanceStatus
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.data.repository.AttendanceRepository
import com.vatsalya.staffattendance.data.repository.EmployeeRepository
import com.vatsalya.staffattendance.util.ShiftUtils
import kotlinx.coroutines.launch

class AttendanceViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    private val attRepo = AttendanceRepository(db.attendanceDao())
    private val empRepo = EmployeeRepository(db.employeeDao())

    val allEmployees: LiveData<List<Employee>> = empRepo.getAllActive()

    private val _currentRecord = MutableLiveData<AttendanceRecord?>()
    val currentRecord: LiveData<AttendanceRecord?> = _currentRecord

    private val _markResult = MutableLiveData<String>()
    val markResult: LiveData<String> = _markResult

    val todayRecords = attRepo.getByDate(ShiftUtils.todayDate())

    fun loadRecord(empId: Long) = viewModelScope.launch {
        _currentRecord.value = attRepo.getByEmployeeAndDate(empId, ShiftUtils.todayDate())
    }

    fun checkIn(
        employee: Employee,
        photoPath: String = "",
        latitude: Double? = null,
        longitude: Double? = null
    ) = viewModelScope.launch {
        val today = ShiftUtils.todayDate()
        val existing = attRepo.getByEmployeeAndDate(employee.id, today)
        if (existing != null) {
            _markResult.value = "Already checked in today"
            return@launch
        }
        val now = System.currentTimeMillis()
        val late = ShiftUtils.isLate(employee.shift, now)
        val status = if (late) AttendanceStatus.LATE else AttendanceStatus.PRESENT
        val record = AttendanceRecord(
            employeeId = employee.id,
            date = today,
            checkInTime = now,
            status = status,
            photoPath = photoPath,
            latitude = latitude,
            longitude = longitude
        )
        attRepo.insert(record)
        _markResult.value = if (late) "Checked in (Late)" else "Checked in successfully"
        _currentRecord.value = attRepo.getByEmployeeAndDate(employee.id, today)
    }

    fun checkOut(employee: Employee) = viewModelScope.launch {
        val today = ShiftUtils.todayDate()
        val record = attRepo.getByEmployeeAndDate(employee.id, today)
        if (record == null) {
            _markResult.value = "No check-in found for today"
            return@launch
        }
        if (record.checkOutTime != null) {
            _markResult.value = "Already checked out today"
            return@launch
        }
        val now = System.currentTimeMillis()
        val earlyOut = ShiftUtils.isEarlyDeparture(employee.shift, now)
        val hours = ShiftUtils.calcWorkingHours(record.checkInTime ?: now, now)
        val updatedStatus = when {
            earlyOut && record.status == AttendanceStatus.LATE -> AttendanceStatus.HALF_DAY
            earlyOut -> AttendanceStatus.EARLY_DEPARTURE
            else -> record.status
        }
        attRepo.update(record.copy(
            checkOutTime = now,
            status = updatedStatus,
            workingHours = hours
        ))
        _markResult.value = if (earlyOut) "Checked out (Early)" else "Checked out successfully"
        _currentRecord.value = null
    }
}
