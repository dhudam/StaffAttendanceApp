package com.vatsalya.staffattendance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vatsalya.staffattendance.data.db.AppDatabase
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.data.repository.EmployeeRepository
import kotlinx.coroutines.launch

class EmployeeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = EmployeeRepository(AppDatabase.getInstance(app).employeeDao())

    val allEmployees: LiveData<List<Employee>> = repo.getAll()
    val activeCount: LiveData<Int> = repo.getActiveCount()

    fun search(query: String): LiveData<List<Employee>> = repo.search(query)

    fun addEmployee(emp: Employee) = viewModelScope.launch { repo.insert(emp) }
    fun updateEmployee(emp: Employee) = viewModelScope.launch { repo.update(emp) }
    fun deactivate(id: Long) = viewModelScope.launch { repo.deactivate(id) }
}
