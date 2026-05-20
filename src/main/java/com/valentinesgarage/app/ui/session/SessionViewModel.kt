package com.valentinesgarage.app.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.data.local.SessionStore
import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.di.AppContainer
import com.valentinesgarage.app.domain.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Holds the currently signed-in employee and persists it to disk via
 * [SessionStore] so a return visit lands directly in the dashboard.
 */
class SessionViewModel(
    private val employeeRepository: EmployeeRepository,
    private val sessionStore: SessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    val currentEmployee: StateFlow<Employee?>
        get() = _currentEmployee.asStateFlow()
    private val _currentEmployee = MutableStateFlow<Employee?>(null)

    init {
        viewModelScope.launch {
            sessionStore.employeeIdFlow.collect { id ->
                if (id == null) {
                    _currentEmployee.value = null
                    _state.value = SessionState.SignedOut
                } else {
                    val employee = employeeRepository.findById(id)
                    if (employee != null && employee.isActive) {
                        _currentEmployee.value = employee
                        _state.value = SessionState.SignedIn(employee)
                    } else {
                        // The persisted account no longer exists or was
                        // deactivated — wipe and bounce to login.
                        sessionStore.setEmployeeId(null)
                        _currentEmployee.value = null
                        _state.value = SessionState.SignedOut
                    }
                }
            }
        }
    }

    fun setSignedIn(employee: Employee) {
        viewModelScope.launch {
            sessionStore.setEmployeeId(employee.id)
            _currentEmployee.value = employee
            _state.value = SessionState.SignedIn(employee)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            sessionStore.setEmployeeId(null)
            _currentEmployee.value = null
            _state.value = SessionState.SignedOut
        }
    }

    /** Re-pull the current user from the database (after profile edits). */
    fun refresh() {
        val id = _currentEmployee.value?.id ?: return
        viewModelScope.launch {
            val employee = employeeRepository.findById(id) ?: return@launch
            _currentEmployee.value = employee
            _state.value = SessionState.SignedIn(employee)
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SessionViewModel(container.employeeRepository, container.sessionStore)
                }
            }
    }
}

sealed interface SessionState {
    data object Loading : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val employee: Employee) : SessionState
}

/** Helper to read the [AppContainer] inside ViewModel factories. */
internal fun CreationExtras.requireContainer(): AppContainer {
    val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
        ?: error("Application not available in CreationExtras")
    return (app as com.valentinesgarage.app.ValentinesGarageApp).container
}
