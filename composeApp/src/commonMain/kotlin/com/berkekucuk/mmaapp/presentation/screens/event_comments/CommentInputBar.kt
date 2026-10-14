package com.berkekucuk.mmaapp.presentation.screens.event_comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings

@Composable
fun CommentInputBar(
    isAuthenticated: Boolean,
    input: String,
    isSending: Boolean,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val canSend = input.isNotBlank() && !isSending

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.pagerBackground)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
    ) {
        HorizontalDivider(color = colors.dividerColor, thickness = 0.8.dp)

        if (!isAuthenticated) {
            Text(
                text = strings.commentLoginRequired,
                color = colors.textSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            return@Column
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChanged,
                placeholder = { Text(strings.commentInputPlaceholder, fontSize = 14.sp) },
                textStyle = TextStyle(fontSize = 14.sp),
                maxLines = 4,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = colors.cardBorder,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedPlaceholderColor = colors.textSecondary,
                    unfocusedPlaceholderColor = colors.textSecondary,
                    focusedContainerColor = colors.fightItemBackground,
                    unfocusedContainerColor = colors.fightItemBackground,
                    cursorColor = colors.textPrimary,
                ),
                supportingText = if (input.length > COUNTER_VISIBLE_FROM) {
                    { Text("${input.length}/${EventCommentsViewModel.MAX_COMMENT_LENGTH}", color = colors.textSecondary) }
                } else null,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onSendClick,
                enabled = canSend
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = colors.ufcRed,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = strings.contentDescriptionSendComment,
                        tint = if (canSend) colors.ufcRed else colors.textSecondary,
                    )
                }
            }
        }
    }
}

private const val COUNTER_VISIBLE_FROM = 450
