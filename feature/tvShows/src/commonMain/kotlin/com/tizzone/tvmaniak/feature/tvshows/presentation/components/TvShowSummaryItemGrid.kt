package com.tizzone.tvmaniak.feature.tvshows.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementKeys
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementTransitions
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakImage
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakRating
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing
import com.tizzone.tvmaniak.core.model.TvShowSummary

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowSummaryItemGrid(
    modifier: Modifier,
    tvShow: TvShowSummary,
    onTvShowClick: () -> Unit,
    onWatchlistClick: (Int) -> Unit = {},
    isInWatchlist: Boolean = false,
    sharedTransitionScope: SharedTransitionScope,
    navigationAnimatedContentScope: AnimatedContentScope,
) {
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
                    }.shadow(
                        elevation = 10.dp,
                        shape = cardShape,
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally),
            ) {
                TvManiakImage(
                    imageUrl = tvShow.imageUrl,
                    contentDescription = tvShow.name,
                    contentScale = ContentScale.Crop,
                    cacheKey = "grid_${tvShow.id}_${tvShow.imageUrl}",
                    modifier = Modifier.fillMaxSize(),
                )
                Column(
                    modifier = Modifier.padding(TvManiakSpacing.small),
                ) {
                    Text(
                        text = tvShow.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        tvShow.rating?.let { rating ->
                            TvManiakRating(rating = rating)
                        }
                        WatchlistButton(
                            isInWatchlist = isInWatchlist,
                            id = tvShow.id,
                            onWatchlistClick = onWatchlistClick,
                        )
                    }
                }
            }
        }
    }
}
