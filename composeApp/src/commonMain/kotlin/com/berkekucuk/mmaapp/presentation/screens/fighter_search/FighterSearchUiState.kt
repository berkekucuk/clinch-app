package com.berkekucuk.mmaapp.presentation.screens.fighter_search

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.Fighter

data class FighterSearchUiState(
    val query: String = "",
    val results: List<Fighter> = emptyList(),
    val error: AppError? = null,
    val addingInteractionFighterId: String? = null,
)