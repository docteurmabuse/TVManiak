package com.tizzone.tvmaniak.feature.tvshows.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementKeys
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementTransitions
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakLoading
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakRating
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.image_miniature
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowSummaryItemGrid(
    modifier: Modifier,
    id: Int,
    url: String,
    name: String,
    rating: Float,
    onTvShowClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigationAnimatedContentScope: AnimatedContentScope,
    isDetailScopeActive: Boolean,
) {
    val cardShape = RoundedCornerShape(12.dp)
    var sharedKey by remember { mutableStateOf(mutableStateOf("")) }

    with(sharedTransitionScope) {
        val customModifier =
            if (isDetailScopeActive) {
                sharedKey.value = SharedElementKeys(id).detailKey
                Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = sharedKey.value),
                        animatedVisibilityScope = navigationAnimatedContentScope,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                    )
            } else {
                sharedKey.value = SharedElementKeys(id).cardKey

                Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = sharedKey.value),
                        animatedVisibilityScope = animatedContentScope,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                    )
            }

        val textModifier =
            Modifier
                .wrapContentWidth()
        Card(
            shape = cardShape,
            modifier =
                customModifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onTvShowClick() },
                        )
                    }.shadow(
                        elevation = 10.dp,
                        shape = cardShape,
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                Logger.d("CacheKey") { "key: '${sharedKey.value}' url = $url" }

                Box(
                    modifier = Modifier
                        .clip(cardShape)
                        .aspectRatio(0.7f),
                    contentAlignment = Alignment.Center
                ) {
                    if (url.isNullOrBlank()) {
                        Icon(
                            Icons.Default.BrokenImage,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        SubcomposeAsyncImage(
                            model =
                                ImageRequest
                                    .Builder(LocalPlatformContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .placeholderMemoryCacheKey(sharedKey.value)
                                    .memoryCacheKey(sharedKey.value)
                                    .build(),
                            modifier =
                                Modifier.fillMaxSize(),
                            loading = {
                                TvManiakLoading(modifier = Modifier)
                            },
                            contentDescription = stringResource(Res.string.image_miniature),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                Text(
                    text = name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    modifier = textModifier.padding(start = 10.dp, end = 10.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
                TvManiakRating(
                    modifier =
                        Modifier
                            .padding(start = 10.dp, bottom = 8.dp),
                    rating = rating,
                )
            }
        }
    }
}
