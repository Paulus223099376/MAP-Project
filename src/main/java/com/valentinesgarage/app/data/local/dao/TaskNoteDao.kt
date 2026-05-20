package com.valentinesgarage.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinesgarage.app.data.local.entity.TaskNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskNoteDao {

    @Query(
        """
        SELECT * FROM task_notes
        WHERE taskId IN (SELECT id FROM repair_tasks WHERE truckId = :truckId)
        ORDER BY createdAt ASC
        """
    )
    fun observeForTruck(truckId: Long): Flow<List<TaskNoteEntity>>

    @Query("SELECT * FROM task_notes WHERE taskId = :taskId ORDER BY createdAt ASC")
    fun observeForTask(taskId: Long): Flow<List<TaskNoteEntity>>

    @Query("SELECT COUNT(*) FROM task_notes")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM task_notes WHERE authorEmployeeId = :employeeId")
    fun observeCountByEmployee(employeeId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: TaskNoteEntity): Long
}
