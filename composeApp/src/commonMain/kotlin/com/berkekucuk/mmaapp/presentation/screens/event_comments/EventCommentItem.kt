package com.berkekucuk.mmaapp.presentation.screens.event_comments

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.domain.model.EventComment
import com.berkekucuk.mmaapp.presentation.components.FighterImage
import com.berkekucuk.mmaapp.presentation.components.appClickable

@Composable
fun EventCommentItem(
    comment: EventComment,
    timeText: String,
    canReport: Boolean,
    canDelete: Boolean,
    onUserClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showMenu = canReport || canDelete

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.fightItemBackground)
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = if (showMenu) 0.dp else 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        FighterImage(
            imageUrl = comment.avatarUrl,
            name = comment.username,
            countryCode = "",
            alignment = Alignment.Start,
            imageSize = 40.dp,
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
                    text = "@${comment.username}",
                    color = AppTheme.colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .appClickable(onClick = onUserClick)
                )
                Text(
                    text = timeText,
                    color = AppTheme.colors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(
                text = comment.content,
                color = AppTheme.colors.textPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
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
