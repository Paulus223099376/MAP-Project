package com.valentinesgarage.app.domain.model

/**
 * A single repair/service item attached to a truck. Mechanics tick these
 * off collaboratively so nothing is missed because two people both
 * assumed the other already did it.
 */
data class RepairTask(
    val id: Long = 0,
    val truckId: Long,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val completedByEmployeeId: Long?,
    val completedByEmployeeName: String?,
    val completedAt: Long?,
    val notes: List<TaskNote>,
)

/**
 * A free-form note any mechanic can attach to a task to record progress
 * or hand-offs between shifts.
 */
data class TaskNote(
    val id: Long = 0,
    val taskId: Long,
    val authorEmployeeId: Long,
    val authorEmployeeName: String,
    val message: String,
    val createdAt: Long,
)
