package com.valentinesgarage.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.usecase.ChangePasswordUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import com.valentinesgarage.app.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val employeeId: Long = 0,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val saving: Boolean = false,
    val profileMessage: String? = null,
    val profileError: String? = null,

    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val showPasswords: Boolean = false,
    val updatingPassword: Boolean = false,
    val passwordMessage: String? = null,
    val passwordError: String? = null,
)

class ProfileViewModel(
    private val employeeRepository: EmployeeRepository,
    private val changePasswordUseCase: ChangePasswordUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun bind(employee: Employee) {
        if (_state.value.employeeId == employee.id) return
        _state.value = ProfileUiState(
            employeeId = employee.id,
            name = employee.name,
            email = employee.email,
            phone = employee.phone,
        )
    }

    fun setName(v: String) = mutate { it.copy(name = v, profileError = null, profileMessage = null) }
    fun setEmail(v: String) = mutate { it.copy(email = v.trim(), profileError = null, profileMessage = null) }
    fun setPhone(v: String) = mutate { it.copy(phone = v, profileError = null, profileMessage = null) }

    fun setCurrentPassword(v: String) = mutate { it.copy(currentPassword = v, passwordError = null, passwordMessage = null) }
    fun setNewPassword(v: String) = mutate { it.copy(newPassword = v, passwordError = null, passwordMessage = null) }
    fun setConfirmPassword(v: String) = mutate { it.copy(confirmPassword = v, passwordError = null, passwordMessage = null) }
    fun toggleShowPasswords() = mutate { it.copy(showPasswords = !it.showPasswords) }

    fun saveProfile(onSaved: () -> Unit) {
        val current = _state.value
        if (current.saving) return

        Validators.nameProblem(current.name)?.let {
            mutate { s -> s.copy(profileError = it) }
            return
        }
        Validators.emailProblem(current.email)?.let {
            mutate { s -> s.copy(profileError = it) }
            return
        }
        Validators.phoneProblem(current.phone)?.let {
            mutate { s -> s.copy(profileError = it) }
            return
        }

        mutate { it.copy(saving = true, profileError = null, profileMessage = null) }
        viewModelScope.launch {
            val updated = employeeRepository.updateProfile(
                employeeId = current.employeeId,
                name = current.name,
                email = current.email,
                phone = current.phone,
            )
            if (updated != null) {
                mutate { it.copy(saving = false, profileMessage = "Profile saved") }
                onSaved()
            } else {
                mutate { it.copy(saving = false, profileError = "Could not save profile") }
            }
        }
    }

    fun changePassword() {
        val current = _state.value
        if (current.updatingPassword) return
        mutate { it.copy(updatingPassword = true, passwordError = null, passwordMessage = null) }
        viewModelScope.launch {
            val result = changePasswordUseCase(
                employeeId = current.employeeId,
                currentPassword = current.currentPassword,
                newPassword = current.newPassword,
                confirmPassword = current.confirmPassword,
            )
            when (result) {
                is ChangePasswordUseCase.Result.Failure ->
                    mutate { it.copy(updatingPassword = false, passwordError = result.message) }
                ChangePasswordUseCase.Result.Success ->
                    mutate {
                        it.copy(
                            updatingPassword = false,
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = "",
                            passwordMessage = "Password updated",
                        )
                    }
            }
        }
    }

    private fun mutate(block: (ProfileUiState) -> ProfileUiState) {
        _state.value = block(_state.value)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                ProfileViewModel(container.employeeRepository, container.changePasswordUseCase)
            }
        }
    }
}
