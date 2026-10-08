package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors

@Composable
fun AppSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
) {
    val colors = LocalAppColors.current
    val backgroundColor = if (isSuccess) colors.winnerFrame else colors.loseColor
    val contentColor = colors.white
    val icon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = 0.85f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = snackbarData.visuals.message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.weight(1f),
        )
        snackbarData.visuals.actionLabel?.let { label ->
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                modifier = Modifier.clickable { snackbarData.performAction() },
            )
        }
    }
}
