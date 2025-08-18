package com.tizzone.tvmaniak.feature.tvshows.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementKeys
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementTransitions
import com.tizzone.tvmaniak.core.designsystem.component.RichHtmlText
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakImage
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakRating
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.model.TvShowsEvent
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TvShowSummaryItemList(
    modifier: Modifier,
    onTvShowClick: () -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    navigationAnimatedContentScope: AnimatedContentScope,
    tvShow: TvShowSummary,
    windowSizeClass: WindowSizeClass,
) {
    val currentWindowSizeClass = calculateWindowSizeClass()
    val ligneSize =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 3
            WindowWidthSizeClass.Medium -> 4
            else -> 5
        }

    val cardShape = RoundedCornerShape(TvManiakSpacing.small)
    val sharedKey = SharedElementKeys(tvShow.id).cardKey
    with(sharedTransitionScope) {
        val customModifier =
            Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = sharedKey),
                    animatedVisibilityScope = navigationAnimatedContentScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                )
        Card(
            shape = cardShape,
            modifier =
                customModifier
                    .padding(TvManiakSpacing.small)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onTvShowClick() },
                        )
                    }.fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = cardShape,
                    ),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = TvManiakSpacing.small),
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.Top,
            ) {
                TvManiakImage(
                    imageUrl = tvShow.imageUrl,
                    contentDescription = tvShow.name,
                    contentScale = ContentScale.Crop,
                    cacheKey = "list_${tvShow.id}_${tvShow.imageUrl}",
                    modifier =
                        Modifier
                            .aspectRatio(0.6f)
                            .weight(0.20f),
                )
                Column(
                    modifier =
                        Modifier
                            .weight(0.80f)
                            .align(Alignment.Top)
                            .padding(start = TvManiakSpacing.small),
                ) {
                    Text(
                        text = tvShow.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = TvManiakSpacing.small),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Logger.d("tvShow.summary screen size: ${currentWindowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact}")
                    RichHtmlText(
                        modifier = Modifier.padding(top = TvManiakSpacing.small),
                        color = MaterialTheme.colorScheme.primary,
                        html = tvShow.summary,
                        maxLines = ligneSize,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        modifier = Modifier.padding(top = TvManiakSpacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TvManiakRating(
                            rating = tvShow.rating ?: 0f,
                        )
                        WatchlistButton(
                            isInWatchlist = tvShow.isInWatchList,
                            id = tvShow.id,
                            onWatchlistClick = { showId ->
                                if (tvShow.isInWatchList) {
                                    onEvent(TvShowsEvent.RemoveFromWatchList(showId))
                                } else {
                                    onEvent(TvShowsEvent.AddToWatchList(showId))
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun TvShowSummaryItemListPreview() {
    val tvShow =
        TvShowSummary(
            id = 1,
            name = "Under the Dome",
            summary =
                "<p><b>Under the Dome</b> is the story of a small town that is suddenly and inexplicably " +
                    "sealed off from the rest of the world by an enormous transparent dome. The town's inhabitants " +
                    "must deal with surviving the post-apocalyptic conditions while searching for answers about " +
                    "the dome, where it came from and if and when it will go away.</p>",
            type = "Scripted",
            language = "English",
            genres = listOf("Drama", "Science-Fiction", "Thriller"),
            status = "Ended",
            rating = 6.5f,
            imageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
            largeImageUrl = "https://static.tvmaze.com/uploads/images/original_portrait/81/202627.jpg",
            updated = 1704794122,
        )
    SharedTransitionScope {
        AnimatedContent(
            targetState = "previewState",
            label = "PreviewAnimatedContentForItem",
        ) {
            val currentAnimatedContentScope = this

            TvShowSummaryItemList(
                modifier = Modifier,
                onTvShowClick = {},
                onEvent = {},
                sharedTransitionScope = this@SharedTransitionScope,
                navigationAnimatedContentScope = currentAnimatedContentScope,
                tvShow = tvShow,
                windowSizeClass = calculateWindowSizeClass(),
            )
        }
    }
}
