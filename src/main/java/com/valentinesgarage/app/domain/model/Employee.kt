package com.valentinesgarage.app.domain.model

/**
 * Garage staff. The OWNER role is reserved for Valentine and unlocks
 * access to the cross-cutting reports screen.
 */
data class Employee(
    val id: Long = 0,
    val username: String,
    val name: String,
    val role: EmployeeRole,
    val email: String = "",
    val phone: String = "",
    val createdAt: Long = 0L,
    val isActive: Boolean = true,
)

enum class EmployeeRole(val displayName: String) {
    OWNER("Owner"),
    MECHANIC("Mechanic"),
}
