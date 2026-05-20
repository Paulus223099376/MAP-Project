package com.valentinesgarage.app.domain.model

/**
 * Aggregated counters shown on the home dashboard. The "my*" fields are
 * the personal slice for the currently signed-in mechanic; the rest are
 * shop-wide and primarily useful for the owner.
 */
data class DashboardStats(
    val totalTrucks: Int,
    val inProgressTrucks: Int,
    val completedTrucks: Int,
    val checkInsToday: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val totalNotes: Int,
    val totalEmployees: Int,
    val recentCheckIns: List<Truck>,
    val myPendingTasksOnFloor: Int,
    val myCompletedTasks: Int,
    val myNotesCount: Int,
    val myCheckIns: Int,
) {
    val completionRate: Float
        get() = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks

    companion object {
        val Empty = DashboardStats(
            totalTrucks = 0,
            inProgressTrucks = 0,
            completedTrucks = 0,
            checkInsToday = 0,
            totalTasks = 0,
            completedTasks = 0,
            pendingTasks = 0,
            totalNotes = 0,
            totalEmployees = 0,
            recentCheckIns = emptyList(),
            myPendingTasksOnFloor = 0,
            myCompletedTasks = 0,
            myNotesCount = 0,
            myCheckIns = 0,
        )
    }
}
