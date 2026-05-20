package com.valentinesgarage.app.ui.login

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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinesgarage.app.R
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.ui.session.SessionViewModel

@Composable
fun LoginScreen(
    sessionViewModel: SessionViewModel,
    onSignedIn: (Employee) -> Unit,
    onRegister: () -> Unit,
    flashMessage: String? = null,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(flashMessage) {
        if (flashMessage != null) viewModel.showMessage(flashMessage)
    }

    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF1C1B1F), Color(0xFF5C6770))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.garage_logo),
                contentDescription = "Valentine's Garage",
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Valentine's Garage",
                color = Color(0xFFFAF6F1),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )

            Spacer(Modifier.height(32.dp))

            if (state.message != null) {
                Text(
                    state.message!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    color = Color(0xFFFAF6F1),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::setUsername,
                label = { Text("Username") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Next,
                ),
                isError = state.error != null,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors(),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::setPassword,
                label = { Text("Password") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            if (state.showPassword) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (state.showPassword) "Hide password"
                            else "Show password",
                            tint = Color(0xFFFAF6F1).copy(alpha = 0.8f),
                        )
                    }
                },
                visualTransformation = if (state.showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                isError = state.error != null,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors(),
            )

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
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

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.submit { employee ->
                        sessionViewModel.setSignedIn(employee)
                        onSignedIn(employee)
                    }
                },
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
                    Text("Sign in", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "New here?",
                    color = Color(0xFFFAF6F1).copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onRegister) {
                    Text("Create an account", color = Color(0xFFF2A516))
                }
            }
        }
    }
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
    errorBorderColor = Color(0xFFFFF3E0),
    errorLabelColor = Color(0xFFFFF3E0),
    errorLeadingIconColor = Color(0xFFFFF3E0),
    errorTrailingIconColor = Color(0xFFFFF3E0),
)