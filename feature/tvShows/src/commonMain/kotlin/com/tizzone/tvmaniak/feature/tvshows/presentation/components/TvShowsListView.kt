package com.tizzone.tvmaniak.feature.tvshows.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakError
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakLoading
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.model.TvShowsEvent
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.no_tv_shows
import org.jetbrains.compose.resources.stringResource

const val REFRESH_LOADING_KEY = "refresh_loading"
const val REFRESH_ERROR_KEY = "refresh_error"
const val APPEND_LOADING_KEY = "append_loading"
const val APPEND_ERROR_KEY = "append_error"

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun TvShowsListView(
    contentPadding: PaddingValues,
    tvShows: LazyPagingItems<TvShowSummary>,
    onTvShowClick: (Int) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    navigationAnimatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    listState: LazyListState,
) {
    var isDetailScopeActive by remember { mutableStateOf(false) }
    LazyColumn(
        state = listState,
        modifier =
            modifier
                .padding(contentPadding)
                .fillMaxWidth(),
    ) {
        when (tvShows.loadState.refresh) {
            is LoadState.Loading -> {
                item(key = REFRESH_LOADING_KEY) {
                    TvManiakLoading(
                        modifier = Modifier,
                    )
                }
            }

            is LoadState.Error -> {
                val refreshError = tvShows.loadState.refresh as LoadState.Error
                item(key = REFRESH_ERROR_KEY) {
                    TvManiakError(
                        modifier = Modifier,
                        text =
                            refreshError.error.message
                                ?: stringResource(Res.string.no_tv_shows),
                    )
                }
            }

            is LoadState.NotLoading -> {
                items(
                    count = tvShows.itemCount,
                    key = { index -> tvShows[index]?.id ?: "item_$index" },
                ) { index ->
                    tvShows[index]?.let { tvShow ->
                        with(sharedTransitionScope) {
                            TvShowSummaryItemList(
                                modifier = modifier,
                                tvShow = tvShow,
                                onTvShowClick = {
                                    onTvShowClick(tvShow.id)
                                },
                                onEvent = onEvent,
                                sharedTransitionScope = sharedTransitionScope,
                                navigationAnimatedContentScope = navigationAnimatedContentScope,
                                windowSizeClass = windowSizeClass,
                            )
                        }
                    }
                }
            }
        }
        when (tvShows.loadState.append) {
            is LoadState.Loading -> {
                item(key = APPEND_LOADING_KEY) {
                    TvManiakLoading(
                        modifier = Modifier,
                    )
                }
            }

            is LoadState.Error -> {
                val appendError = tvShows.loadState.append as LoadState.Error
                item(key = APPEND_ERROR_KEY) {
                    TvManiakError(
                        modifier = Modifier,
                        text =
                            appendError.error.message
                                ?: stringResource(Res.string.no_tv_shows),
                    )
                }
            }

            is LoadState.NotLoading -> {
                // No additional UI needed
            }
        }
    }
}
