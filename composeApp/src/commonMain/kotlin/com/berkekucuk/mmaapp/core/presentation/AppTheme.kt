package com.berkekucuk.mmaapp.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.berkekucuk.mmaapp.core.presentation.colors.AppColors
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.AppStrings
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings

object AppTheme {
    val strings: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStrings.current

    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}
