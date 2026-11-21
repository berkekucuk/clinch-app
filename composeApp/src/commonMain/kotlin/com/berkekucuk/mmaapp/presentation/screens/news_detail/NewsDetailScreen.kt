package com.berkekucuk.mmaapp.presentation.screens.news_detail

import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.FlowRow
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings
import com.berkekucuk.mmaapp.core.utils.toUserFriendlyDate
import com.berkekucuk.mmaapp.domain.model.NewsArticle
import com.berkekucuk.mmaapp.presentation.components.appClickable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsDetailScreenRoot(
    viewModel: NewsDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is NewsDetailNavigationEvent.Back -> onNavigateBack()
            }
        }
    }

    NewsDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    state: NewsDetailUiState,
    onAction: (NewsDetailUiAction) -> Unit
) {
    // Compose Core States
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppTheme.strings.newsDetailTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(NewsDetailUiAction.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppTheme.strings.contentDescriptionBack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.topBarBackground,
                    navigationIconContentColor = AppTheme.colors.textPrimary,
                    titleContentColor = AppTheme.colors.textPrimary,
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(NewsDetailUiAction.OnRefresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            state.article?.let { article ->
                NewsDetailContent(
                    article = article,
                    bottomPadding = navBarBottomPadding
                )
            }
        }
    }
}

@Composable
private fun NewsDetailContent(
    article: NewsArticle,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val dateStrings = rememberLocalizedDateStrings()
    val isTurkish = AppTheme.strings.language == AppLanguage.TR
    val title = if (isTurkish) article.titleTr else article.titleEn
    val summary = if (isTurkish) article.summaryTr else article.summaryEn
    val publishedDate = remember(article.publishedAt, dateStrings) {
        article.publishedAt.toUserFriendlyDate(dateStrings.months, dateStrings.daysOfWeek)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding + 16.dp)
    ) {
        if (article.imageUrl != null) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(AppTheme.colors.cardHeaderBackground)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = AppTheme.colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Text(
                text = publishedDate,
                color = AppTheme.colors.textSecondary,
                fontSize = 13.sp
            )

            if (article.fighterNames.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    article.fighterNames.forEach { name ->
                        Text(
                            text = name,
                            color = AppTheme.colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppTheme.colors.fightItemBackground)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = summary,
                color = AppTheme.colors.textPrimary,
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = AppTheme.colors.dividerColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .appClickable {
                        try {
                            uriHandler.openUri(article.sourceUrl)
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppTheme.strings.newsSource(article.sourceName),
                    color = AppTheme.colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = AppTheme.colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
