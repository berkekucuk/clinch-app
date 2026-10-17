package com.berkekucuk.mmaapp.presentation.screens.settings

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.presentation.LocalMeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.LocalThemeMode
import com.berkekucuk.mmaapp.core.presentation.MeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.OddsFormat
import com.berkekucuk.mmaapp.core.presentation.ThemeMode
import com.berkekucuk.mmaapp.core.storage.NotificationStorage
import com.berkekucuk.mmaapp.core.utils.OnResumeEffect
import org.koin.compose.koinInject
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.domain.enums.SettingsDialogType
import com.berkekucuk.mmaapp.domain.model.AuthState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    onBackClick: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onMeasurementUnitChange: (MeasurementUnit) -> Unit,
    onOddsFormatChange: (OddsFormat) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBlockedUsersClick: () -> Unit,
    notificationStorage: NotificationStorage = koinInject(),
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onBackClick = onBackClick,
        onLanguageChange = onLanguageChange,
        onMeasurementUnitChange = onMeasurementUnitChange,
        onOddsFormatChange = onOddsFormatChange,
        onThemeModeChange = onThemeModeChange,
        onBlockedUsersClick = onBlockedUsersClick,
        notificationStorage = notificationStorage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBackClick: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onMeasurementUnitChange: (MeasurementUnit) -> Unit,
    onOddsFormatChange: (OddsFormat) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBlockedUsersClick: () -> Unit,
    notificationStorage: NotificationStorage
) {
    // Theme & Resources
    val uriHandler = LocalUriHandler.current
    val currentLanguage = AppTheme.strings.language
    val currentMeasurementUnit = LocalMeasurementUnit.current
    val currentOddsFormat = LocalOddsFormat.current
    val currentThemeMode = LocalThemeMode.current

    // Compose Core States
    val scope = rememberCoroutineScope()
    var notificationsEnabled by remember { mutableStateOf(false) }
    var activeDialog by remember { mutableStateOf<SettingsDialogType?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        notificationsEnabled = notificationStorage.load()
    }
    
    OnResumeEffect {
        scope.launch {
            notificationsEnabled = notificationStorage.load()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        topBar = {
            Column(
                modifier = Modifier.background(AppTheme.colors.topBarBackground)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = AppTheme.strings.settingsTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
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
                    ),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsCard(
                icon = Icons.Default.DarkMode,
                title = AppTheme.strings.settingsSectionTheme,
                subtitle = when (currentThemeMode) {
                    ThemeMode.LIGHT -> AppTheme.strings.settingsThemeLight
                    ThemeMode.DARK -> AppTheme.strings.settingsThemeDark
                },
                onClick = { activeDialog = SettingsDialogType.THEME }
            )

            SettingsCard(
                icon = Icons.Default.Language,
                title = AppTheme.strings.settingsSectionLanguage,
                subtitle = when (currentLanguage) {
                    AppLanguage.EN -> "English"
                    AppLanguage.TR -> "Türkçe"
                },
                onClick = { activeDialog = SettingsDialogType.LANGUAGE }
            )

            SettingsCard(
                icon = Icons.Default.AttachMoney,
                title = AppTheme.strings.settingsSectionOdds,
                subtitle = when (currentOddsFormat) {
                    OddsFormat.DECIMAL -> "Decimal"
                    OddsFormat.AMERICAN -> "American"
                },
                onClick = { activeDialog = SettingsDialogType.ODDS }
            )

            SettingsCard(
                icon = Icons.Default.Straighten,
                title = AppTheme.strings.settingsSectionMeasurements,
                subtitle = when (currentMeasurementUnit) {
                    MeasurementUnit.METRIC -> "Metric (cm)"
                    MeasurementUnit.IMPERIAL -> "Imperial (ft/in)"
                },
                onClick = { activeDialog = SettingsDialogType.MEASUREMENT }
            )

            SettingsCard(
                icon = if (notificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                title = AppTheme.strings.menuItemNotifications,
                subtitle = if (notificationsEnabled) AppTheme.strings.menuNotificationsEnabled else AppTheme.strings.menuNotificationsDisabled,
                onClick = { notificationStorage.openNotificationSettings() }
            )

            SettingsCard(
                icon = Icons.Default.Email,
                title = AppTheme.strings.settingsSectionSupport,
                subtitle = AppTheme.strings.settingsSectionSupportSub,
                onClick = {
                    try {
                        uriHandler.openUri("mailto:clinchapp0@gmail.com")
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            )

            if (state.authState is AuthState.Authenticated) {
                SettingsCard(
                    icon = Icons.Default.Block,
                    title = AppTheme.strings.settingsSectionBlockedUsers,
                    subtitle = AppTheme.strings.settingsSectionBlockedUsersSub,
                    onClick = onBlockedUsersClick
                )
            }
        }
    }

    if (activeDialog != null) {
        ModalBottomSheet(
            onDismissRequest = { activeDialog = null },
            sheetState = sheetState,
            containerColor = AppTheme.colors.dropdownMenuBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (activeDialog) {
                        SettingsDialogType.THEME -> AppTheme.strings.settingsSectionTheme
                        SettingsDialogType.LANGUAGE -> AppTheme.strings.settingsSectionLanguage
                        SettingsDialogType.ODDS -> AppTheme.strings.settingsSectionOdds
                        SettingsDialogType.MEASUREMENT -> AppTheme.strings.settingsSectionMeasurements
                        else -> ""
                    },
                    color = AppTheme.colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (activeDialog) {
                    SettingsDialogType.THEME -> {
                        SettingsBottomSheetOption(
                            text = AppTheme.strings.settingsThemeLight,
                            isSelected = currentThemeMode == ThemeMode.LIGHT,
                            onClick = {
                                onThemeModeChange(ThemeMode.LIGHT)
                                activeDialog = null
                            }
                        )
                        SettingsBottomSheetOption(
                            text = AppTheme.strings.settingsThemeDark,
                            isSelected = currentThemeMode == ThemeMode.DARK,
                            onClick = {
                                onThemeModeChange(ThemeMode.DARK)
                                activeDialog = null
                            }
                        )
                    }
                    SettingsDialogType.LANGUAGE -> {
                        SettingsBottomSheetOption(
                            text = "English",
                            isSelected = currentLanguage == AppLanguage.EN,
                            onClick = {
                                onLanguageChange(AppLanguage.EN)
                                activeDialog = null
                            }
                        )
                        SettingsBottomSheetOption(
                            text = "Türkçe",
                            isSelected = currentLanguage == AppLanguage.TR,
                            onClick = {
                                onLanguageChange(AppLanguage.TR)
                                activeDialog = null
                            }
                        )
                    }
                    SettingsDialogType.ODDS -> {
                        SettingsBottomSheetOption(
                            text = "Decimal",
                            isSelected = currentOddsFormat == OddsFormat.DECIMAL,
                            onClick = {
                                onOddsFormatChange(OddsFormat.DECIMAL)
                                activeDialog = null
                            }
                        )
                        SettingsBottomSheetOption(
                            text = "American",
                            isSelected = currentOddsFormat == OddsFormat.AMERICAN,
                            onClick = {
                                onOddsFormatChange(OddsFormat.AMERICAN)
                                activeDialog = null
                            }
                        )
                    }
                    SettingsDialogType.MEASUREMENT -> {
                        SettingsBottomSheetOption(
                            text = "Metric (cm)",
                            isSelected = currentMeasurementUnit == MeasurementUnit.METRIC,
                            onClick = {
                                onMeasurementUnitChange(MeasurementUnit.METRIC)
                                activeDialog = null
                            }
                        )
                        SettingsBottomSheetOption(
                            text = "Imperial (ft/in)",
                            isSelected = currentMeasurementUnit == MeasurementUnit.IMPERIAL,
                            onClick = {
                                onMeasurementUnitChange(MeasurementUnit.IMPERIAL)
                                activeDialog = null
                            }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}