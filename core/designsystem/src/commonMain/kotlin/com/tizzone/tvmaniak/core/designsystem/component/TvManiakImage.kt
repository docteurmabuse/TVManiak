package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tizzone.tvmaniak.resources.Res

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvManiakImage(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
    cacheKey: String?,
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalPlatformContext.current)
            .data(imageUrl.ifEmpty { Res.getUri("drawable/outline_broken_image_24.xml") })
            .crossfade(true)
            .placeholderMemoryCacheKey(cacheKey)
            .memoryCacheKey(cacheKey)
            .build(),
        placeholder = null,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
    )
}
