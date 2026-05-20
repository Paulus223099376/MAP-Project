package com.valentinesgarage.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.valentinesgarage.app.data.local.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun observeAll(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id")
    fun observeById(id: Long): Flow<EmployeeEntity?>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun findById(id: Long): EmployeeEntity?

    // Case‑sensitive exact match for username
    @Query("SELECT * FROM employees WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): EmployeeEntity?

    // Email uniqueness remains case‑insensitive (ignoring case)
    @Query("SELECT * FROM employees WHERE LOWER(email) = LOWER(:email) AND email != '' LIMIT 1")
    suspend fun findByEmail(email: String): EmployeeEntity?

    @Query("SELECT COUNT(*) FROM employees")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM employees")
    fun observeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity)
}