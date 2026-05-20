package com.valentinesgarage.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.valentinesgarage.app.data.local.entity.RepairTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairTaskDao {

    @Query("SELECT * FROM repair_tasks WHERE truckId = :truckId ORDER BY isDone ASC, id ASC")
    fun observeForTruck(truckId: Long): Flow<List<RepairTaskEntity>>

    @Query("SELECT * FROM repair_tasks WHERE id = :id")
    suspend fun findById(id: Long): RepairTaskEntity?

    @Query("SELECT COUNT(*) FROM repair_tasks")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM repair_tasks WHERE isDone = 1")
    fun observeDoneCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM repair_tasks WHERE isDone = 0")
    fun observePendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM repair_tasks WHERE isDone = 1 AND completedByEmployeeId = :employeeId")
    fun observeDoneByEmployee(employeeId: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM repair_tasks
        WHERE isDone = 0
        AND truckId IN (SELECT id FROM trucks WHERE isCompleted = 0)
        """
    )
    fun observePendingOnFloor(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: RepairTaskEntity): Long

    @Update
    suspend fun update(task: RepairTaskEntity)

    @Delete
    suspend fun delete(task: RepairTaskEntity)
}
