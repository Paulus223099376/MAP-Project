package com.valentinesgarage.app.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinesgarage.app.R
import com.valentinesgarage.app.domain.model.Employee

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: (Employee) -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF1C1B1F), Color(0xFF5C6770))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFAF6F1)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.garage_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Create account",
                    color = Color(0xFFFAF6F1),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                FormField(
                    label = "Full name",
                    value = state.name,
                    onValueChange = viewModel::setName,
                    leading = Icons.Default.Person,
                    capitalization = KeyboardCapitalization.Words,
                )

                FormField(
                    label = "Username",
                    value = state.username,
                    onValueChange = viewModel::setUsername,
                    leading = Icons.Default.Person,
                    capitalization = KeyboardCapitalization.None,
                    helper = "Lowercase letters, numbers, dot, underscore",
                )

                FormField(
                    label = "Email",
                    value = state.email,
                    onValueChange = viewModel::setEmail,
                    leading = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    helper = "Optional but recommended",
                )

                FormField(
                    label = "Phone",
                    value = state.phone,
                    onValueChange = viewModel::setPhone,
                    leading = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    helper = "Optional",
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::setPassword,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                if (state.showPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color(0xFFFAF6F1).copy(alpha = 0.8f),
                            )
                        }
                    },
                    visualTransformation = if (state.showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    singleLine = true,
                    supportingText = {
                        Text(
                            "At least 6 characters with letters and numbers",
                            color = Color(0xFFFAF6F1).copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                )

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::setConfirmPassword,
                    label = { Text("Confirm password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (state.showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                )

                if (state.error != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFFFF3E0),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            state.error!!,
                            color = Color(0xFFFFF3E0),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                Button(
                    onClick = { viewModel.submit(onRegistered) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !state.submitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2A516),
                        contentColor = Color(0xFF1C1B1F),
                    ),
                ) {
                    if (state.submitting) {
                        CircularProgressIndicator(
                            color = Color(0xFF1C1B1F),
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(20.dp),
                        )
                    } else {
                        Text("Create account", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leading: androidx.compose.ui.graphics.vector.ImageVector,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    keyboardType: KeyboardType = KeyboardType.Text,
    helper: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leading, contentDescription = null) },
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = ImeAction.Next,
        ),
        singleLine = true,
        supportingText = {
            if (helper != null) {
                Text(
                    helper,
                    color = Color(0xFFFAF6F1).copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors(),
    )
}

@Composable
private fun fieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFFFAF6F1),
    unfocusedTextColor = Color(0xFFFAF6F1),
    focusedLabelColor = Color(0xFFFAF6F1).copy(alpha = 0.8f),
    unfocusedLabelColor = Color(0xFFFAF6F1).copy(alpha = 0.6f),
    focusedBorderColor = Color(0xFFFAF6F1),
    unfocusedBorderColor = Color(0xFFFAF6F1).copy(alpha = 0.4f),
    cursorColor = Color(0xFFFAF6F1),
    focusedLeadingIconColor = Color(0xFFFAF6F1),
    unfocusedLeadingIconColor = Color(0xFFFAF6F1).copy(alpha = 0.6f),
    focusedTrailingIconColor = Color(0xFFFAF6F1),
    unfocusedTrailingIconColor = Color(0xFFFAF6F1).copy(alpha = 0.6f),
    focusedSupportingTextColor = Color(0xFFFAF6F1).copy(alpha = 0.6f),
    unfocusedSupportingTextColor = Color(0xFFFAF6F1).copy(alpha = 0.4f),
    errorBorderColor = Color(0xFFFFF3E0),
    errorLabelColor = Color(0xFFFFF3E0),
    errorLeadingIconColor = Color(0xFFFFF3E0),
    errorTrailingIconColor = Color(0xFFFFF3E0),
    errorSupportingTextColor = Color(0xFFFFF3E0),
)