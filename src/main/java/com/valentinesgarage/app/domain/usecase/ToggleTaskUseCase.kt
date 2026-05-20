package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.RepairTaskRepository

/**
 * Flip a task between done/not-done. Recording who toggled it and when
 * is what allows mechanics to collaborate without duplicating work.
 */
class ToggleTaskUseCase(private val repository: RepairTaskRepository) {
    suspend operator fun invoke(taskId: Long, employeeId: Long, nowMillis: Long) {
        require(employeeId > 0) { "Employee must be signed in to update tasks" }
        repository.toggleTaskDone(taskId, employeeId, nowMillis)
    }
}
