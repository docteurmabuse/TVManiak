package com.tizzone.tvmaniak.feature.watchlist.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakImage
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakRating
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.remove_from_watchlist
import org.jetbrains.compose.resources.stringResource

@Composable
fun WatchlistItemGrid(
    tvShow: TvShowSummary,
    onShowClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onShowClick,
        modifier = modifier,
        shape = RoundedCornerShape(TvManiakSpacing.small),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Box {
                TvManiakImage(
                    imageUrl = tvShow.imageUrl,
                    contentDescription = tvShow.name,
                    contentScale = ContentScale.Crop,
                    cacheKey = tvShow.imageUrl,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f / 3f),
                )
                IconButton(
                    onClick = onRemoveClick,
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.remove_from_watchlist),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
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
                tvShow.rating?.let { rating ->
                    TvManiakRating(rating = rating)
                }
            }
        }
    }
}
