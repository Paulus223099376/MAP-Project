package com.valentinesgarage.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repair_tasks",
    foreignKeys = [
        ForeignKey(
            entity = TruckEntity::class,
            parentColumns = ["id"],
            childColumns = ["truckId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("truckId"), Index("completedByEmployeeId")],
)
data class RepairTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val truckId: Long,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val completedByEmployeeId: Long?,
    val completedAt: Long?,
)
