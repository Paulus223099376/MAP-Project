package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.domain.model.Employee

/**
 * Authenticate by username + password. Failure messages are deliberately
 * generic so we don't reveal which half of the credential pair was wrong.
 */
class LoginUseCase(private val employeeRepository: EmployeeRepository) {

    sealed interface Result {
        data class Success(val employee: Employee) : Result
        data class Failure(val message: String) : Result
    }

    suspend operator fun invoke(username: String, password: String): Result {
        if (username.isBlank() || password.isBlank()) {
            return Result.Failure("Enter both username and password")
        }
        val employee = employeeRepository.authenticate(username, password)
            ?: return Result.Failure("Username or password is incorrect")
        if (!employee.isActive) {
            return Result.Failure("This account has been deactivated. See the owner.")
        }
        return Result.Success(employee)
    }
}
