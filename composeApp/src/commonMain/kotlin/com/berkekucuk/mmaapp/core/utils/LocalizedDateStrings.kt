package com.berkekucuk.mmaapp.core.utils

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.berkekucuk.mmaapp.core.presentation.AppLanguage

data class LocalizedDateStrings(
    val months: List<String>,
    val daysOfWeek: List<String>,
)

private val EN_MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val EN_DAYS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val TR_MONTHS = listOf("Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara")
private val TR_DAYS = listOf("Pzt", "Sal", "Çrş", "Prş", "Cum", "Cmt", "Paz")

@Composable
fun rememberLocalizedDateStrings(): LocalizedDateStrings {
    val language = AppTheme.strings.language
    return remember(language) {
        when (language) {
            AppLanguage.EN -> LocalizedDateStrings(months = EN_MONTHS, daysOfWeek = EN_DAYS)
            AppLanguage.TR -> LocalizedDateStrings(months = TR_MONTHS, daysOfWeek = TR_DAYS)
        }
    }
}
