package com.valentinesgarage.app.di

import android.content.Context
import com.valentinesgarage.app.data.local.AppDatabase
import com.valentinesgarage.app.data.local.DatabaseSeeder
import com.valentinesgarage.app.data.local.SessionStore
import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.data.repository.RepairTaskRepository
import com.valentinesgarage.app.data.repository.TruckRepository
import com.valentinesgarage.app.domain.usecase.AddTaskNoteUseCase
import com.valentinesgarage.app.domain.usecase.ChangePasswordUseCase
import com.valentinesgarage.app.domain.usecase.CheckInTruckUseCase
import com.valentinesgarage.app.domain.usecase.GetDashboardStatsUseCase
import com.valentinesgarage.app.domain.usecase.GetReportsUseCase
import com.valentinesgarage.app.domain.usecase.LoginUseCase
import com.valentinesgarage.app.domain.usecase.RegisterUseCase
import com.valentinesgarage.app.domain.usecase.ToggleTaskUseCase
import com.valentinesgarage.app.util.SystemTimeProvider
import com.valentinesgarage.app.util.TimeProvider

/**
 * Tiny manual DI container. We deliberately avoid Hilt/Dagger here to
 * keep the project compileable without extra annotation processors and
 * to make the dependency graph trivially auditable for the assignment
 * presentation.
 */
class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val timeProvider: TimeProvider = SystemTimeProvider

    val sessionStore: SessionStore = SessionStore(context.applicationContext)

    val employeeRepository: EmployeeRepository = EmployeeRepository(database.employeeDao())
    val truckRepository: TruckRepository = TruckRepository(database.truckDao())
    val repairTaskRepository: RepairTaskRepository = RepairTaskRepository(
        repairTaskDao = database.repairTaskDao(),
        taskNoteDao = database.taskNoteDao(),
        employeeDao = database.employeeDao(),
    )

    val checkInTruckUseCase = CheckInTruckUseCase(truckRepository)
    val toggleTaskUseCase = ToggleTaskUseCase(repairTaskRepository)
    val addTaskNoteUseCase = AddTaskNoteUseCase(repairTaskRepository)
    val getReportsUseCase = GetReportsUseCase(
        truckRepository = truckRepository,
        taskRepository = repairTaskRepository,
        employeeRepository = employeeRepository,
    )

    val loginUseCase = LoginUseCase(employeeRepository)
    val registerUseCase = RegisterUseCase(employeeRepository, timeProvider)
    val changePasswordUseCase = ChangePasswordUseCase(employeeRepository)
    val getDashboardStatsUseCase = GetDashboardStatsUseCase(
        truckRepository = truckRepository,
        employeeRepository = employeeRepository,
        repairTaskDao = database.repairTaskDao(),
        taskNoteDao = database.taskNoteDao(),
        timeProvider = timeProvider,
    )

    val databaseSeeder: DatabaseSeeder = DatabaseSeeder(database)
}
