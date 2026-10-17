package com.berkekucuk.mmaapp.presentation.screens.profile_edit

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import com.berkekucuk.mmaapp.presentation.components.ErrorBox
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.*
import io.github.ismoy.imagepickerkmp.domain.extensions.loadBytes
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.compose.LocalPlatformContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.clip
import com.berkekucuk.mmaapp.core.utils.compressImageByteArray

@Composable
fun ProfileEditScreenRoot(
    viewModel: ProfileEditViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is ProfileEditNavigationEvent.Back -> onNavigateBack()
                is ProfileEditNavigationEvent.AccountDeleted -> onNavigateBack()
            }
        }
    }

    ProfileEditScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    state: ProfileEditUiState,
    onAction: (ProfileEditUiAction) -> Unit,
) {
    // Theme & Resources
    val context = LocalPlatformContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Compose Core States
    val showDeleteDialog = remember { mutableStateOf(false) }
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(),
            cropConfig = CropConfig(
                enabled = true,
                circularCrop = true
            ),
        )
    )

    // UI Data & Definitions
    val errorMessage = AppTheme.strings.mapError(state.error)

    val avatarModel = remember(state.selectedImageBytes, state.avatarUrl) {
        state.selectedImageBytes ?: state.avatarUrl
    }

    val imageRequest = remember(avatarModel) {
        ImageRequest.Builder(context)
            .data(avatarModel)
            .crossfade(true)
            .build()
    }

    val result = picker.result
    LaunchedEffect(result) {
        if (result is ImagePickerResult.Success) {
            val photo = result.photos.firstOrNull()
            if (photo != null) {
                val bytes = photo.loadBytes()
                val compressed = try {
                    compressImageByteArray(bytes)
                } catch (t: Throwable) {
                    bytes
                }
                onAction(ProfileEditUiAction.OnImageSelected(compressed))
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        topBar = {
            Column(
                modifier = Modifier.background(AppTheme.colors.topBarBackground)
            ){
                TopAppBar(
                    title = {
                        Text(
                            text = AppTheme.strings.profileEditTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        SaveButton(
                            text = AppTheme.strings.profileEditSaveChanges,
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                onAction(ProfileEditUiAction.OnSaveClicked)
                            },
                            isSaving = state.isSaving,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onAction(ProfileEditUiAction.OnBackClicked) }) {
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
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .background(AppTheme.colors.winnerFrame, CircleShape)
                            .border(1.5.dp, AppTheme.colors.topBarBackground, CircleShape)
                            .clickable {
                                picker.launchGallery()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = AppTheme.colors.white,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = AppTheme.strings.profileEditPersonalInfo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                label = { Text(AppTheme.strings.profileEditEmail, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AppTheme.colors.textSecondary,
                    unfocusedTextColor = AppTheme.colors.textSecondary,
                    focusedBorderColor = AppTheme.colors.cardBorder,
                    unfocusedBorderColor = AppTheme.colors.cardBorder,
                    focusedLabelColor = AppTheme.colors.textSecondary,
                    unfocusedLabelColor = AppTheme.colors.textSecondary,
                    cursorColor = Color.Transparent
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.fullName,
                onValueChange = { name -> onAction(ProfileEditUiAction.OnFullNameChanged(name)) },
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                label = { Text(AppTheme.strings.profileEditFullName, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)) },
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
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = { username -> onAction(ProfileEditUiAction.OnUsernameChanged(username)) },
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                label = { Text(AppTheme.strings.profileEditUsernameLabel, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)) },
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
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorBox(message = errorMessage)
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { showDeleteDialog.value = true }
                ) {
                    Text(
                        text = AppTheme.strings.profileEditDeleteAccount,
                        color = AppTheme.colors.ufcRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    if (showDeleteDialog.value) {
        AppAlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            onConfirmClick = {
                showDeleteDialog.value = false
                onAction(ProfileEditUiAction.OnDeleteAccountClicked)
            },
            title = AppTheme.strings.profileEditDeleteAccountTitle,
            text = AppTheme.strings.profileEditDeleteAccountConfirm,
            confirmText = AppTheme.strings.profileEditDeleteAccount,
            dismissText = AppTheme.strings.commonCancel,
            isDestructive = true
        )
    }
}