package com.berkekucuk.mmaapp.presentation.components.comment

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.presentation.components.FighterImage
import com.berkekucuk.mmaapp.presentation.components.appClickable

enum class CommentItemStyle {
    Card,
    Flat,
}

@Composable
fun CommentItem(
    username: String,
    avatarUrl: String,
    content: String,
    timeText: String,
    canReport: Boolean,
    canDelete: Boolean,
    onUserClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
    replyToUsername: String? = null,
    onReplyToUserClick: (() -> Unit)? = null,
    onReplyClick: (() -> Unit)? = null,
    avatarSize: Dp = 40.dp,
    style: CommentItemStyle = CommentItemStyle.Card,
) {
    val showMenu = canReport || canDelete
    val isFlat = style == CommentItemStyle.Flat

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFlat) {
                    Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp, end = if (showMenu) 4.dp else 16.dp)
                } else {
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppTheme.colors.fightItemBackground)
                        .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = if (showMenu) 0.dp else 12.dp)
                }
            ),
        verticalAlignment = Alignment.Top
    ) {
        FighterImage(
            imageUrl = avatarUrl,
            name = username,
            countryCode = "",
            alignment = Alignment.Start,
            imageSize = avatarSize,
            showFlag = false,
            modifier = Modifier
                .clip(CircleShape)
                .appClickable(onClick = onUserClick)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@$username",
                    color = AppTheme.colors.textPrimary,
                    fontSize = if (isFlat) 15.sp else 14.sp,
                    fontWeight = if (isFlat) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .appClickable(onClick = onUserClick)
                )
                Text(
                    text = if (isFlat) "· $timeText" else timeText,
                    color = AppTheme.colors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(
                text = buildAnnotatedString {
                    if (replyToUsername != null) {
                        val mentionStyle = SpanStyle(color = AppTheme.colors.mentionColor, fontWeight = FontWeight.SemiBold)
                        if (onReplyToUserClick != null) {
                            withLink(
                                LinkAnnotation.Clickable(
                                    tag = "mention",
                                    styles = TextLinkStyles(style = mentionStyle),
                                    linkInteractionListener = { onReplyToUserClick() }
                                )
                            ) {
                                append("@$replyToUsername")
                            }
                        } else {
                            withStyle(mentionStyle) { append("@$replyToUsername") }
                        }
                        append(" ")
                    }
                    append(content)
                },
                color = AppTheme.colors.textPrimary,
                fontSize = if (isFlat) 15.sp else 14.sp,
                lineHeight = if (isFlat) 21.sp else 20.sp,
            )
            if (onReplyClick != null) {
                Text(
                    text = AppTheme.strings.commentReply,
                    color = AppTheme.colors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .appClickable(onClick = onReplyClick)
                        .padding(top = 2.dp, bottom = 2.dp, end = 8.dp)
                )
            }
        }

        if (showMenu) {
            CommentOverflowMenu(
                canReport = canReport,
                canDelete = canDelete,
                contentDescription = AppTheme.strings.contentDescriptionMoreOptions,
                onDeleteClick = onDeleteClick,
                onReportClick = onReportClick,
            )
        }
    }
}

@Composable
private fun CommentOverflowMenu(
    canReport: Boolean,
    canDelete: Boolean,
    contentDescription: String,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = contentDescription,
                tint = AppTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = AppTheme.colors.dropdownMenuBackground
        ) {
            if (canReport) {
                CommentMenuItem(AppTheme.strings.reportUserTitle, Icons.Default.Report) {
                    expanded = false
                    onReportClick()
                }
            }
            if (canDelete) {
                CommentMenuItem(AppTheme.strings.commonDelete, Icons.Default.Delete) {
                    expanded = false
                    onDeleteClick()
                }
            }
        }
    }
}

@Composable
private fun CommentMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {

    DropdownMenuItem(
        text = { Text(text, color = AppTheme.colors.textPrimary) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = AppTheme.colors.textPrimary
            )
        },
        onClick = onClick
    )
}
