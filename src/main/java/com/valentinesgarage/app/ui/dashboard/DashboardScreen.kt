package com.valentinesgarage.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinesgarage.app.R
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.domain.model.Truck
import com.valentinesgarage.app.domain.model.VehicleCondition
import com.valentinesgarage.app.ui.components.AurevargFooter
import com.valentinesgarage.app.ui.components.GarageBottomBar
import com.valentinesgarage.app.ui.components.StatCard
import com.valentinesgarage.app.ui.session.SessionViewModel
import com.valentinesgarage.app.ui.navigation.Routes
import com.valentinesgarage.app.util.formatRelative

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionViewModel: SessionViewModel,
    onNavigate: (String) -> Unit,
    onTruckClick: (Long) -> Unit,
    onCheckIn: () -> Unit,
    onSettings: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val employee by sessionViewModel.currentEmployee.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    LaunchedEffect(employee?.id) {
        val id = employee?.id ?: return@LaunchedEffect
        viewModel.setEmployee(id)
    }

    val role = employee?.role ?: EmployeeRole.MECHANIC

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.garage_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Column {
                            Text("Hello, ${employee?.name?.substringBefore(' ') ?: ""}")
                            Text(
                                "Here's what's happening at the garage",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            GarageBottomBar(
                role = role,
                currentRoute = Routes.DASHBOARD,
                onNavigate = onNavigate,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Check in truck") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = onCheckIn,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 12.dp,
                bottom = padding.calculateBottomPadding() + 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { TodayBanner(stats.checkInsToday, stats.totalEmployees) }

            item {
                Text(
                    "Workshop overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        label = "Trucks in shop",
                        value = stats.inProgressTrucks.toString(),
                        icon = Icons.Default.LocalShipping,
                        accent = Color(0xFFB3261E),
                        modifier = Modifier.weight(1f),
                        helper = "${stats.totalTrucks} total"
                    )
                    StatCard(
                        label = "Completed",
                        value = stats.completedTrucks.toString(),
                        icon = Icons.Default.CheckCircle,
                        accent = Color(0xFF1B7F3B),
                        modifier = Modifier.weight(1f),
                        helper = "Across all time"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        label = "Pending tasks",
                        value = stats.pendingTasks.toString(),
                        icon = Icons.Default.PendingActions,
                        accent = Color(0xFFCB8B0F),
                        modifier = Modifier.weight(1f),
                        helper = "${stats.completedTasks} done"
                    )
                    StatCard(
                        label = "Notes logged",
                        value = stats.totalNotes.toString(),
                        icon = Icons.Default.Note,
                        accent = Color(0xFF5C6770),
                        modifier = Modifier.weight(1f),
                        helper = "Across all jobs"
                    )
                }
            }

            item { CompletionCard(stats.completedTasks, stats.totalTasks) }

            item {
                Text(
                    "What you've done",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        label = "Tasks completed",
                        value = stats.myCompletedTasks.toString(),
                        icon = Icons.Default.AssignmentTurnedIn,
                        accent = Color(0xFF1B7F3B),
                        modifier = Modifier.weight(1f),
                        helper = "By you"
                    )
                    StatCard(
                        label = "Notes you wrote",
                        value = stats.myNotesCount.toString(),
                        icon = Icons.Default.Note,
                        accent = Color(0xFFB3261E),
                        modifier = Modifier.weight(1f),
                        helper = "${stats.myCheckIns} check-ins"
                    )
                }
            }

            if (role == EmployeeRole.OWNER) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            label = "Team size",
                            value = stats.totalEmployees.toString(),
                            icon = Icons.Default.People,
                            accent = Color(0xFF7B1A14),
                            modifier = Modifier.weight(1f),
                            helper = "Active accounts"
                        )
                        StatCard(
                            label = "On the floor",
                            value = stats.myPendingTasksOnFloor.toString(),
                            icon = Icons.Default.Build,
                            accent = Color(0xFFF2A516),
                            modifier = Modifier.weight(1f),
                            helper = "Open tasks"
                        )
                    }
                }
            }

            item {
                Text(
                    "Recent check-ins",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            if (stats.recentCheckIns.isEmpty()) {
                item {
                    Text(
                        "No trucks yet — tap the button below to record the first arrival.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(stats.recentCheckIns, key = { it.id }) { truck ->
                    RecentTruckRow(truck = truck, onClick = { onTruckClick(truck.id) })
                }
            }

            // Aurevarg footer - clickable and centralized
            item {
                AurevargFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun TodayBanner(checkInsToday: Int, teamSize: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Today,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Today's pulse",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                )
                Text(
                    if (checkInsToday == 1) "1 truck checked in today"
                    else "$checkInsToday trucks checked in today",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    "$teamSize team members on the books",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                )
            }
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun CompletionCard(done: Int, total: Int) {
    val ratio = if (total == 0) 0f else done / total.toFloat()
    val percent = (ratio * 100).toInt()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Completion rate",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "$percent%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { ratio },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "$done of $total tasks ticked off",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RecentTruckRow(truck: Truck, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(conditionColor(truck.condition)),
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(truck.plateNumber, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${truck.make} ${truck.model} • ${truck.customerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                color = if (truck.isCompleted) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = if (truck.isCompleted) "Done" else formatRelative(truck.checkedInAt),
                    color = if (truck.isCompleted) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

private fun conditionColor(condition: VehicleCondition): Color = when (condition) {
    VehicleCondition.EXCELLENT -> Color(0xFF1B7F3B)
    VehicleCondition.GOOD -> Color(0xFF4F8F2A)
    VehicleCondition.FAIR -> Color(0xFFCB8B0F)
    VehicleCondition.POOR -> Color(0xFFB3261E)
}