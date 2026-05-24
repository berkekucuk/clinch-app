package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfile(
    val user: User,
    val interactions: List<Interaction>
) {
    val topFavorite: Interaction? get() = interactions.find { it.interactionType == "favorite" && it.rankNumber == 1 }
    val topHated: Interaction? get() = interactions.find { it.interactionType == "hated" && it.rankNumber == 1 }
    val topGoat: Interaction? get() = interactions.find { it.interactionType == "goat" && it.rankNumber == 1 }
}
