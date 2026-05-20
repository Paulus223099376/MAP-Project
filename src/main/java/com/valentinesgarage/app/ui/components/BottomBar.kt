package com.valentinesgarage.app.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.ui.navigation.Routes

data class BottomTab(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
)

object BottomTabs {
    val Dashboard = BottomTab(Routes.DASHBOARD, Icons.Default.Dashboard, "Dashboard")
    val Trucks    = BottomTab(Routes.TRUCKS, Icons.Default.LocalShipping, "Trucks")
    val Reports   = BottomTab(Routes.REPORTS, Icons.Default.Assessment, "Reports")
    val Profile   = BottomTab(Routes.PROFILE, Icons.Default.Person, "Me")
    val Settings  = BottomTab(Routes.SETTINGS, Icons.Default.Settings, "Settings")

    fun forRole(role: EmployeeRole): List<BottomTab> = when (role) {
        EmployeeRole.OWNER    -> listOf(Dashboard, Trucks, Reports, Profile, Settings)
        EmployeeRole.MECHANIC -> listOf(Dashboard, Trucks, Profile, Settings)
    }
}

private val FloatingBarShape = RoundedCornerShape(32.dp)

@Composable
fun GarageBottomBar(
    role: EmployeeRole,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val tabs   = BottomTabs.forRole(role)
    val isDark = isSystemInDarkTheme()

    // Solid colors instead of transparent with alpha
    val solidBackground = if (isDark)
        Color(0xFF1C1C1E)
    else
        Color(0xFFF2F2F7)

    val solidBorder = if (isDark)
        Color(0xFF2C2C2E)
    else
        Color(0xFFE5E5EA)

    val shadowColor = if (isDark)
        Color.Black.copy(alpha = 0.55f)
    else
        Color.Black.copy(alpha = 0.18f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 22.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // ── Drop shadow ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(
                    elevation = 24.dp,
                    shape = FloatingBarShape,
                    ambientColor = shadowColor,
                    spotColor = shadowColor,
                    clip = false,
                ),
        )

        // ── Solid background layer ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .clip(FloatingBarShape)
                .background(solidBackground)
                .border(0.6.dp, solidBorder, FloatingBarShape),
        )

        // ── Navigation icons layer ────────────────────────────────────────
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .clip(FloatingBarShape),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0),
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { if (currentRoute != tab.route) onNavigate(tab.route) },
                    icon = {
                        // Wrap icon in Box to center it perfectly
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.height(66.dp) // Match bar height
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier
                                    .then(
                                        if (selected)
                                            Modifier.graphicsLayer {
                                                scaleX = 1.12f
                                                scaleY = 1.12f
                                                transformOrigin = TransformOrigin.Center
                                            }
                                        else Modifier
                                    ),
                            )
                        }
                    },
                    label = null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        // Remove indicator color entirely - no background on click
                        indicatorColor      = Color.Transparent,
                    ),
                )
            }
        }
    }
}