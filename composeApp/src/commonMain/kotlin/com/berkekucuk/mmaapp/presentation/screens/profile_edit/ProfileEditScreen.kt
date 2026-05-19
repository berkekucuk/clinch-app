package com.berkekucuk.mmaapp.presentation.screens.profile_edit

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
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
    val onBackClicked = remember(onAction) { { onAction(ProfileEditUiAction.OnBackClicked) } }
    val onFullNameChanged = remember(onAction) { { name: String -> onAction(ProfileEditUiAction.OnFullNameChanged(name)) } }
    val onUsernameChanged = remember(onAction) { { username: String -> onAction(ProfileEditUiAction.OnUsernameChanged(username)) } }
    val onSaveClicked = remember(onAction) { { onAction(ProfileEditUiAction.OnSaveClicked) } }
    val onDeleteAccountClicked = remember(onAction) { { onAction(ProfileEditUiAction.OnDeleteAccountClicked) } }

    val showDeleteDialog = remember { mutableStateOf(false) }
    val onDeleteDialogDismiss = remember { { showDeleteDialog.value = false } }
    val onDeleteConfirmed = remember(onAction) {
        {
            showDeleteDialog.value = false
            onDeleteAccountClicked()
        }
    }

    val context = LocalPlatformContext.current
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val focusManager = LocalFocusManager.current
    val errorMessage = strings.mapError(state.error)

    val avatarModel = remember(state.selectedImageBytes, state.avatarUrl) {
        state.selectedImageBytes ?: state.avatarUrl
    }

    val imageRequest = remember(avatarModel) {
        ImageRequest.Builder(context)
            .data(avatarModel)
            .crossfade(true)
            .build()
    }

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(),
            cropConfig = CropConfig(
                enabled = true,
                circularCrop = true
            ),
        )
    )
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
        containerColor = colors.pagerBackground,
        topBar = {
            Column(
                modifier = Modifier.background(colors.topBarBackground)
            ){
                TopAppBar(
                    title = {
                        Text(
                            text = strings.profileEditTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        SaveButton(
                            text = strings.profileEditSaveChanges,
                            onClick = onSaveClicked,
                            isSaving = state.isSaving,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
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
                            .background(colors.winnerFrame, CircleShape)
                            .border(1.5.dp, colors.topBarBackground, CircleShape)
                            .clickable {
                                picker.launchGallery()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = colors.white,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = strings.profileEditPersonalInfo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                label = { Text(strings.profileEditEmail, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textSecondary,
                    unfocusedTextColor = colors.textSecondary,
                    focusedBorderColor = colors.cardBorder,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedLabelColor = colors.textSecondary,
                    unfocusedLabelColor = colors.textSecondary,
                    cursorColor = Color.Transparent
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.fullName,
                onValueChange = onFullNameChanged,
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                label = { Text(strings.profileEditFullName, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    errorTextColor = colors.textPrimary,
                    focusedBorderColor = colors.winnerFrame,
                    unfocusedBorderColor = colors.cardBorder,
                    errorBorderColor = colors.ufcRed,
                    focusedLabelColor = colors.textPrimary,
                    unfocusedLabelColor = colors.textSecondary,
                    errorLabelColor = colors.ufcRed,
                    cursorColor = colors.textPrimary,
                    errorCursorColor = colors.ufcRed
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = onUsernameChanged,
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                label = { Text(strings.profileEditUsernameLabel, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    errorTextColor = colors.textPrimary,
                    focusedBorderColor = colors.winnerFrame,
                    unfocusedBorderColor = colors.cardBorder,
                    errorBorderColor = colors.ufcRed,
                    focusedLabelColor = colors.textPrimary,
                    unfocusedLabelColor = colors.textSecondary,
                    errorLabelColor = colors.ufcRed,
                    cursorColor = colors.textPrimary,
                    errorCursorColor = colors.ufcRed
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
                        text = strings.profileEditDeleteAccount,
                        color = colors.ufcRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    if (showDeleteDialog.value) {
        AppAlertDialog(
            onDismissRequest = onDeleteDialogDismiss,
            onConfirmClick = onDeleteConfirmed,
            title = strings.profileEditDeleteAccountTitle,
            text = strings.profileEditDeleteAccountConfirm,
            confirmText = strings.profileEditDeleteAccount,
            dismissText = strings.commonCancel,
            isDestructive = true
        )
    }
}