package com.valentinesgarage.app.domain.model

/**
 * Possible overall condition ratings captured at truck check-in.
 * Used to give Valentine a quick visual indicator of how each vehicle
 * arrived at the garage so disputes can be settled later.
 */
enum class VehicleCondition(val displayName: String) {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor");

    companion object {
        fun fromName(name: String?): VehicleCondition =
            entries.firstOrNull { it.name == name } ?: GOOD
    }
}
