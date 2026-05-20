package com.valentinesgarage.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.valentinesgarage.app.domain.model.Truck
import com.valentinesgarage.app.domain.model.VehicleCondition

@Entity(tableName = "trucks")
data class TruckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plateNumber: String,
    val make: String,
    val model: String,
    val customerName: String,
    val odometerKm: Int,
    val condition: String,
    val conditionNotes: String,
    val checkedInAt: Long,
    val checkedInByEmployeeId: Long,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
) {
    fun toDomain(): Truck = Truck(
        id = id,
        plateNumber = plateNumber,
        make = make,
        model = model,
        customerName = customerName,
        odometerKm = odometerKm,
        condition = VehicleCondition.fromName(condition),
        conditionNotes = conditionNotes,
        checkedInAt = checkedInAt,
        checkedInByEmployeeId = checkedInByEmployeeId,
        isCompleted = isCompleted,
        completedAt = completedAt,
    )

    companion object {
        fun fromDomain(truck: Truck): TruckEntity = TruckEntity(
            id = truck.id,
            plateNumber = truck.plateNumber,
            make = truck.make,
            model = truck.model,
            customerName = truck.customerName,
            odometerKm = truck.odometerKm,
            condition = truck.condition.name,
            conditionNotes = truck.conditionNotes,
            checkedInAt = truck.checkedInAt,
            checkedInByEmployeeId = truck.checkedInByEmployeeId,
            isCompleted = truck.isCompleted,
            completedAt = truck.completedAt,
        )
    }
}
