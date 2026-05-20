package com.valentinesgarage.app.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinesgarage.app.R
import com.valentinesgarage.app.ui.components.AurevargFooter
import com.valentinesgarage.app.ui.session.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    sessionViewModel: SessionViewModel,
    onBack: () -> Unit,
    onProfile: () -> Unit,
    onSignedOut: () -> Unit,
) {
    val employee by sessionViewModel.currentEmployee.collectAsStateWithLifecycle()
    var showAboutDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProfileSummary(
                name = employee?.name.orEmpty(),
                roleLabel = employee?.role?.displayName.orEmpty(),
                username = employee?.username.orEmpty(),
            )

            SettingsRow(
                icon = Icons.Default.Person,
                title = "Profile & password",
                subtitle = "Update your details or change your password",
                onClick = onProfile,
            )
            SettingsRow(
                icon = Icons.Default.Shield,
                title = "Security",
                subtitle = "Passwords are stored hashed with a per-user salt",
                onClick = { showSecurityDialog = true },
            )
            SettingsRow(
                icon = Icons.Default.SupportAgent,
                title = "Support",
                subtitle = "Reach the workshop owner for account help",
                onClick = { showSupportDialog = true },
            )
            SettingsRow(
                icon = Icons.Default.Description,
                title = "Terms of Service",
                subtitle = "Rules for using Valentine's Garage app",
                onClick = { showTermsDialog = true },
            )
            SettingsRow(
                icon = Icons.Default.Policy,
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = { showPrivacyDialog = true },
            )

            Spacer(Modifier.height(8.dp))

            // ── About & Sign out side by side ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Compact About card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showAboutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.garage_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "About",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                // Sign out button
                FilledTonalButton(
                    onClick = {
                        sessionViewModel.signOut()
                        onSignedOut()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign out", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.weight(1f))
            // Aurevarg footer - clickable and centralized
            AurevargFooter()
            Spacer(Modifier.height(8.dp))
        }
    }

    // ── Security dialog ────────────────────────────────────
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            icon = {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            title = { Text("Security") },
            text = {
                Text(
                    "All passwords are salted and hashed using iterated SHA‑256 (12,000 rounds) " +
                            "with a unique 16‑byte salt per user.\n\n" +
                            "This means your password is never stored in plain text " +
                            "and cannot be recovered – even by the app owner.\n\n" +
                            "If you forget your password, contact the shop owner to have your account reset.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Close")
                }
            },
        )
    }

    // ── About dialog ────────────────────────────────────────
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.garage_logo),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
            },
            title = { Text("Valentine's Garage") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "A workshop assistant built for Valentine's Garage to track truck check-ins, " +
                                "coordinate repairs across mechanics, and keep an honest record of every job. " +
                                "Includes anti-misuse safeguards (odometer & condition logged at hand-over).\n\n" +
                                "Version 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(
                        onClick = {
                            showAboutDialog = false
                            showTermsDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Terms of Service", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(
                        onClick = {
                            showAboutDialog = false
                            showPrivacyDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Privacy Policy", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            },
        )
    }

    // ── Terms of Service dialog ─────────────────────────────
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service") },
            text = {
                Text(
                    "This app is an internal tool for Valentine's Garage.\n\n" +
                            "By using this app, you agree not to misuse the service, " +
                            "to accurately record repairs and check‑ins, and to respect " +
                            "the privacy of customer data.\n\n" +
                            "All activity is logged and may be reviewed by the shop owner.\n\n" +
                            "Last updated: 27 April 2026",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            },
        )
    }

    // ── Privacy Policy dialog ───────────────────────────────
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Text(
                    "Your personal data (name, email, phone) is stored securely " +
                            "on the device and is only accessible to the shop owner.\n\n" +
                            "We do not share your data with third parties. " +
                            "Passwords are hashed with a per‑user salt.\n\n" +
                            "For any privacy concerns, contact support@valgarage.com.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close")
                }
            },
        )
    }

    // ── Support dialog ──────────────────────────────────────
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            icon = {
                Icon(
                    Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            title = { Text("Support") },
            text = {
                Text(
                    "For account help or workshop questions, please contact:\n\n" +
                            "support@valgarage.com",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text("Close")
                }
            },
        )
    }
}

// ── Helper composables ────────────────────────
@Composable
private fun ProfileSummary(name: String, roleLabel: String, username: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    name.ifBlank { "Signed out" },
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    "$roleLabel • @$username",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (onClick != null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}