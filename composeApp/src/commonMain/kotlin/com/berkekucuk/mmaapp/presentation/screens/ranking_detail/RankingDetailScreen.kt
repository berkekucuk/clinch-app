package com.berkekucuk.mmaapp.presentation.screens.ranking_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun RankingDetailScreenRoot(
    viewModel: RankingDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToFighterDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is RankingDetailNavigationEvent.Back -> onNavigateBack()
                is RankingDetailNavigationEvent.ToFighterDetail -> onNavigateToFighterDetail(event.fighterId)
            }
        }
    }

    RankingDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingDetailScreen(
    state: RankingDetailUiState,
    onAction: (RankingDetailUiAction) -> Unit
) {
    val onBackClicked = remember(onAction) { { onAction(RankingDetailUiAction.OnBackClicked) } }
    val onFighterClicked = remember(onAction) { { fighterId: String -> onAction(RankingDetailUiAction.OnFighterClicked(fighterId)) } }
    val onRefresh = remember(onAction) { { onAction(RankingDetailUiAction.OnRefresh) } }

    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.pagerBackground,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            Column(
                modifier = Modifier.background(colors.rankingTopBar)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.toUpperCase(strings.weightClassDisplayName(state.weightClassId)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = colors.textPrimary,
                        titleContentColor = colors.textPrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        ListContainer(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(top = 8.dp),
            verticalSpacing = 0.dp,
            extraBottomPadding = navBarBottomPadding,
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.fightItemBackground)
                ) {
                    state.rankedFighters.forEachIndexed { index, ranking ->
                        ranking.fighter?.let { fighter ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                RankedFighterRow(
                                    rankNumber = ranking.rankNumber,
                                    isChampion = ranking.rankNumber == 0,
                                    name = fighter.name,
                                    record = fighter.record.toString(),
                                    imageUrl = fighter.imageUrl,
                                    countryCode = fighter.countryCode,
                                    onFighterClicked = { onFighterClicked(fighter.fighterId) },
                                    trailingContent = {
                                        val rankChange = ranking.rankChange ?: 0
                                        if (rankChange != 0) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(end = 16.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (rankChange > 0) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                                    contentDescription = null,
                                                    tint = if (rankChange > 0) colors.winnerFrame else colors.loseColor,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = abs(rankChange).toString(),
                                                    color = if (rankChange > 0) colors.winnerFrame else colors.loseColor,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                )

                                if (index < state.rankedFighters.lastIndex) {
                                    HorizontalDivider(
                                        color = colors.dividerColor,
                                        thickness = 0.8.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
