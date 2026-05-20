package com.valentinesgarage.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Core shimmer brush ─────────────────────────────────────────────────────────

@Composable
fun rememberShimmerBrush(
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface,
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_x",
    )
    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor.copy(alpha = 0.85f),
            baseColor,
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 600f, 0f),
    )
}

// ── Primitive skeleton blocks ──────────────────────────────────────────────────

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    radius: Dp = 8.dp,
    brush: Brush = rememberShimmerBrush(),
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))
            .background(brush),
    )
}

@Composable
fun ShimmerLine(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    height: Dp = 14.dp,
    radius: Dp = 6.dp,
    brush: Brush = rememberShimmerBrush(),
) {
    ShimmerBox(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height),
        radius = radius,
        brush = brush,
    )
}

// ── Skeleton for a StatCard (2 per row) ───────────────────────────────────────

@Composable
fun StatCardSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush = rememberShimmerBrush(),
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(modifier = Modifier.size(36.dp), radius = 50.dp, brush = brush)
                Spacer(Modifier.width(10.dp))
                ShimmerLine(widthFraction = 0.6f, height = 12.dp, brush = brush)
            }
            Spacer(Modifier.height(12.dp))
            ShimmerLine(widthFraction = 0.4f, height = 28.dp, brush = brush)
            Spacer(Modifier.height(6.dp))
            ShimmerLine(widthFraction = 0.7f, height = 10.dp, brush = brush)
        }
    }
}

// ── Skeleton for the TodayBanner ──────────────────────────────────────────────

@Composable
fun TodayBannerSkeleton(brush: Brush = rememberShimmerBrush()) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(modifier = Modifier.size(44.dp), radius = 50.dp, brush = brush)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerLine(widthFraction = 0.35f, height = 11.dp, brush = brush)
                Spacer(Modifier.height(6.dp))
                ShimmerLine(widthFraction = 0.75f, height = 22.dp, brush = brush)
                Spacer(Modifier.height(5.dp))
                ShimmerLine(widthFraction = 0.55f, height = 11.dp, brush = brush)
            }
        }
    }
}

// ── Skeleton for a RecentTruckRow ─────────────────────────────────────────────

@Composable
fun TruckRowSkeleton(brush: Brush = rememberShimmerBrush()) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(modifier = Modifier.size(12.dp), radius = 50.dp, brush = brush)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerLine(widthFraction = 0.4f, height = 16.dp, brush = brush)
                Spacer(Modifier.height(5.dp))
                ShimmerLine(widthFraction = 0.7f, height = 11.dp, brush = brush)
            }
            Spacer(Modifier.width(12.dp))
            ShimmerBox(
                modifier = Modifier
                    .height(24.dp)
                    .width(60.dp),
                radius = 50.dp,
                brush = brush,
            )
        }
    }
}

// ── Skeleton for TruckListScreen (full-size card) ─────────────────────────────

@Composable
fun TruckCardSkeleton(brush: Brush = rememberShimmerBrush()) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(modifier = Modifier.size(14.dp), radius = 50.dp, brush = brush)
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerLine(widthFraction = 0.35f, height = 20.dp, brush = brush)
                    Spacer(Modifier.height(5.dp))
                    ShimmerLine(widthFraction = 0.65f, height = 12.dp, brush = brush)
                }
                Spacer(Modifier.width(12.dp))
                ShimmerBox(
                    modifier = Modifier
                        .height(26.dp)
                        .width(80.dp),
                    radius = 50.dp,
                    brush = brush,
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(3) {
                    Column {
                        ShimmerLine(widthFraction = 0f, height = 10.dp, brush = brush, modifier = Modifier.width(60.dp))
                        Spacer(Modifier.height(4.dp))
                        ShimmerLine(widthFraction = 0f, height = 14.dp, brush = brush, modifier = Modifier.width(70.dp))
                    }
                }
            }
        }
    }
}

// ── Skeleton for a ConditionBreakdownCard (Reports) ───────────────────────────

@Composable
fun ConditionBreakdownSkeleton(brush: Brush = rememberShimmerBrush()) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            ShimmerLine(widthFraction = 0.55f, height = 18.dp, brush = brush)
            Spacer(Modifier.height(16.dp))
            repeat(4) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ShimmerBox(modifier = Modifier.size(10.dp), radius = 50.dp, brush = brush)
                            Spacer(Modifier.width(8.dp))
                            ShimmerLine(widthFraction = 0f, height = 12.dp, brush = brush, modifier = Modifier.width(70.dp))
                        }
                        ShimmerLine(widthFraction = 0f, height = 12.dp, brush = brush, modifier = Modifier.width(20.dp))
                    }
                    Spacer(Modifier.height(5.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        radius = 50.dp,
                        brush = brush,
                    )
                }
            }
        }
    }
}

