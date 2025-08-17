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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementKeys
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementTransitions
import com.tizzone.tvmaniak.core.designsystem.color.AppColors.YellowRating
import com.tizzone.tvmaniak.core.designsystem.component.RichHtmlText
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.favorite_icon
import com.tizzone.tvmaniak.resources.image_miniature
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun TvShowSummaryItemList(
    modifier: Modifier,
    onTvShowClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigationAnimatedContentScope: AnimatedContentScope,
    tvShow: TvShowSummary,
    isDetailScopeActive: Boolean,
    windowSizeClass: WindowSizeClass,
) {
    val cardShape = RoundedCornerShape(12.dp)
    val sharedKey = remember(tvShow.id, isDetailScopeActive) {
        if (isDetailScopeActive) {
            SharedElementKeys(tvShow.id).detailKey
        } else {
            SharedElementKeys(tvShow.id).cardKey
        }
    }
    with(sharedTransitionScope) {
        val imageModifier =
            Modifier
                .aspectRatio(0.6f)
                .clip(cardShape)
        val customModifier = Modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = sharedKey),
                animatedVisibilityScope = if (isDetailScopeActive) navigationAnimatedContentScope else animatedContentScope,
                enter = fadeIn(),
                exit = fadeOut(),
                boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
            )
        Card(
            shape = cardShape,
            modifier =
                customModifier
                    .padding(8.dp)
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
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.Top,
            ) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalPlatformContext.current)
                            .data(tvShow.smallImageUrl)
                            .crossfade(true)
                            .placeholderMemoryCacheKey(sharedKey)
                            .memoryCacheKey(sharedKey)
                            .build(),
                    modifier =
                        imageModifier
                            .weight(0.20f),
                    contentDescription = stringResource(Res.string.image_miniature),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier =
                        Modifier
                            .weight(0.80f)
                            .align(Alignment.Top)
                            .padding(start = 8.dp),
                ) {
                    Text(
                        text = tvShow.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    RichHtmlText(
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        html = tvShow.summary,
                        maxLines = if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) 4 else 7,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        modifier =
                            Modifier
                                .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(Res.string.favorite_icon),
                            tint = YellowRating,
                        )
                        Text(
                            modifier =
                                Modifier,
                            text = tvShow.rating.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
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
            summary = "<p><b>Under the Dome</b> is the story of a small town that is suddenly and inexplicably " +
                "sealed off from the rest of the world by an enormous transparent dome. The town's inhabitants " +
                "must deal with surviving the post-apocalyptic conditions while searching for answers about " +
                "the dome, where it came from and if and when it will go away.</p>",
            type = "Scripted",
            language = "English",
            genres = listOf("Drama", "Science-Fiction", "Thriller"),
            status = "Ended",
            rating = 6.5f,
            smallImageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
            updated = 1704794122
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
                sharedTransitionScope = this@SharedTransitionScope,
                animatedContentScope = currentAnimatedContentScope,
                navigationAnimatedContentScope = currentAnimatedContentScope,
                tvShow = tvShow,
                isDetailScopeActive = false,
                windowSizeClass = WindowSizeClass.calculateFromSize(
                    size = DpSize(width = 360.dp, height = 640.dp),
                )
            )
        }
    }
}
