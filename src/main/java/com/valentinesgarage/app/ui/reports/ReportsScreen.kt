package com.valentinesgarage.app.ui.reports

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinesgarage.app.domain.model.VehicleCondition
import com.valentinesgarage.app.domain.usecase.GetReportsUseCase
import com.valentinesgarage.app.ui.components.GarageBottomBar
import com.valentinesgarage.app.ui.navigation.Routes
import com.valentinesgarage.app.ui.session.SessionViewModel
import com.valentinesgarage.app.util.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    sessionViewModel: SessionViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ReportsViewModel = viewModel(factory = ReportsViewModel.Factory),
) {
    val report by viewModel.report.collectAsStateWithLifecycle()
    val employee by sessionViewModel.currentEmployee.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            employee?.let {
                GarageBottomBar(
                    role = it.role,
                    currentRoute = Routes.REPORTS,
                    onNavigate = onNavigate,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val data = report
        if (data == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading reports…")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 12.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(key = "condition_breakdown") {
                    ConditionBreakdownCard(data.conditionBreakdown)
                }
                item(key = "employee_header") {
                    SectionHeader("Employee activity")
                }

                itemsIndexed(
                    data.employeeActivity,
                    key = { index, activity -> "emp_${activity.employee.id}_$index" }
                ) { _, activity ->
                    EmployeeRow(activity)
                }

                item(key = "spacer_before_checkins") {
                    Spacer(Modifier.height(8.dp))
                }
                item(key = "checkin_header") {
                    SectionHeader("Check-in conditions")
                }

                itemsIndexed(
                    data.checkIns,
                    key = { index, record -> "truck_${record.truck.id}_$index" }
                ) { _, record ->
                    CheckInRow(record)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ConditionBreakdownCard(breakdown: Map<VehicleCondition, Int>) {
    val total = breakdown.values.sum().coerceAtLeast(1)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Conditions on arrival",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(12.dp))
            VehicleCondition.entries.forEach { condition ->
                val count = breakdown[condition] ?: 0
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        conditionColor(condition),
                                        RoundedCornerShape(50)
                                    ),
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                condition.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            "$count",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { count / total.toFloat() },
                        color = conditionColor(condition),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmployeeRow(activity: GetReportsUseCase.EmployeeActivity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        activity.employee.name.take(1).uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        activity.employee.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        activity.employee.role.name.lowercase()
                            .replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBlock("Tasks done", activity.completedTaskCount.toString())
                StatBlock("Notes", activity.notesCount.toString())
                StatBlock("Check-ins", activity.checkedInTrucksCount.toString())
            }
        }
    }
}

@Composable
private fun CheckInRow(record: GetReportsUseCase.CheckInRecord) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    record.truck.plateNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    record.truck.condition.displayName,
                    color = conditionColor(record.truck.condition),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                "${record.truck.make} ${record.truck.model} • ${record.truck.customerName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Checked in by ${record.checkedInBy} on ${formatTimestamp(record.truck.checkedInAt)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Odometer: %,d km".format(record.truck.odometerKm),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (record.truck.conditionNotes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    record.truck.conditionNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (record.taskTotal > 0) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Tasks: ${record.taskDone} / ${record.taskTotal} done",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun conditionColor(condition: VehicleCondition): Color = when (condition) {
    VehicleCondition.EXCELLENT -> Color(0xFF1B7F3B)
    VehicleCondition.GOOD -> Color(0xFF4F8F2A)
    VehicleCondition.FAIR -> Color(0xFFCB8B0F)
    VehicleCondition.POOR -> Color(0xFFB3261E)
}