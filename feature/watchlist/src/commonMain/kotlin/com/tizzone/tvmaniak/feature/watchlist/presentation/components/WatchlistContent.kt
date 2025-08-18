package com.tizzone.tvmaniak.feature.watchlist.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing
import com.tizzone.tvmaniak.core.model.TvShowSummary

@Composable
fun WatchlistContent(
    shows: List<TvShowSummary>,
    isGridView: Boolean,
    onShowClick: (Int) -> Unit,
    onRemoveFromWatchlist: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(TvManiakSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiakSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(TvManiakSpacing.medium),
            modifier = modifier,
        ) {
            items(
                items = shows,
                key = { it.id },
            ) { show ->
                WatchlistItemGrid(
                    tvShow = show,
                    onShowClick = { onShowClick(show.id) },
                    onRemoveClick = { onRemoveFromWatchlist(show.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(TvManiakSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(TvManiakSpacing.small),
            modifier = modifier,
        ) {
            items(
                items = shows,
                key = { it.id },
            ) { show ->
                WatchlistItemList(
                    tvShow = show,
                    onShowClick = { onShowClick(show.id) },
                    onRemoveClick = { onRemoveFromWatchlist(show.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
