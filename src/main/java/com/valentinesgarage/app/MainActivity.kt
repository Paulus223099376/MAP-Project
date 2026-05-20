package com.valentinesgarage.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.valentinesgarage.app.ui.checkin.CheckInScreen
import com.valentinesgarage.app.ui.components.DashboardSkeleton
import com.valentinesgarage.app.ui.dashboard.DashboardScreen
import com.valentinesgarage.app.ui.login.LoginScreen
import com.valentinesgarage.app.ui.navigation.Routes
import com.valentinesgarage.app.ui.profile.ProfileScreen
import com.valentinesgarage.app.ui.register.RegisterScreen
import com.valentinesgarage.app.ui.reports.ReportsScreen
import com.valentinesgarage.app.ui.session.SessionState
import com.valentinesgarage.app.ui.session.SessionViewModel
import com.valentinesgarage.app.ui.settings.SettingsScreen
import com.valentinesgarage.app.ui.theme.ValentinesGarageTheme
import com.valentinesgarage.app.ui.trucks.TruckDetailScreen
import com.valentinesgarage.app.ui.trucks.TruckListScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as ValentinesGarageApp).container

        setContent {
            ValentinesGarageTheme {
                val sessionViewModel: SessionViewModel = viewModel(
                    factory = SessionViewModel.factory(container)
                )
                val navController = rememberNavController()
                val sessionState by sessionViewModel.state.collectAsStateWithLifecycle()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // Latch the start destination exactly once — the first time we
                // leave Loading.  After that, all navigation is driven by the
                // LaunchedEffect below; we never recreate the NavHost.
                var startDestination by rememberSaveable { mutableStateOf<String?>(null) }
                if (startDestination == null && sessionState !is SessionState.Loading) {
                    startDestination = when (sessionState) {
                        is SessionState.SignedIn -> Routes.DASHBOARD
                        else -> Routes.LOGIN
                    }
                }

                // Handle sign-out / sign-in navigation after the initial load.
                LaunchedEffect(sessionState, currentRoute) {
                    when (sessionState) {
                        SessionState.Loading -> Unit
                        SessionState.SignedOut -> {
                            if (currentRoute != null &&
                                currentRoute != Routes.LOGIN &&
                                currentRoute != Routes.REGISTER
                            ) {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        is SessionState.SignedIn -> {
                            if (currentRoute == Routes.LOGIN || currentRoute == Routes.REGISTER) {
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                // Keep showing the skeleton until we know where to start.
                if (startDestination == null) {
                    DashboardSkeleton(
                        padding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 48.dp,
                            bottom = 96.dp
                        )
                    )
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination!!,
                        modifier = Modifier,
                    ) {
                        composable(Routes.LOGIN) {
                            LoginScreen(
                                sessionViewModel = sessionViewModel,
                                onSignedIn = {
                                    navController.navigate(Routes.DASHBOARD) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onRegister = { navController.navigate(Routes.REGISTER) },
                            )
                        }

                        composable(Routes.REGISTER) {
                            RegisterScreen(
                                onBack = { navController.popBackStack() },
                                onRegistered = { employee ->
                                    sessionViewModel.setSignedIn(employee)
                                    navController.navigate(Routes.DASHBOARD) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                            )
                        }

                        composable(Routes.DASHBOARD) {
                            DashboardScreen(
                                sessionViewModel = sessionViewModel,
                                onNavigate = { route -> navigateBottomTab(navController, route) },
                                onTruckClick = { id -> navController.navigate(Routes.truckDetail(id)) },
                                onCheckIn = { navController.navigate(Routes.CHECK_IN) },
                                onSettings = { navController.navigate(Routes.SETTINGS) },
                            )
                        }

                        composable(Routes.TRUCKS) {
                            TruckListScreen(
                                sessionViewModel = sessionViewModel,
                                onNavigate = { route -> navigateBottomTab(navController, route) },
                                onCheckIn = { navController.navigate(Routes.CHECK_IN) },
                                onTruckClick = { id -> navController.navigate(Routes.truckDetail(id)) },
                            )
                        }

                        composable(Routes.CHECK_IN) {
                            CheckInScreen(
                                sessionViewModel = sessionViewModel,
                                onBack = { navController.popBackStack() },
                                onSaved = { id ->
                                    navController.popBackStack()
                                    navController.navigate(Routes.truckDetail(id))
                                },
                            )
                        }

                        composable(
                            route = Routes.TRUCK_DETAIL,
                            arguments = listOf(navArgument(Routes.TRUCK_ID_ARG) { type = NavType.LongType }),
                        ) { entry ->
                            val truckId = entry.arguments?.getLong(Routes.TRUCK_ID_ARG) ?: 0L
                            TruckDetailScreen(
                                truckId = truckId,
                                sessionViewModel = sessionViewModel,
                                onBack = { navController.popBackStack() },
                            )
                        }

                        composable(Routes.REPORTS) {
                            ReportsScreen(
                                sessionViewModel = sessionViewModel,
                                onNavigate = { route -> navigateBottomTab(navController, route) },
                                onBack = { navController.popBackStack() },
                            )
                        }

                        composable(Routes.SETTINGS) {
                            SettingsScreen(
                                sessionViewModel = sessionViewModel,
                                onBack = { navController.popBackStack() },
                                onProfile = { navController.navigate(Routes.PROFILE) },
                                onSignedOut = {
                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                            )
                        }

                        composable(Routes.PROFILE) {
                            ProfileScreen(
                                sessionViewModel = sessionViewModel,
                                onNavigate = { route -> navigateBottomTab(navController, route) },
                                showBottomBar = true,
                                onBack = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun navigateBottomTab(
    navController: androidx.navigation.NavController,
    route: String,
) {
    navController.navigate(route) {
        popUpTo(Routes.DASHBOARD) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}