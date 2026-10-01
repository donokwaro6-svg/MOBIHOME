package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AuthUser
import com.example.model.UserRole
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.viewmodel.MobiHomeViewModel

@Composable
fun RoleChangeModalDialog(
    user: AuthUser,
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val targetRole = if (user.role == UserRole.PROPERTY_SEEKER) {
        UserRole.PROPERTY_ADMIN
    } else {
        UserRole.PROPERTY_SEEKER
    }

    var fullName by remember { mutableStateOf(user.displayName) }
    var email by remember { mutableStateOf(user.email) }
    var reason by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Count words in reason
    val wordsList = remember(reason) {
        reason.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    }
    val wordCount = wordsList.size
    val isWordCountMet = wordCount >= 50

    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .testTag("role_change_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MobiCoralPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = MobiCoralPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Request Role Change",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Account transition verification",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        modifier = Modifier.testTag("role_change_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Current vs Target Role banner
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Current Role",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (user.role == UserRole.PROPERTY_ADMIN) "🛡️ Property Admin" else "🔍 Property Seeker",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "➔",
                                fontSize = 18.sp,
                                color = MobiCoralPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Requested Role",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (targetRole == UserRole.PROPERTY_ADMIN) "🛡️ Property Admin" else "🔍 Property Seeker",
                                    fontWeight = FontWeight.Bold,
                                    color = MobiCoralPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Security & Policy notice
                    Text(
                        text = "To preserve platform trust and prevent unauthorized access, role changes require identity confirmation, a detailed motivation (minimum 50 words), and password re-authentication.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    // Error Message Banner (if any)
                    if (errorMessage != null) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("role_change_error_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 1. Full Names
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            errorMessage = null
                        },
                        label = { Text("Full Names *") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MobiCoralPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_change_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 2. Account Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Account Email *") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MobiCoralPrimary)
                        },
                        supportingText = {
                            Text("Must match your registered email: ${user.email}", fontSize = 11.sp)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_change_email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 3. Reason for changing roles (minimum 50 words)
                    Column {
                        OutlinedTextField(
                            value = reason,
                            onValueChange = {
                                reason = it
                                errorMessage = null
                            },
                            label = { Text("Reason for Changing Roles (Min 50 Words) *") },
                            placeholder = {
                                Text(
                                    "Explain why you are changing roles, your real estate or tenancy background, properties you manage or requirements, and how you will use this role on MobiHome...",
                                    fontSize = 12.sp
                                )
                            },
                            minLines = 4,
                            maxLines = 7,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("role_change_reason_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isWordCountMet) MobiEmerald else MobiCoralPrimary,
                                unfocusedBorderColor = if (isWordCountMet) MobiEmerald else MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Word count tracker badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isWordCountMet) "Word count criteria satisfied" else "Minimum 50 words required",
                                fontSize = 11.sp,
                                color = if (isWordCountMet) MobiEmerald else MaterialTheme.colorScheme.error,
                                fontWeight = if (isWordCountMet) FontWeight.SemiBold else FontWeight.Normal
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isWordCountMet) MobiEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "$wordCount / 50 words",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWordCountMet) MobiEmerald else MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // 4. Account Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Account Password *") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MobiCoralPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        supportingText = {
                            Text("Your account password is required for security verification.", fontSize = 11.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_change_password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "Please enter your full name."
                                return@Button
                            }
                            if (email.isBlank() || !email.trim().equals(user.email.trim(), ignoreCase = true)) {
                                errorMessage = "Email must match your registered account (${user.email})."
                                return@Button
                            }
                            if (wordCount < 50) {
                                errorMessage = "Reason is too short ($wordCount words). Please write at least 50 words."
                                return@Button
                            }
                            if (password.isBlank()) {
                                errorMessage = "Account password is required."
                                return@Button
                            }

                            isSubmitting = true
                            errorMessage = null

                            viewModel.submitRoleChangeRequest(
                                fullName = fullName,
                                email = email,
                                reason = reason,
                                password = password,
                                targetRole = targetRole
                            ) { success, msg ->
                                isSubmitting = false
                                if (success) {
                                    onSuccess(msg)
                                    onDismiss()
                                } else {
                                    errorMessage = msg
                                }
                            }
                        },
                        enabled = !isSubmitting && fullName.isNotBlank() && email.isNotBlank() && isWordCountMet && password.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("role_change_submit_button")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying...")
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify & Change")
                        }
                    }
                }
            }
        }
    }
}
