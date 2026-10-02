package com.berkekucuk.mmaapp.presentation.screens.rankings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.domain.model.WeightClass
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@Composable
fun RankingContainer(
    weightClasses: List<WeightClass>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onWeightClassClicked: (String) -> Unit,
    listState: LazyListState
) {
    val strings = LocalAppStrings.current

    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        listState = listState,
        contentPadding = PaddingValues(top = 10.dp),
        verticalSpacing = 10.dp
    ) {
        weightClasses.forEach { weightClass ->
            val champion = weightClass.rankings.minByOrNull { it.rankNumber }
            val displayName = strings.toUpperCase(strings.weightClassDisplayName(weightClass.id))
            item(key = "header_${weightClass.id}") {
                WeightClassCard(
                    weightClassName = displayName,
                    weightLimit = weightClass.weightLimit,
                    champion = champion,
                    onWeightClassClicked = { onWeightClassClicked(weightClass.id) }
                )
            }
        }
    }
}
