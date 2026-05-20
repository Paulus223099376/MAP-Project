package com.valentinesgarage.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Persists the currently signed-in employee's id across process death so
 * the user does not need to log in every time the app launches.
 */
class SessionStore(private val context: Context) {

    val employeeIdFlow: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMPLOYEE_ID]?.takeIf { it > 0 }
    }

    suspend fun setEmployeeId(id: Long?) {
        context.dataStore.edit { prefs ->
            if (id == null || id <= 0) {
                prefs.remove(KEY_EMPLOYEE_ID)
            } else {
                prefs[KEY_EMPLOYEE_ID] = id
            }
        }
    }

    private companion object {
        val Context.dataStore by preferencesDataStore(name = "garage_session")
        val KEY_EMPLOYEE_ID = longPreferencesKey("employee_id")
    }
}
