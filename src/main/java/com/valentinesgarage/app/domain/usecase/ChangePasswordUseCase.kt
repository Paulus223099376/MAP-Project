package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.EmployeeRepository
import com.valentinesgarage.app.util.Validators

class ChangePasswordUseCase(private val employeeRepository: EmployeeRepository) {

    sealed interface Result {
        data object Success : Result
        data class Failure(val message: String) : Result
    }

    suspend operator fun invoke(
        employeeId: Long,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ): Result {
        if (currentPassword.isBlank()) return Result.Failure("Enter your current password")
        Validators.passwordProblem(newPassword)?.let { return Result.Failure(it) }
        if (newPassword != confirmPassword) return Result.Failure("New passwords do not match")
        if (newPassword == currentPassword) {
            return Result.Failure("New password must be different from the current one")
        }

        val ok = employeeRepository.updatePassword(employeeId, currentPassword, newPassword)
        return if (ok) Result.Success
        else Result.Failure("Current password is incorrect")
    }
}
