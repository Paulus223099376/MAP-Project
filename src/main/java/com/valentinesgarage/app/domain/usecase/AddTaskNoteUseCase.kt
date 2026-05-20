package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.RepairTaskRepository

/**
 * Append a free-form note to a task on behalf of the signed in mechanic.
 */
class AddTaskNoteUseCase(private val repository: RepairTaskRepository) {
    suspend operator fun invoke(taskId: Long, employeeId: Long, message: String, nowMillis: Long) {
        require(employeeId > 0) { "Employee must be signed in to add notes" }
        repository.addNote(taskId, employeeId, message, nowMillis)
    }
}
