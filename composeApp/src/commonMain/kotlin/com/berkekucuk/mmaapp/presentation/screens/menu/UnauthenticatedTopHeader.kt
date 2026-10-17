package com.berkekucuk.mmaapp.presentation.screens.menu

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UnauthenticatedTopHeader(onSignInClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = AppTheme.colors.textPrimary,
            modifier = Modifier.size(28.dp).padding(top = 4.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = AppTheme.strings.menuSignInPrompt,
                color = AppTheme.colors.textPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
            Button(
                onClick = onSignInClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.signInButton,
                    contentColor = AppTheme.colors.black
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = AppTheme.strings.menuSignInButton,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}