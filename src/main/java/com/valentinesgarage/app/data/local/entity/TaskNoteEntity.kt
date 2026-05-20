package com.valentinesgarage.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_notes",
    foreignKeys = [
        ForeignKey(
            entity = RepairTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("taskId"), Index("authorEmployeeId")],
)
data class TaskNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val authorEmployeeId: Long,
    val message: String,
    val createdAt: Long,
)
