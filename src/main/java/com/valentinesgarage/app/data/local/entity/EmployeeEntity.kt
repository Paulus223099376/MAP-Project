package com.valentinesgarage.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.model.EmployeeRole

@Entity(
    tableName = "employees",
    indices = [Index(value = ["username"], unique = true)],
)
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val name: String,
    val role: String,
    val passwordHash: String,
    val passwordSalt: String,
    val email: String = "",
    val phone: String = "",
    val createdAt: Long = 0L,
    val isActive: Boolean = true,
) {
    fun toDomain(): Employee = Employee(
        id = id,
        username = username,
        name = name,
        role = runCatching { EmployeeRole.valueOf(role) }.getOrDefault(EmployeeRole.MECHANIC),
        email = email,
        phone = phone,
        createdAt = createdAt,
        isActive = isActive,
    )
}
