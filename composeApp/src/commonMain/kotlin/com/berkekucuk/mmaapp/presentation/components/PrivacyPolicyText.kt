package com.berkekucuk.mmaapp.presentation.components

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyText(modifier: Modifier = Modifier) {

    val termsText = AppTheme.strings.menuSignInTerms
    val privacyStr = AppTheme.strings.menuSignInPrivacyPolicy

    val annotatedString = buildAnnotatedString {
        val startIndex = termsText.indexOf(privacyStr)

        if (startIndex >= 0) {
            val endIndex = startIndex + privacyStr.length
            append(termsText.substring(0, startIndex))

            pushLink(
                LinkAnnotation.Url(
                    url = "https://www.termsfeed.com/live/0be34d50-def3-48ea-b982-75635a10803b"
                )
            )
            withStyle(
                style = SpanStyle(
                    color = AppTheme.colors.textPrimary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(termsText.substring(startIndex, endIndex))
            }
            pop()

            append(termsText.substring(endIndex))
        } else {
            append(termsText)
        }
    }

    Text(
        text = annotatedString,
        color = AppTheme.colors.textSecondary,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}
