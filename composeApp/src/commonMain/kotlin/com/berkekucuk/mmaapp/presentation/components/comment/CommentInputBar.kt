package com.berkekucuk.mmaapp.presentation.components.comment

import com.berkekucuk.mmaapp.core.presentation.AppTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommentInputBar(
    isAuthenticated: Boolean,
    input: String,
    isSending: Boolean,
    maxLength: Int,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    replyingToUsername: String? = null,
    onCancelReply: () -> Unit = {},
    focusRequester: FocusRequester? = null,
) {
    val canSend = input.isNotBlank() && !isSending

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.pagerBackground)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
    ) {
        if (!isAuthenticated) {
            Text(
                text = AppTheme.strings.commentLoginRequired,
                color = AppTheme.colors.textSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            return@Column
        }

        if (replyingToUsername != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppTheme.strings.commentReplyingTo(replyingToUsername),
                    color = AppTheme.colors.textSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCancelReply,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = AppTheme.strings.commonCancel,
                        tint = AppTheme.colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
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
                placeholder = { Text(AppTheme.strings.commentInputPlaceholder, fontSize = 14.sp) },
                textStyle = TextStyle(fontSize = 14.sp),
                maxLines = 4,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    focusedBorderColor = AppTheme.colors.cardBorder,
                    unfocusedBorderColor = AppTheme.colors.cardBorder,
                    focusedPlaceholderColor = AppTheme.colors.textSecondary,
                    unfocusedPlaceholderColor = AppTheme.colors.textSecondary,
                    focusedContainerColor = AppTheme.colors.fightItemBackground,
                    unfocusedContainerColor = AppTheme.colors.fightItemBackground,
                    cursorColor = AppTheme.colors.textPrimary,
                ),
                supportingText = if (input.length > maxLength - COUNTER_VISIBLE_FROM_REMAINING) {
                    { Text("${input.length}/$maxLength", color = AppTheme.colors.textSecondary) }
                } else null,
                modifier = Modifier
                    .weight(1f)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            )

            IconButton(
                onClick = onSendClick,
                enabled = canSend
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.ufcRed,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = AppTheme.strings.contentDescriptionSendComment,
                        tint = if (canSend) AppTheme.colors.ufcRed else AppTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}

private const val COUNTER_VISIBLE_FROM_REMAINING = 50
