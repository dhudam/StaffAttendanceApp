package com.vatsalya.staffattendance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vatsalya.staffattendance.data.entity.Employee

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: Employee): Long

    @Update
    suspend fun update(employee: Employee)

    @Delete
    suspend fun delete(employee: Employee)

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): LiveData<List<Employee>>

    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAll(): LiveData<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Long): Employee?

    @Query("SELECT * FROM employees WHERE employeeCode = :code LIMIT 1")
    suspend fun getByCode(code: String): Employee?

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :query || '%' OR employeeCode LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): LiveData<List<Employee>>

    @Query("SELECT COUNT(*) FROM employees WHERE isActive = 1")
    fun getActiveCount(): LiveData<Int>

    @Query("UPDATE employees SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)
}
