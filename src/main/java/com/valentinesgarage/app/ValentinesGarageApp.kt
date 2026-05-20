package com.valentinesgarage.app

import android.app.Application
import com.valentinesgarage.app.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application root. Holds the manual DI container so all ViewModels can
 * access shared singletons (database, repositories, use cases) and seeds
 */
class ValentinesGarageApp : Application() {
    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        applicationScope.launch {
            container.databaseSeeder.seedIfNeeded(container.timeProvider.now())
        }
    }
}
