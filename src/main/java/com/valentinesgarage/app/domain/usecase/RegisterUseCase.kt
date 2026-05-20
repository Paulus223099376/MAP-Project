package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.util.TimeProvider
import com.valentinesgarage.app.util.Validators

/**
 * Validates input and persists a new employee account. Defaults to the
 * MECHANIC role; only an existing OWNER session may create another OWNER
 * (enforced by the calling ViewModel — the use case stays role-agnostic
 * to remain easy to test).
 */
class RegisterUseCase(
    private val employeeRepository: EmployeeRepository,
    private val timeProvider: TimeProvider,
) {

    data class Input(
        val username: String,
        val name: String,
        val password: String,
        val confirmPassword: String,
        val role: EmployeeRole = EmployeeRole.MECHANIC,
        val email: String = "",
        val phone: String = "",
    )

    sealed interface Result {
        data class Success(val employee: Employee) : Result
        data class Failure(val message: String) : Result
    }

    suspend operator fun invoke(input: Input): Result {
        Validators.usernameProblem(input.username)?.let { return Result.Failure(it) }
        Validators.nameProblem(input.name)?.let { return Result.Failure(it) }
        Validators.passwordProblem(input.password)?.let { return Result.Failure(it) }
        if (input.password != input.confirmPassword) {
            return Result.Failure("Passwords do not match")
        }
        Validators.emailProblem(input.email)?.let { return Result.Failure(it) }
        Validators.phoneProblem(input.phone)?.let { return Result.Failure(it) }

        if (employeeRepository.isUsernameTaken(input.username)) {
            return Result.Failure("That username is already taken")
        }

        val created = employeeRepository.register(
            username = input.username,
            name = input.name,
            password = input.password,
            role = input.role,
            email = input.email,
            phone = input.phone,
            nowMillis = timeProvider.now(),
        )
        return Result.Success(created)
    }
}
