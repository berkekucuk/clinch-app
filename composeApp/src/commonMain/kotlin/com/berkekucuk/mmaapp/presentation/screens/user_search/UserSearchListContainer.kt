package com.berkekucuk.mmaapp.presentation.screens.user_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.presentation.components.FighterPortrait
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.presentation.components.appClickable

@Composable
fun UserSearchListContainer(
    users: List<User>,
    onUserClicked: (String) -> Unit,
) {
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val colors = LocalAppColors.current

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    ListContainer(
        isRefreshing = false,
        onRefresh = {},
        listState = listState,
        contentPadding = PaddingValues(top = 8.dp),
        verticalSpacing = 0.dp,
        extraBottomPadding = navBarBottomPadding,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.fightItemBackground)
            ) {
                users.forEachIndexed { index, user ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .appClickable { onUserClicked(user.id) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FighterPortrait(
                                name = user.fullName ?: user.username ?: "Unknown",
                                imageUrl = user.avatarUrl ?: "",
                                countryCode = null,
                                result = null,
                                record = user.username?.let { "@$it" } ?: "",
                                alignment = Alignment.Start,
                                modifier = Modifier.weight(1f),
                                imageSize = 42.dp,
                                flagWidth = 14.dp,
                                flagHeight = 9.dp,
                                nameFontSize = 14.sp,
                            )
                        }

                        if (index < users.lastIndex) {
                            HorizontalDivider(
                                color = colors.dividerColor,
                                thickness = 0.8.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}
