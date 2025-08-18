package com.tizzone.tvmaniak.feature.tvshows.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing

@Composable
fun WatchlistButton(
    isInWatchlist: Boolean,
    id: Int,
    onWatchlistClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Watchlist button overlay
    Box(
        modifier =
            Modifier
                .padding(TvManiakSpacing.small)
                .size(24.dp)
                .background(
                    color =
                        if (isInWatchlist) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Black.copy(alpha = 0.6f)
                        },
                    shape = CircleShape,
                ).clickable { onWatchlistClick(id) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
            contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
    }
}
