package com.valentinesgarage.app.ui.navigation

/**
 * Centralised list of route names so screens cannot drift out of sync
 * with the navigation graph.
 */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val TRUCKS = "trucks"
    const val CHECK_IN = "check_in"
    const val TRUCK_DETAIL = "truck_detail/{truckId}"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"

    fun truckDetail(truckId: Long): String = "truck_detail/$truckId"
    const val TRUCK_ID_ARG = "truckId"
}
