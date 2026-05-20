package com.valentinesgarage.app.data.repository

import com.valentinesgarage.app.data.local.dao.EmployeeDao
import com.valentinesgarage.app.data.local.dao.RepairTaskDao
import com.valentinesgarage.app.data.local.dao.TaskNoteDao
import com.valentinesgarage.app.data.local.entity.RepairTaskEntity
import com.valentinesgarage.app.data.local.entity.TaskNoteEntity
import com.valentinesgarage.app.domain.model.RepairTask
import com.valentinesgarage.app.domain.model.TaskNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Aggregates repair tasks together with their notes and resolves
 * employee names so the UI can render rich activity logs without
 * making N+1 queries.
 */
class RepairTaskRepository(
    private val repairTaskDao: RepairTaskDao,
    private val taskNoteDao: TaskNoteDao,
    private val employeeDao: EmployeeDao,
) {

    fun observeForTruck(truckId: Long): Flow<List<RepairTask>> =
        combine(
            repairTaskDao.observeForTruck(truckId),
            taskNoteDao.observeForTruck(truckId),
        ) { tasks, notes ->
            val notesByTask = notes.groupBy { it.taskId }
            // Pre-fetch involved employees so we can attach names.
            val employeeIds = (tasks.mapNotNull { it.completedByEmployeeId } +
                notes.map { it.authorEmployeeId }).toSet()
            val employees = employeeIds.mapNotNull { employeeDao.findById(it) }
                .associateBy { it.id }

            tasks.map { task ->
                val resolvedNotes = notesByTask[task.id].orEmpty().map { note ->
                    TaskNote(
                        id = note.id,
                        taskId = note.taskId,
                        authorEmployeeId = note.authorEmployeeId,
                        authorEmployeeName = employees[note.authorEmployeeId]?.name ?: "Unknown",
                        message = note.message,
                        createdAt = note.createdAt,
                    )
                }
                RepairTask(
                    id = task.id,
                    truckId = task.truckId,
                    title = task.title,
                    description = task.description,
                    isDone = task.isDone,
                    completedByEmployeeId = task.completedByEmployeeId,
                    completedByEmployeeName = task.completedByEmployeeId?.let { employees[it]?.name },
                    completedAt = task.completedAt,
                    notes = resolvedNotes,
                )
            }
        }

    suspend fun addTask(truckId: Long, title: String, description: String): Long {
        require(title.isNotBlank()) { "Task title cannot be empty" }
        return repairTaskDao.insert(
            RepairTaskEntity(
                truckId = truckId,
                title = title.trim(),
                description = description.trim(),
                isDone = false,
                completedByEmployeeId = null,
                completedAt = null,
            )
        )
    }

    suspend fun toggleTaskDone(taskId: Long, employeeId: Long, nowMillis: Long) {
        val current = repairTaskDao.findById(taskId) ?: return
        val newDone = !current.isDone
        repairTaskDao.update(
            current.copy(
                isDone = newDone,
                completedByEmployeeId = if (newDone) employeeId else null,
                completedAt = if (newDone) nowMillis else null,
            )
        )
    }

    suspend fun addNote(taskId: Long, employeeId: Long, message: String, nowMillis: Long) {
        require(message.isNotBlank()) { "Note message cannot be empty" }
        taskNoteDao.insert(
            TaskNoteEntity(
                taskId = taskId,
                authorEmployeeId = employeeId,
                message = message.trim(),
                createdAt = nowMillis,
            )
        )
    }
}
