package com.valentinesgarage.app.domain.usecase

import com.valentinesgarage.app.data.repository.TruckRepository
import com.valentinesgarage.app.domain.model.Truck
import com.valentinesgarage.app.domain.model.VehicleCondition

/**
 * Validates and persists a new truck check-in.
 *
 * Capturing the odometer reading and the on-arrival condition is the
 * core anti-misuse safeguard for the garage: if a vehicle leaves with
 * extra mileage or fresh damage, the check-in record is the source of
 * truth.
 */
class CheckInTruckUseCase(private val truckRepository: TruckRepository) {

    data class Input(
        val plateNumber: String,
        val make: String,
        val model: String,
        val customerName: String,
        val odometerKm: Int,
        val condition: VehicleCondition,
        val conditionNotes: String,
        val checkedInByEmployeeId: Long,
    )

    sealed interface Result {
        data class Success(val truckId: Long) : Result
        data class Failure(val reason: String) : Result
    }

    suspend operator fun invoke(input: Input, nowMillis: Long): Result {
        if (input.plateNumber.isBlank()) return Result.Failure("Number plate is required")
        if (input.make.isBlank()) return Result.Failure("Make is required")
        if (input.model.isBlank()) return Result.Failure("Model is required")
        if (input.customerName.isBlank()) return Result.Failure("Customer name is required")
        if (input.odometerKm < 0) return Result.Failure("Odometer cannot be negative")
        if (input.checkedInByEmployeeId <= 0) return Result.Failure("Sign in to check in trucks")

        val id = truckRepository.checkIn(
            Truck(
                plateNumber = input.plateNumber.trim().uppercase(),
                make = input.make.trim(),
                model = input.model.trim(),
                customerName = input.customerName.trim(),
                odometerKm = input.odometerKm,
                condition = input.condition,
                conditionNotes = input.conditionNotes.trim(),
                checkedInAt = nowMillis,
                checkedInByEmployeeId = input.checkedInByEmployeeId,
            )
        )
        return Result.Success(id)
    }
}
