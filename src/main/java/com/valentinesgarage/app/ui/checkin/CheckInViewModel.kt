package com.valentinesgarage.app.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.domain.model.VehicleCondition
import com.valentinesgarage.app.domain.usecase.CheckInTruckUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import com.valentinesgarage.app.util.TimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckInUiState(
    val plate: String = "",
    val make: String = "",
    val model: String = "",
    val customer: String = "",
    val km: String = "",
    val condition: VehicleCondition = VehicleCondition.GOOD,
    val notes: String = "",
    val saving: Boolean = false,
    val error: String? = null,
)

class CheckInViewModel(
    private val checkInTruckUseCase: CheckInTruckUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()

    fun update(transform: (CheckInUiState) -> CheckInUiState) {
        _state.value = transform(_state.value)
    }

    fun submit(employeeId: Long, onSuccess: (Long) -> Unit) {
        val current = _state.value
        val km = current.km.trim().toIntOrNull()
        if (km == null || km < 0) {
            _state.value = current.copy(error = "Enter a valid odometer reading")
            return
        }
        _state.value = current.copy(saving = true, error = null)
        viewModelScope.launch {
            val result = checkInTruckUseCase(
                input = CheckInTruckUseCase.Input(
                    plateNumber = current.plate,
                    make = current.make,
                    model = current.model,
                    customerName = current.customer,
                    odometerKm = km,
                    condition = current.condition,
                    conditionNotes = current.notes,
                    checkedInByEmployeeId = employeeId,
                ),
                nowMillis = timeProvider.now(),
            )
            when (result) {
                is CheckInTruckUseCase.Result.Failure ->
                    _state.value = _state.value.copy(saving = false, error = result.reason)
                is CheckInTruckUseCase.Result.Success -> {
                    _state.value = CheckInUiState()
                    onSuccess(result.truckId)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                CheckInViewModel(container.checkInTruckUseCase, container.timeProvider)
            }
        }
    }
}
