package com.berkekucuk.mmaapp.presentation.screens.menu

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.utils.isIos
import com.berkekucuk.mmaapp.presentation.components.PrivacyPolicyText
import com.berkekucuk.mmaapp.presentation.components.SocialSignInButton
import mmaapp.composeapp.generated.resources.Res
import mmaapp.composeapp.generated.resources.apple_logo
import mmaapp.composeapp.generated.resources.ic_google_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInBottomSheet(
    onDismiss: () -> Unit,
    onStartGoogleSignIn: () -> Unit,
    onStartAppleSignIn: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppTheme.colors.dropdownMenuBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = AppTheme.strings.menuSignInButton,
                color = AppTheme.colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!isIos) {
                SocialSignInButton(
                    text = AppTheme.strings.menuSignInWithGoogle,
                    icon = Res.drawable.ic_google_logo,
                    onClick = onStartGoogleSignIn,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                SocialSignInButton(
                    text = AppTheme.strings.menuSignInWithApple,
                    icon = Res.drawable.apple_logo,
                    onClick = onStartAppleSignIn,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrivacyPolicyText()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
