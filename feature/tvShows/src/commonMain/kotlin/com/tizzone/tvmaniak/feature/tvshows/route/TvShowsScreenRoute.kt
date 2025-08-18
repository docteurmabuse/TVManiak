package com.tizzone.tvmaniak.feature.tvshows.route

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import com.tizzone.tvmaniak.feature.tvshows.presentation.TvShowsScreen
import com.tizzone.tvmaniak.feature.tvshows.presentation.TvShowsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowsScreenRoute(
    viewModel: TvShowsViewModel,
    onTvShowClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    windowSizeClass: WindowSizeClass,
) {
    val tvShows = viewModel.tvShows.collectAsLazyPagingItems()
    val isGridView by viewModel.isGridView.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()

    LaunchedEffect(searchQuery) {
        gridState.scrollToItem(0)
        listState.scrollToItem(0)
    }

    TvShowsScreen(
        tvShows = tvShows,
        onTvShowClick = onTvShowClick,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize(),
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        windowSizeClass = windowSizeClass,
        isGridView = isGridView,
        searchQuery = searchQuery,
        onRefreshList = { tvShows.refresh() },
        gridState = gridState,
        listState = listState,
    )
}
