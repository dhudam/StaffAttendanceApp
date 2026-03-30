package com.vatsalya.staffattendance.data.repository

import androidx.lifecycle.LiveData
import com.vatsalya.staffattendance.data.dao.EmployeeDao
import com.vatsalya.staffattendance.data.entity.Employee

class EmployeeRepository(private val dao: EmployeeDao) {
    fun getAllActive(): LiveData<List<Employee>> = dao.getAllActive()
    fun getAll(): LiveData<List<Employee>> = dao.getAll()
    fun search(query: String): LiveData<List<Employee>> = dao.search(query)
    fun getActiveCount(): LiveData<Int> = dao.getActiveCount()
    suspend fun getById(id: Long): Employee? = dao.getById(id)
    suspend fun getByCode(code: String): Employee? = dao.getByCode(code)
    suspend fun insert(employee: Employee): Long = dao.insert(employee)
    suspend fun update(employee: Employee) = dao.update(employee)
    suspend fun delete(employee: Employee) = dao.delete(employee)
    suspend fun deactivate(id: Long) = dao.deactivate(id)
}