// ── Skeleton for an EmployeeRow (Reports) ─────────────────────────────────────

@Composable
fun EmployeeRowSkeleton(brush: Brush = rememberShimmerBrush()) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(
                    modifier = Modifier
                        .height(34.dp)
                        .width(34.dp),
                    radius = 50.dp,
                    brush = brush,
                )
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerLine(widthFraction = 0.5f, height = 16.dp, brush = brush)
                    Spacer(Modifier.height(5.dp))
                    ShimmerLine(widthFraction = 0.3f, height = 11.dp, brush = brush)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ShimmerLine(widthFraction = 0f, height = 28.dp, brush = brush, modifier = Modifier.width(32.dp))
                        Spacer(Modifier.height(4.dp))
                        ShimmerLine(widthFraction = 0f, height = 10.dp, brush = brush, modifier = Modifier.width(55.dp))
                    }
                }
            }
        }
    }
}

// ── Skeleton for a CheckInRow (Reports) ───────────────────────────────────────

@Composable
fun CheckInRowSkeleton(brush: Brush = rememberShimmerBrush()) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerLine(widthFraction = 0f, height = 18.dp, brush = brush, modifier = Modifier.width(90.dp))
                ShimmerLine(widthFraction = 0f, height = 14.dp, brush = brush, modifier = Modifier.width(65.dp))
            }
            Spacer(Modifier.height(6.dp))
            ShimmerLine(widthFraction = 0.75f, height = 12.dp, brush = brush)
            Spacer(Modifier.height(10.dp))
            ShimmerLine(widthFraction = 0.85f, height = 11.dp, brush = brush)
            Spacer(Modifier.height(4.dp))
            ShimmerLine(widthFraction = 0.45f, height = 11.dp, brush = brush)
        }
    }
}

// ── Full-screen skeletons ──────────────────────────────────────────────────────

@Composable
fun DashboardSkeleton(padding: PaddingValues) {
    val brush = rememberShimmerBrush()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = padding.calculateTopPadding() + 12.dp,
            bottom = padding.calculateBottomPadding() + 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false,
    ) {
        item { TodayBannerSkeleton(brush) }
        item { ShimmerLine(widthFraction = 0.45f, height = 18.dp, brush = brush) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCardSkeleton(modifier = Modifier.weight(1f), brush = brush)
                StatCardSkeleton(modifier = Modifier.weight(1f), brush = brush)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCardSkeleton(modifier = Modifier.weight(1f), brush = brush)
                StatCardSkeleton(modifier = Modifier.weight(1f), brush = brush)
            }
        }
        // Completion card placeholder
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShimmerLine(widthFraction = 0.5f, height = 18.dp, brush = brush, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(12.dp))
                        ShimmerLine(widthFraction = 0f, height = 32.dp, brush = brush, modifier = Modifier.width(52.dp))
                    }
                    Spacer(Modifier.height(10.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth().height(8.dp), radius = 50.dp, brush = brush)
                    Spacer(Modifier.height(7.dp))
                    ShimmerLine(widthFraction = 0.55f, height = 11.dp, brush = brush)
                }
            }
        }
        item { ShimmerLine(widthFraction = 0.4f, height = 18.dp, brush = brush) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCardSkeleton(modifier = Modifier.weight(1f), brush = brush)
                StatCardSkeleton(modifier = Modifier.weight(1f), brush = brush)
            }
        }
        item { ShimmerLine(widthFraction = 0.38f, height = 18.dp, brush = brush) }
        repeat(3) {
            item { TruckRowSkeleton(brush) }
        }
    }
}

@Composable
fun ReportsSkeleton(padding: PaddingValues) {
    val brush = rememberShimmerBrush()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = padding.calculateTopPadding() + 12.dp,
            bottom = padding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        userScrollEnabled = false,
    ) {
        item { ConditionBreakdownSkeleton(brush) }
        item { ShimmerLine(widthFraction = 0.42f, height = 18.dp, brush = brush) }
        repeat(2) { item { EmployeeRowSkeleton(brush) } }
        item { ShimmerLine(widthFraction = 0.48f, height = 18.dp, brush = brush) }
        repeat(3) { item { CheckInRowSkeleton(brush) } }
    }
}

@Composable
fun TruckListSkeleton(padding: PaddingValues) {
    val brush = rememberShimmerBrush()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = padding.calculateTopPadding() + 4.dp,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false,
    ) {
        repeat(6) { item { TruckCardSkeleton(brush) } }
    }
}