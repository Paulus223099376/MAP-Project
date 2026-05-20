package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.local.dao.RepairTaskDao
import com.valentinesgarage.app.data.local.dao.TaskNoteDao
import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.data.repository.TruckRepository
import com.valentinesgarage.app.domain.model.DashboardStats
import com.valentinesgarage.app.util.TimeProvider
import com.valentinesgarage.app.util.startOfTodayMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Streams a snapshot of everything the dashboard cares about. We push
 * the per-counter SQL down to Room so the UI stays smooth even with a
 * lot of trucks/tasks.
 */
class GetDashboardStatsUseCase(
    private val truckRepository: TruckRepository,
    private val employeeRepository: EmployeeRepository,
    private val repairTaskDao: RepairTaskDao,
    private val taskNoteDao: TaskNoteDao,
    private val timeProvider: TimeProvider,
) {

    @Suppress("LongMethod")
    fun observe(employeeId: Long): Flow<DashboardStats> {
        val sinceToday = startOfTodayMillis(timeProvider.now())

        val shopWide = combine(
            truckRepository.observeCount(),
            truckRepository.observeInProgressCount(),
            truckRepository.observeCompletedCount(),
            truckRepository.observeCheckInsSince(sinceToday),
            employeeRepository.observeCount(),
        ) { values ->
            ShopCounts(
                totalTrucks = values[0],
                inProgressTrucks = values[1],
                completedTrucks = values[2],
                checkInsToday = values[3],
                totalEmployees = values[4],
            )
        }

        val taskCounts = combine(
            repairTaskDao.observeTotalCount(),
            repairTaskDao.observeDoneCount(),
            repairTaskDao.observePendingCount(),
            repairTaskDao.observePendingOnFloor(),
            taskNoteDao.observeTotalCount(),
        ) { values ->
            TaskCounts(
                totalTasks = values[0],
                completedTasks = values[1],
                pendingTasks = values[2],
                pendingOnFloor = values[3],
                totalNotes = values[4],
            )
        }

        val mine = combine(
            repairTaskDao.observeDoneByEmployee(employeeId),
            taskNoteDao.observeCountByEmployee(employeeId),
            truckRepository.observeCheckInsByEmployee(employeeId),
        ) { done, notes, checkIns ->
            MyCounts(done = done, notes = notes, checkIns = checkIns)
        }

        val recent = truckRepository.observeRecent(limit = 5)

        return combine(shopWide, taskCounts, mine, recent) { shop, task, my, recentTrucks ->
            DashboardStats(
                totalTrucks = shop.totalTrucks,
                inProgressTrucks = shop.inProgressTrucks,
                completedTrucks = shop.completedTrucks,
                checkInsToday = shop.checkInsToday,
                totalTasks = task.totalTasks,
                completedTasks = task.completedTasks,
                pendingTasks = task.pendingTasks,
                totalNotes = task.totalNotes,
                totalEmployees = shop.totalEmployees,
                recentCheckIns = recentTrucks,
                myPendingTasksOnFloor = task.pendingOnFloor,
                myCompletedTasks = my.done,
                myNotesCount = my.notes,
                myCheckIns = my.checkIns,
            )
        }
    }

    private data class ShopCounts(
        val totalTrucks: Int,
        val inProgressTrucks: Int,
        val completedTrucks: Int,
        val checkInsToday: Int,
        val totalEmployees: Int,
    )

    private data class TaskCounts(
        val totalTasks: Int,
        val completedTasks: Int,
        val pendingTasks: Int,
        val pendingOnFloor: Int,
        val totalNotes: Int,
    )

    private data class MyCounts(
        val done: Int,
        val notes: Int,
        val checkIns: Int,
    )
}
