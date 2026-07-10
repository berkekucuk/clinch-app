package com.berkekucuk.mmaapp.presentation.screens.user_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.ErrorBox
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserSearchScreenRoot(
    viewModel: UserSearchViewModel = koinViewModel(),
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is UserSearchNavigationEvent.ToUserProfile -> onNavigateToUserProfile(event.userId)
                is UserSearchNavigationEvent.Back -> onNavigateBack()
            }
        }
    }

    UserSearchScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    state: UserSearchUiState,
    onAction: (UserSearchUiAction) -> Unit,
) {
    // 1. Theme & Resources
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // 2. Compose Core States
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(state.query, TextRange(state.query.length))) }

    // 3. UI Data & Definitions
    LaunchedEffect(state.query) {
        if (textFieldValue.text != state.query) {
            textFieldValue = TextFieldValue(state.query, TextRange(state.query.length))
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = colors.pagerBackground,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            Column(
                modifier = Modifier.background(colors.rankingTopBar)
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onAction(UserSearchUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack,
                                tint = colors.textPrimary,
                            )
                        }
                    },
                    title = {
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                textFieldValue = newValue
                                onAction(UserSearchUiAction.OnQueryChanged(newValue.text))
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = colors.textPrimary,
                                fontSize = 18.sp,
                            ),
                            cursorBrush = SolidColor(colors.ufcRed),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (state.query.isEmpty()) {
                                        Text(
                                            text = strings.userSearchPlaceholder,
                                            color = colors.textSecondary,
                                            fontSize = 18.sp,
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                        )
                    },
                    actions = {
                        if (state.query.isNotEmpty()) {
                            IconButton(onClick = { onAction(UserSearchUiAction.OnClearQuery) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = colors.textPrimary,
                        titleContentColor = colors.textPrimary
                    ),
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.error != null -> {
                    strings.mapError(state.error)?.let { errorMessage ->
                        ErrorBox(
                            message = errorMessage,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
                state.results.isNotEmpty() -> {
                    UserSearchListContainer(
                        users = state.results,
                        onUserClicked = { userId -> onAction(UserSearchUiAction.OnUserClicked(userId)) }
                    )
                }
                state.isLoading -> {
                    // Do nothing, just wait.
                }
                state.query.length >= 2 -> {
                    ErrorBox(
                        message = strings.userSearchEmpty,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
