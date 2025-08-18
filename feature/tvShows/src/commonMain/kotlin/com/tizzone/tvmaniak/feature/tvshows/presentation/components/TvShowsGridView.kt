
package com.tizzone.tvmaniak.feature.tvshows.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakError
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakLoading
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.model.TvShowsEvent
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.no_tv_shows
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun TvShowsGridView(
    contentPadding: PaddingValues,
    tvShows: LazyPagingItems<TvShowSummary>,
    onTvShowClick: (Int) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    navigationAnimatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
) {
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier =
            modifier
                .padding(contentPadding)
                .fillMaxWidth(),
    ) {
        when (val refreshState = tvShows.loadState.refresh) {
            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TvManiakError(
                        modifier = Modifier.fillMaxWidth(),
                        text = refreshState.error.message ?: stringResource(Res.string.no_tv_shows),
                    )
                }
            }

            is LoadState.NotLoading -> {
                if (tvShows.itemCount == 0 && tvShows.loadState.append.endOfPaginationReached) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TvManiakError(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.no_tv_shows),
                        )
                    }
                } else {
                    items(
                        count = tvShows.itemCount,
                        key = tvShows.itemKey { it.id },
                    ) { index ->
                        val tvShow = tvShows[index]
                        tvShow?.let { currentShow ->
                            TvShowSummaryItemGrid(
                                modifier = Modifier,
                                onTvShowClick = {
                                    onTvShowClick(currentShow.id)
                                },
                                onWatchlistClick = { showId ->
                                    if (currentShow.isInWatchList) {
                                        onEvent(TvShowsEvent.RemoveFromWatchList(showId))
                                    } else {
                                        onEvent(TvShowsEvent.AddToWatchList(showId))
                                    }
                                },
                                isInWatchlist = currentShow.isInWatchList,
                                sharedTransitionScope = sharedTransitionScope,
                                navigationAnimatedContentScope = navigationAnimatedContentScope,
                                tvShow = currentShow,
                            )
                        }
                    }
                }
            }

            is LoadState.Loading -> {
                if (tvShows.itemCount == 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TvManiakLoading(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = TvManiakSpacing.extraLarge),
                        )
                    }
                }
            }
        }
        when (val appendState = tvShows.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TvManiakLoading(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = TvManiakSpacing.small),
                    )
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TvManiakError(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = TvManiakSpacing.small),
                        text = appendState.error.message ?: stringResource(Res.string.no_tv_shows),
                    )
                }
            }

            is LoadState.NotLoading -> {
                // No additional UI needed
            }
        }
    }
}
