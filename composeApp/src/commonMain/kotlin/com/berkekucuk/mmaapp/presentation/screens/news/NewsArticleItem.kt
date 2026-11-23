package com.berkekucuk.mmaapp.presentation.screens.news

import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.domain.model.NewsArticle
import com.berkekucuk.mmaapp.presentation.components.appClickable

@Composable
fun NewsArticleItem(
    article: NewsArticle,
    timeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTurkish = AppTheme.strings.language == AppLanguage.TR
    val title = if (isTurkish) article.titleTr else article.titleEn
    val summary = if (isTurkish) article.summaryTr else article.summaryEn
    val metaText = timeText
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AppTheme.colors.cardShadowElevation,
                shape = cardShape,
                clip = false
            )
            .appClickable { onClick() },
        shape = cardShape,
        border = BorderStroke(0.5.dp, AppTheme.colors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.fightItemBackground
        )
    ) {
        if (article.imageUrl != null) {
            ImageNewsCard(imageUrl = article.imageUrl, title = title, metaText = metaText)
        } else {
            TextNewsCard(title = title, summary = summary, metaText = metaText)
        }
    }
}

@Composable
private fun ImageNewsCard(
    imageUrl: String,
    title: String,
    metaText: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 2f)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.cardHeaderBackground)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.4f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.85f),
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = metaText,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TextNewsCard(
    title: String,
    summary: String,
    metaText: String,
) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = summary,
            color = AppTheme.colors.textSecondary,
            fontSize = 13.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = metaText,
            color = AppTheme.colors.textSecondary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
