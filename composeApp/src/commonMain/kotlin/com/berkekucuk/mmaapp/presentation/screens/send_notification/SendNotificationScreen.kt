package com.berkekucuk.mmaapp.presentation.screens.send_notification

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import com.berkekucuk.mmaapp.domain.enums.NotificationTarget
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import com.berkekucuk.mmaapp.presentation.screens.profile_edit.SaveButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SendNotificationScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: SendNotificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                SendNotificationNavigationEvent.Back -> onNavigateBack()
            }
        }
    }

    SendNotificationScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendNotificationScreen(
    state: SendNotificationUiState,
    onAction: (SendNotificationUiAction) -> Unit,
) {
    // Theme & Resources
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Compose Core States
    val snackbarHostState = remember { SnackbarHostState() }

    // UI Data & Definitions
    val errorMessage = AppTheme.strings.mapError(state.error)

    AppSnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(SendNotificationUiAction.OnSnackbarDismissed) },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    AppSnackbar(
                        snackbarData = data,
                        isSuccess = false,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            Column(modifier = Modifier.background(AppTheme.colors.topBarBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Send Notification",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        SaveButton(
                            text = "Send",
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                onAction(SendNotificationUiAction.OnSendClicked)
                            },
                            isSaving = state.isSending,
                            enabled = state.canSend,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onAction(SendNotificationUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppTheme.strings.contentDescriptionBack,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = AppTheme.colors.textPrimary,
                        titleContentColor = AppTheme.colors.textPrimary,
                    )
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Notification",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NotificationTextField(
                value = state.title,
                onValueChange = { onAction(SendNotificationUiAction.OnTitleChanged(it)) },
                label = "Title",
            )
            Spacer(modifier = Modifier.height(16.dp))
            NotificationTextField(
                value = state.body,
                onValueChange = { onAction(SendNotificationUiAction.OnBodyChanged(it)) },
                label = "Body",
            )
            Spacer(modifier = Modifier.height(16.dp))
            NotificationTextField(
                value = state.eventId,
                onValueChange = { onAction(SendNotificationUiAction.OnEventIdChanged(it)) },
                label = "Event ID (optional)",
            )

            Spacer(modifier = Modifier.height(16.dp))
            TargetDropdown(
                selected = state.target,
                onSelected = { onAction(SendNotificationUiAction.OnTargetChanged(it)) },
            )

            if (state.target == NotificationTarget.USERS) {
                Spacer(modifier = Modifier.height(16.dp))
                NotificationTextField(
                    value = state.usernames,
                    onValueChange = { onAction(SendNotificationUiAction.OnUsernamesChanged(it)) },
                    label = "Usernames (comma separated)",
                    singleLine = false,
                    minLines = 2,
                )
            }
        }
    }

    if (state.showConfirmDialog) {
        AppAlertDialog(
            onDismissRequest = { onAction(SendNotificationUiAction.OnDismissDialog) },
            onConfirmClick = { onAction(SendNotificationUiAction.OnConfirmSend) },
            title = "Send notification?",
            text = "\"${state.title}\"\n${state.body}\n\nTarget: " +
                if (state.target == NotificationTarget.USERS) "${state.parsedUsernames.size} user(s)" else state.target.label,
            confirmText = "Send",
            dismissText = AppTheme.strings.commonCancel,
            isDestructive = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetDropdown(
    selected: NotificationTarget,
    onSelected: (NotificationTarget) -> Unit,
) {
    val textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            textStyle = textStyle,
            label = { Text("Target", style = textStyle) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary,
                focusedBorderColor = AppTheme.colors.winnerFrame,
                unfocusedBorderColor = AppTheme.colors.cardBorder,
                focusedLabelColor = AppTheme.colors.textPrimary,
                unfocusedLabelColor = AppTheme.colors.textSecondary,
                focusedTrailingIconColor = AppTheme.colors.textSecondary,
                unfocusedTrailingIconColor = AppTheme.colors.textSecondary,
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = AppTheme.colors.topBarBackground,
        ) {
            NotificationTarget.entries.forEach { target ->
                DropdownMenuItem(
                    text = { Text(target.label, style = textStyle, color = AppTheme.colors.textPrimary) },
                    onClick = {
                        onSelected(target)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun NotificationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    val textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        label = { Text(label, style = textStyle) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppTheme.colors.textPrimary,
            unfocusedTextColor = AppTheme.colors.textPrimary,
            errorTextColor = AppTheme.colors.textPrimary,
            focusedBorderColor = AppTheme.colors.winnerFrame,
            unfocusedBorderColor = AppTheme.colors.cardBorder,
            errorBorderColor = AppTheme.colors.ufcRed,
            focusedLabelColor = AppTheme.colors.textPrimary,
            unfocusedLabelColor = AppTheme.colors.textSecondary,
            errorLabelColor = AppTheme.colors.ufcRed,
            cursorColor = AppTheme.colors.textPrimary,
            errorCursorColor = AppTheme.colors.ufcRed
        ),
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
