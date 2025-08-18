package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.image_miniature
import org.jetbrains.compose.resources.stringResource

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
    if (imageUrl.isBlank()) {
        Icon(
            Icons.Default.BrokenImage,
            contentDescription = null,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        SubcomposeAsyncImage(
            model =
                ImageRequest
                    .Builder(LocalPlatformContext.current)
                    .data(imageUrl.ifEmpty { Res.getUri("drawable/outline_broken_image_24.xml") })
                    .crossfade(true)
                    .placeholderMemoryCacheKey(cacheKey)
                    .memoryCacheKey(cacheKey)
                    .build(),
            modifier = modifier,
            loading = {
                TvManiakLoading(modifier = modifier)
            },
            contentDescription = stringResource(Res.string.image_miniature),
            contentScale = ContentScale.Crop,
        )
    }
}
