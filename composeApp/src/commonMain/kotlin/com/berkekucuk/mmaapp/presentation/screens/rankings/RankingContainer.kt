package com.berkekucuk.mmaapp.presentation.screens.rankings

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.domain.model.WeightClass
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@Composable
fun RankingContainer(
    weightClasses: List<WeightClass>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onWeightClassClicked: (String) -> Unit,
    listState: LazyListState
) {

    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        listState = listState,
        contentPadding = PaddingValues(top = 10.dp),
        verticalSpacing = 10.dp
    ) {
        weightClasses.forEach { weightClass ->
            val champion = weightClass.rankings.minByOrNull { it.rankNumber }
            item(key = "header_${weightClass.id}") {
                WeightClassCard(
                    weightClassName = AppTheme.strings.toUpperCase(AppTheme.strings.weightClassDisplayName(weightClass.id)),
                    weightLimit = weightClass.weightLimit,
                    champion = champion,
                    onWeightClassClicked = { onWeightClassClicked(weightClass.id) }
                )
            }
        }
    }
}
