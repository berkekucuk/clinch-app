package com.berkekucuk.mmaapp.presentation.screens.settings

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
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
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
    var notificationsEnabled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        notificationsEnabled = notificationStorage.load()
    }
    
    OnResumeEffect {
        scope.launch {
            notificationsEnabled = notificationStorage.load()
        }
    }

    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val currentLanguage = strings.language
    val currentMeasurementUnit = LocalMeasurementUnit.current
    val currentOddsFormat = LocalOddsFormat.current
    val currentThemeMode = LocalThemeMode.current
    val uriHandler = LocalUriHandler.current

    var activeDialog by remember { mutableStateOf<SettingsDialogType?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val onDismissRequest = { activeDialog = null }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.pagerBackground,
        topBar = {
            Column(
                modifier = Modifier.background(colors.topBarBackground)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.settingsTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = colors.textPrimary,
                        titleContentColor = colors.textPrimary,
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
                title = strings.settingsSectionTheme,
                subtitle = when (currentThemeMode) {
                    ThemeMode.LIGHT -> strings.settingsThemeLight
                    ThemeMode.DARK -> strings.settingsThemeDark
                },
                onClick = { activeDialog = SettingsDialogType.THEME }
            )

            SettingsCard(
                icon = Icons.Default.Language,
                title = strings.settingsSectionLanguage,
                subtitle = when (currentLanguage) {
                    AppLanguage.EN -> "English"
                    AppLanguage.TR -> "Türkçe"
                },
                onClick = { activeDialog = SettingsDialogType.LANGUAGE }
            )

            SettingsCard(
                icon = Icons.Default.AttachMoney,
                title = strings.settingsSectionOdds,
                subtitle = when (currentOddsFormat) {
                    OddsFormat.DECIMAL -> "Decimal"
                    OddsFormat.AMERICAN -> "American"
                },
                onClick = { activeDialog = SettingsDialogType.ODDS }
            )

            SettingsCard(
                icon = Icons.Default.Straighten,
                title = strings.settingsSectionMeasurements,
                subtitle = when (currentMeasurementUnit) {
                    MeasurementUnit.METRIC -> "Metric (cm)"
                    MeasurementUnit.IMPERIAL -> "Imperial (ft/in)"
                },
                onClick = { activeDialog = SettingsDialogType.MEASUREMENT }
            )

            SettingsCard(
                icon = if (notificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                title = strings.menuItemNotifications,
                subtitle = if (notificationsEnabled) strings.menuNotificationsEnabled else strings.menuNotificationsDisabled,
                onClick = { notificationStorage.openNotificationSettings() }
            )

            SettingsCard(
                icon = Icons.Default.Email,
                title = strings.settingsSectionSupport,
                subtitle = strings.settingsSectionSupportSub,
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
                    title = strings.settingsSectionBlockedUsers,
                    subtitle = strings.settingsSectionBlockedUsersSub,
                    onClick = onBlockedUsersClick
                )
            }
        }
    }

    if (activeDialog != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = colors.dropdownMenuBackground
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
                        SettingsDialogType.THEME -> strings.settingsSectionTheme
                        SettingsDialogType.LANGUAGE -> strings.settingsSectionLanguage
                        SettingsDialogType.ODDS -> strings.settingsSectionOdds
                        SettingsDialogType.MEASUREMENT -> strings.settingsSectionMeasurements
                        else -> ""
                    },
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (activeDialog) {
                    SettingsDialogType.THEME -> {
                        SettingsBottomSheetOption(
                            text = strings.settingsThemeLight,
                            isSelected = currentThemeMode == ThemeMode.LIGHT,
                            onClick = {
                                onThemeModeChange(ThemeMode.LIGHT)
                                onDismissRequest()
                            }
                        )
                        SettingsBottomSheetOption(
                            text = strings.settingsThemeDark,
                            isSelected = currentThemeMode == ThemeMode.DARK,
                            onClick = {
                                onThemeModeChange(ThemeMode.DARK)
                                onDismissRequest()
                            }
                        )
                    }
                    SettingsDialogType.LANGUAGE -> {
                        SettingsBottomSheetOption(
                            text = "English",
                            isSelected = currentLanguage == AppLanguage.EN,
                            onClick = {
                                onLanguageChange(AppLanguage.EN)
                                onDismissRequest()
                            }
                        )
                        SettingsBottomSheetOption(
                            text = "Türkçe",
                            isSelected = currentLanguage == AppLanguage.TR,
                            onClick = {
                                onLanguageChange(AppLanguage.TR)
                                onDismissRequest()
                            }
                        )
                    }
                    SettingsDialogType.ODDS -> {
                        SettingsBottomSheetOption(
                            text = "Decimal",
                            isSelected = currentOddsFormat == OddsFormat.DECIMAL,
                            onClick = {
                                onOddsFormatChange(OddsFormat.DECIMAL)
                                onDismissRequest()
                            }
                        )
                        SettingsBottomSheetOption(
                            text = "American",
                            isSelected = currentOddsFormat == OddsFormat.AMERICAN,
                            onClick = {
                                onOddsFormatChange(OddsFormat.AMERICAN)
                                onDismissRequest()
                            }
                        )
                    }
                    SettingsDialogType.MEASUREMENT -> {
                        SettingsBottomSheetOption(
                            text = "Metric (cm)",
                            isSelected = currentMeasurementUnit == MeasurementUnit.METRIC,
                            onClick = {
                                onMeasurementUnitChange(MeasurementUnit.METRIC)
                                onDismissRequest()
                            }
                        )
                        SettingsBottomSheetOption(
                            text = "Imperial (ft/in)",
                            isSelected = currentMeasurementUnit == MeasurementUnit.IMPERIAL,
                            onClick = {
                                onMeasurementUnitChange(MeasurementUnit.IMPERIAL)
                                onDismissRequest()
                            }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}