package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AuthState
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.viewmodel.MobiHomeViewModel

enum class AuthMode {
    SIGN_IN,
    REGISTER,
    FORGOT_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    viewModel: MobiHomeViewModel,
    initialMode: AuthMode = AuthMode.SIGN_IN,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentMode by remember { mutableStateOf(initialMode) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(viewModel.getLastUsedEmail()) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Auto-dismiss on successful authentication
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onSuccess()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearAuthError()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("auth_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MobiCoralPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MobiCoralPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MobiHome Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.clearAuthError()
                        onDismiss()
                    },
                    modifier = Modifier.testTag("auth_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle & Title
            Text(
                text = when (currentMode) {
                    AuthMode.SIGN_IN -> "Welcome Back"
                    AuthMode.REGISTER -> "Create MobiHome Account"
                    AuthMode.FORGOT_PASSWORD -> "Reset Your Password"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = when (currentMode) {
                    AuthMode.SIGN_IN -> "Sign in with Google or your email to access your trips, wishlists, and host portal."
                    AuthMode.REGISTER -> "Join millions of travelers discovering luxury villas and stays worldwide."
                    AuthMode.FORGOT_PASSWORD -> "Enter your email address and we'll send you a password recovery link."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            // Auth Tabs (Sign In vs Register)
            if (currentMode != AuthMode.FORGOT_PASSWORD) {
                TabRow(
                    selectedTabIndex = if (currentMode == AuthMode.SIGN_IN) 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[if (currentMode == AuthMode.SIGN_IN) 0 else 1]),
                            color = MobiCoralPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("auth_tabs")
                ) {
                    Tab(
                        selected = currentMode == AuthMode.SIGN_IN,
                        onClick = {
                            currentMode = AuthMode.SIGN_IN
                            localError = null
                            viewModel.clearAuthError()
                        },
                        text = {
                            Text(
                                text = "Sign In",
                                fontWeight = if (currentMode == AuthMode.SIGN_IN) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = currentMode == AuthMode.REGISTER,
                        onClick = {
                            currentMode = AuthMode.REGISTER
                            localError = null
                            viewModel.clearAuthError()
                        },
                        text = {
                            Text(
                                text = "Register",
                                fontWeight = if (currentMode == AuthMode.REGISTER) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Google Sign-In with Credential Manager Button
            if (currentMode != AuthMode.FORGOT_PASSWORD) {
                OutlinedButton(
                    onClick = {
                        localError = null
                        viewModel.clearAuthError()
                        viewModel.signInWithGoogle(context) { success, msg ->
                            if (success) {
                                onSuccess()
                                onDismiss()
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("google_sign_in_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google Color Logo Symbol
                        GoogleLogoIcon(modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (currentMode == AuthMode.SIGN_IN) "Continue with Google" else "Sign up with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Divider "Or with email"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = "or with email",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Error Feedback Banner
            val currentAuthState = authState
            val activeError = localError ?: (currentAuthState as? AuthState.Error)?.message
            if (activeError != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (activeError.contains("register", ignoreCase = true) && currentMode == AuthMode.SIGN_IN) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    currentMode = AuthMode.REGISTER
                                    localError = null
                                    viewModel.clearAuthError()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Switch to Register", fontSize = 11.sp)
                            }
                        } else if (activeError.contains("already", ignoreCase = true) && currentMode == AuthMode.REGISTER) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    currentMode = AuthMode.SIGN_IN
                                    localError = null
                                    viewModel.clearAuthError()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Switch to Sign In", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Success feedback message (e.g. Password reset link sent)
            if (feedbackMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MobiEmerald.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = feedbackMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MobiEmerald,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(14.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Input Fields
            if (currentMode == AuthMode.REGISTER) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("e.g. Jane Doe") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MobiCoralPrimary,
                        focusedLabelColor = MobiCoralPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_input_name")
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = null
                },
                label = { Text("Email Address") },
                placeholder = { Text("you@example.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MobiCoralPrimary,
                    focusedLabelColor = MobiCoralPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_input_email")
            )

            if (currentMode != AuthMode.FORGOT_PASSWORD) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        localError = null
                    },
                    label = { Text("Password") },
                    placeholder = { Text("Minimum 6 characters") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (currentMode == AuthMode.REGISTER) ImeAction.Next else ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MobiCoralPrimary,
                        focusedLabelColor = MobiCoralPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_input_password")
                )
            }

            if (currentMode == AuthMode.REGISTER) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Re-enter password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MobiCoralPrimary,
                        focusedLabelColor = MobiCoralPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_input_confirm_password")
                )
            }

            // Forgot password link
            if (currentMode == AuthMode.SIGN_IN) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = {
                            currentMode = AuthMode.FORGOT_PASSWORD
                            localError = null
                            feedbackMessage = null
                        },
                        modifier = Modifier.testTag("auth_forgot_password_button")
                    ) {
                        Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MobiCoralPrimary
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Submit Button
            val isAuthenticating = currentAuthState is AuthState.Authenticating
            Button(
                onClick = {
                    localError = null
                    feedbackMessage = null
                    when (currentMode) {
                        AuthMode.SIGN_IN -> {
                            if (email.isBlank() || password.isBlank()) {
                                localError = "Please enter both email and password."
                                return@Button
                            }
                            viewModel.signInWithEmail(email, password) { success, _ ->
                                if (success) {
                                    onSuccess()
                                    onDismiss()
                                }
                            }
                        }
                        AuthMode.REGISTER -> {
                            if (email.isBlank() || password.isBlank()) {
                                localError = "Please fill in all required fields."
                                return@Button
                            }
                            if (password.length < 6) {
                                localError = "Password must be at least 6 characters."
                                return@Button
                            }
                            if (password != confirmPassword) {
                                localError = "Passwords do not match."
                                return@Button
                            }
                            viewModel.signUpWithEmail(name, email, password) { success, _ ->
                                if (success) {
                                    onSuccess()
                                    onDismiss()
                                }
                            }
                        }
                        AuthMode.FORGOT_PASSWORD -> {
                            if (email.isBlank()) {
                                localError = "Please enter your email address."
                                return@Button
                            }
                            viewModel.sendPasswordReset(email) { success, msg ->
                                if (success) {
                                    feedbackMessage = msg
                                } else {
                                    localError = msg
                                }
                            }
                        }
                    }
                },
                enabled = !isAuthenticating,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MobiCoralPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_submit_button")
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = when (currentMode) {
                            AuthMode.SIGN_IN -> "Sign In"
                            AuthMode.REGISTER -> "Create Account"
                            AuthMode.FORGOT_PASSWORD -> "Send Reset Link"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (currentMode == AuthMode.FORGOT_PASSWORD) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        currentMode = AuthMode.SIGN_IN
                        localError = null
                        feedbackMessage = null
                    },
                    modifier = Modifier.testTag("auth_back_to_sign_in_button")
                ) {
                    Text("Back to Sign In", color = MobiCoralPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.Transparent
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
            val center = size / 2f
            val radius = size.minDimension / 2f
            drawCircle(
                color = Color(0xFF4285F4),
                radius = radius * 0.95f
            )
            drawCircle(
                color = Color.White,
                radius = radius * 0.65f
            )
            drawCircle(
                color = Color(0xFF4285F4),
                radius = radius * 0.35f
            )
        }
    }
}
