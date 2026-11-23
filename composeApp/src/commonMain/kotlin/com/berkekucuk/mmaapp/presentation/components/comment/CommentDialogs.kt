package com.berkekucuk.mmaapp.presentation.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.domain.enums.ReportReason
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog

@Composable
fun CommentDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppAlertDialog(
        onDismissRequest = onDismiss,
        onConfirmClick = onConfirm,
        title = AppTheme.strings.commentDeleteTitle,
        text = AppTheme.strings.commentDeleteConfirm,
        confirmText = AppTheme.strings.dialogAccept,
        dismissText = AppTheme.strings.commonCancel,
        isDestructive = true
    )
}

@Composable
fun CommentReportDialog(
    selectedReason: ReportReason?,
    isReporting: Boolean,
    onReasonSelected: (ReportReason) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    AppAlertDialog(
        onDismissRequest = onDismiss,
        onConfirmClick = onSubmit,
        title = AppTheme.strings.reportUserTitle,
        confirmText = AppTheme.strings.reportUserSubmit,
        dismissText = AppTheme.strings.commonCancel,
        confirmEnabled = selectedReason != null,
        isConfirmLoading = isReporting,
        isDestructive = true,
        content = {
            Column(modifier = Modifier.selectableGroup()) {
                ReportReason.commentReasons.forEach { reason ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (reason == selectedReason),
                                onClick = { onReasonSelected(reason) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (reason == selectedReason),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppTheme.colors.textPrimary,
                                unselectedColor = AppTheme.colors.textSecondary
                            )
                        )
                        Text(
                            text = AppTheme.strings.reportReasonDisplayName(reason),
                            color = AppTheme.colors.textPrimary,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    )
}
