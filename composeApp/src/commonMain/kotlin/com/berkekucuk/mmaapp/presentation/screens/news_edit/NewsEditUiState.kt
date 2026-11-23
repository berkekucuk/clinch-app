package com.berkekucuk.mmaapp.presentation.screens.news_edit

import com.berkekucuk.mmaapp.core.utils.AppError

data class NewsEditUiState(
    val isLoaded: Boolean = false,
    val titleTr: String = "",
    val summaryTr: String = "",
    val titleEn: String = "",
    val summaryEn: String = "",
    val isSaving: Boolean = false,
    val error: AppError? = null,
) {
    val canSave: Boolean
        get() = isLoaded && !isSaving &&
            titleTr.isNotBlank() && summaryTr.isNotBlank() && titleEn.isNotBlank() && summaryEn.isNotBlank()
}
