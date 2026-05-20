package com.valentinesgarage.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.usecase.LoginUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val submitting: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)


class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun setUsername(value: String) = mutate { it.copy(username = value, error = null) }
    fun setPassword(value: String) = mutate { it.copy(password = value, error = null) }
    fun togglePasswordVisibility() = mutate { it.copy(showPassword = !it.showPassword) }
    fun showMessage(message: String?) = mutate { it.copy(message = message) }

    fun submit(onSuccess: (Employee) -> Unit) {
        val current = _state.value
        if (current.submitting) return
        mutate { it.copy(submitting = true, error = null) }
        viewModelScope.launch {
            when (val result = loginUseCase(current.username, current.password)) {
                is LoginUseCase.Result.Failure ->
                    mutate { it.copy(submitting = false, error = result.message) }
                is LoginUseCase.Result.Success -> {
                    mutate { LoginUiState() }
                    onSuccess(result.employee)
                }
            }
        }
    }

    private fun mutate(block: (LoginUiState) -> LoginUiState) {
        _state.value = block(_state.value)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                LoginViewModel(container.loginUseCase)
            }
        }
    }
}
