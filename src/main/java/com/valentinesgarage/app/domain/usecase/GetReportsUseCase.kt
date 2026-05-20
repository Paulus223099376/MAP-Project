package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.data.repository.RepairTaskRepository
import com.valentinesgarage.app.data.repository.TruckRepository
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.model.Truck
import com.valentinesgarage.app.domain.model.VehicleCondition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Aggregates data across employees, trucks and tasks to produce
 * Valentine's reports: who did what, and what shape every truck
 * arrived in.
 */
class GetReportsUseCase(
    private val truckRepository: TruckRepository,
    private val taskRepository: RepairTaskRepository,
    private val employeeRepository: EmployeeRepository,
) {

    data class EmployeeActivity(
        val employee: Employee,
        val completedTaskCount: Int,
        val notesCount: Int,
        val checkedInTrucksCount: Int,
    )

    data class CheckInRecord(
        val truck: Truck,
        val checkedInBy: String,
        val taskTotal: Int,
        val taskDone: Int,
    )

    data class Report(
        val employeeActivity: List<EmployeeActivity>,
        val checkIns: List<CheckInRecord>,
        val conditionBreakdown: Map<VehicleCondition, Int>,
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun observe(): Flow<Report> =
        combine(
            employeeRepository.observeAll(),
            truckRepository.observeAll(),
        ) { employees, trucks -> employees to trucks }
            .flatMapLatest { (employees, trucks) ->
                if (trucks.isEmpty()) {
                    kotlinx.coroutines.flow.flowOf(
                        Report(
                            employeeActivity = employees.map {
                                EmployeeActivity(it, 0, 0, 0)
                            },
                            checkIns = emptyList(),
                            conditionBreakdown = emptyMap(),
                        )
                    )
                } else {
                    val perTruckFlows = trucks.map { truck ->
                        taskRepository.observeForTruck(truck.id).map { tasks -> truck to tasks }
                    }
                    combine(perTruckFlows) { array ->
                        val taskByTruck = array.toList().toMap()
                        val allTasks = taskByTruck.values.flatten()
                        val allNotes = allTasks.flatMap { it.notes }
                        val employeesById = employees.associateBy { it.id }

                        val activity = employees.map { emp ->
                            EmployeeActivity(
                                employee = emp,
                                completedTaskCount = allTasks.count { it.completedByEmployeeId == emp.id },
                                notesCount = allNotes.count { it.authorEmployeeId == emp.id },
                                checkedInTrucksCount = trucks.count { it.checkedInByEmployeeId == emp.id },
                            )
                        }

                        val checkIns = trucks.map { truck ->
                            val tasks = taskByTruck[truck].orEmpty()
                            CheckInRecord(
                                truck = truck,
                                checkedInBy = employeesById[truck.checkedInByEmployeeId]?.name ?: "Unknown",
                                taskTotal = tasks.size,
                                taskDone = tasks.count { it.isDone },
                            )
                        }

                        val conditionBreakdown = trucks.groupingBy { it.condition }.eachCount()

                        Report(activity, checkIns, conditionBreakdown)
                    }
                }
            }
}
