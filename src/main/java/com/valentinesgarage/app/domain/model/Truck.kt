package com.valentinesgarage.app.domain.model

/**
 * Domain representation of a truck checked in to the garage.
 * Independent of any persistence framework so it can be unit tested
 * without Android dependencies.
 */
data class Truck(
    val id: Long = 0,
    val plateNumber: String,
    val make: String,
    val model: String,
    val customerName: String,
    val odometerKm: Int,
    val condition: VehicleCondition,
    val conditionNotes: String,
    val checkedInAt: Long,
    val checkedInByEmployeeId: Long,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
)
