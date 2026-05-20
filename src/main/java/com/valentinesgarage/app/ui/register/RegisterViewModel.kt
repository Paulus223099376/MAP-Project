package com.valentinesgarage.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.domain.usecase.RegisterUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val username: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val phone: String = "",
    val role: EmployeeRole = EmployeeRole.MECHANIC,
    val showPassword: Boolean = false,
    val submitting: Boolean = false,
    val error: String? = null,
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun setUsername(v: String) = mutate { it.copy(username = v.lowercase().trim(), error = null) }
    fun setName(v: String) = mutate { it.copy(name = v, error = null) }
    fun setPassword(v: String) = mutate { it.copy(password = v, error = null) }
    fun setConfirmPassword(v: String) = mutate { it.copy(confirmPassword = v, error = null) }
    fun setEmail(v: String) = mutate { it.copy(email = v.trim(), error = null) }
    fun setPhone(v: String) = mutate { it.copy(phone = v, error = null) }
    fun togglePasswordVisibility() = mutate { it.copy(showPassword = !it.showPassword) }

    fun submit(onSuccess: (Employee) -> Unit) {
        val current = _state.value
        if (current.submitting) return
        mutate { it.copy(submitting = true, error = null) }
        viewModelScope.launch {
            val result = registerUseCase(
                RegisterUseCase.Input(
                    username = current.username,
                    name = current.name,
                    password = current.password,
                    confirmPassword = current.confirmPassword,
                    role = current.role,
                    email = current.email,
                    phone = current.phone,
                )
            )
            when (result) {
                is RegisterUseCase.Result.Failure ->
                    mutate { it.copy(submitting = false, error = result.message) }
                is RegisterUseCase.Result.Success -> {
                    mutate { RegisterUiState() }
                    onSuccess(result.employee)
                }
            }
        }
    }

    private fun mutate(block: (RegisterUiState) -> RegisterUiState) {
        _state.value = block(_state.value)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                RegisterViewModel(container.registerUseCase)
            }
        }
    }
}
