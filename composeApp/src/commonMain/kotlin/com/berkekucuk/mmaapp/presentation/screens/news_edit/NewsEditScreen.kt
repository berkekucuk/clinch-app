package com.berkekucuk.mmaapp.presentation.screens.news_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import com.berkekucuk.mmaapp.presentation.screens.profile_edit.SaveButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsEditScreenRoot(
    viewModel: NewsEditViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is NewsEditNavigationEvent.Back -> onNavigateBack()
            }
        }
    }

    NewsEditScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsEditScreen(
    state: NewsEditUiState,
    onAction: (NewsEditUiAction) -> Unit
) {
    // Theme & Resources
    val focusManager = LocalFocusManager.current

    // Compose Core States
    val snackbarHostState = remember { SnackbarHostState() }

    // UI Data & Definitions
    val errorMessage = AppTheme.strings.mapError(state.error)

    AppSnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(NewsEditUiAction.OnMessageShown) },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    AppSnackbar(
                        snackbarData = snackbarData,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppTheme.strings.newsEditTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(NewsEditUiAction.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppTheme.strings.contentDescriptionBack
                        )
                    }
                },
                actions = {
                    SaveButton(
                        text = AppTheme.strings.newsEditSave,
                        onClick = {
                            focusManager.clearFocus()
                            onAction(NewsEditUiAction.OnSaveClicked)
                        },
                        isSaving = state.isSaving,
                        enabled = state.canSave,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.topBarBackground,
                    navigationIconContentColor = AppTheme.colors.textPrimary,
                    titleContentColor = AppTheme.colors.textPrimary,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NewsEditField(
                value = state.titleTr,
                label = AppTheme.strings.newsEditTitleTr,
                enabled = state.isLoaded && !state.isSaving,
                onValueChange = { onAction(NewsEditUiAction.OnTitleTrChanged(it)) },
            )
            NewsEditField(
                value = state.summaryTr,
                label = AppTheme.strings.newsEditSummaryTr,
                enabled = state.isLoaded && !state.isSaving,
                minLines = 6,
                onValueChange = { onAction(NewsEditUiAction.OnSummaryTrChanged(it)) },
            )
            NewsEditField(
                value = state.titleEn,
                label = AppTheme.strings.newsEditTitleEn,
                enabled = state.isLoaded && !state.isSaving,
                onValueChange = { onAction(NewsEditUiAction.OnTitleEnChanged(it)) },
            )
            NewsEditField(
                value = state.summaryEn,
                label = AppTheme.strings.newsEditSummaryEn,
                enabled = state.isLoaded && !state.isSaving,
                minLines = 6,
                onValueChange = { onAction(NewsEditUiAction.OnSummaryEnChanged(it)) },
            )
        }
    }
}

@Composable
private fun NewsEditField(
    value: String,
    label: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label, style = TextStyle(fontSize = 14.sp)) },
        textStyle = TextStyle(fontSize = 15.sp),
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppTheme.colors.textPrimary,
            unfocusedTextColor = AppTheme.colors.textPrimary,
            disabledTextColor = AppTheme.colors.textSecondary,
            focusedBorderColor = AppTheme.colors.textSecondary,
            unfocusedBorderColor = AppTheme.colors.cardBorder,
            disabledBorderColor = AppTheme.colors.cardBorder,
            focusedLabelColor = AppTheme.colors.textSecondary,
            unfocusedLabelColor = AppTheme.colors.textSecondary,
            disabledLabelColor = AppTheme.colors.textSecondary,
            cursorColor = AppTheme.colors.textPrimary,
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
