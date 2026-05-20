package com.valentinesgarage.app.ui.trucks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.data.repository.RepairTaskRepository
import com.valentinesgarage.app.data.repository.TruckRepository
import com.valentinesgarage.app.domain.model.RepairTask
import com.valentinesgarage.app.domain.model.Truck
import com.valentinesgarage.app.domain.usecase.AddTaskNoteUseCase
import com.valentinesgarage.app.domain.usecase.ToggleTaskUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import com.valentinesgarage.app.util.TimeProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TruckDetailViewModel(
    private val truckId: Long,
    private val truckRepository: TruckRepository,
    private val taskRepository: RepairTaskRepository,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val addTaskNoteUseCase: AddTaskNoteUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val truck: StateFlow<Truck?> = truckRepository.observeById(truckId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tasks: StateFlow<List<RepairTask>> = taskRepository.observeForTruck(truckId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTask(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            taskRepository.addTask(truckId, title, description)
        }
    }

    fun toggleTask(taskId: Long, employeeId: Long) {
        viewModelScope.launch {
            toggleTaskUseCase(taskId, employeeId, timeProvider.now())
        }
    }

    fun addNote(taskId: Long, employeeId: Long, message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            addTaskNoteUseCase(taskId, employeeId, message, timeProvider.now())
        }
    }

    fun completeTruck() {
        viewModelScope.launch {
            val isCompleted = truck.value?.isCompleted ?: false
            truckRepository.setCompleted(
                truckId = truckId,
                isCompleted = !isCompleted,
                completedAt = if (!isCompleted) timeProvider.now() else null,
            )
        }
    }

    companion object {
        fun factoryFor(truckId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                TruckDetailViewModel(
                    truckId = truckId,
                    truckRepository = container.truckRepository,
                    taskRepository = container.repairTaskRepository,
                    toggleTaskUseCase = container.toggleTaskUseCase,
                    addTaskNoteUseCase = container.addTaskNoteUseCase,
                    timeProvider = container.timeProvider,
                )
            }
        }
    }
}
