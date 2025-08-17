package com.tizzone.tvmaniak.feature.tvshows.presentation

import TvShowsGridView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import com.tizzone.tvmaniak.core.common.utils.GRID_VIEW_ICON
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakLoading
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakSearchTopBar
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.model.TvShowsEvent
import com.tizzone.tvmaniak.feature.tvshows.presentation.components.TvShowsListView
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.search_placeholder
import com.tizzone.tvmaniak.resources.tv_shows_screen
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowsScreen(
    modifier: Modifier = Modifier,
    onTvShowClick: (Int) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    onRefreshList: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    windowSizeClass: WindowSizeClass,
    tvShows: LazyPagingItems<TvShowSummary>,
    isGridView: Boolean,
    searchQuery: String,
    gridState: LazyGridState,
    listState: LazyListState,
) {
    // Synchronize scroll positions between grid and list
    LaunchedEffect(isGridView) {
        if (isGridView) {
            // Switching to grid - sync position from list to grid
            val currentIndex = listState.firstVisibleItemIndex
            if (currentIndex > 0) {
                gridState.scrollToItem(currentIndex)
            }
        } else {
            // Switching to list - sync position from grid to list
            val currentIndex = gridState.firstVisibleItemIndex
            if (currentIndex > 0) {
                listState.scrollToItem(currentIndex)
            }
        }
    }

    Scaffold(
        modifier =
            modifier
                .testTag(stringResource(Res.string.tv_shows_screen)),
        topBar = {
            Column {
                TvManiakSearchTopBar(
                    modifier =
                        Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query ->
                        onEvent(TvShowsEvent.SearchTvShows(query))
                        onRefreshList()
                    },
                    onSearch = { query ->
                        onEvent(TvShowsEvent.SearchTvShows(query))
                        onRefreshList()
                    },
                    searchPlaceholder = stringResource(Res.string.search_placeholder),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector =
                            if (isGridView) {
                                Icons.AutoMirrored.Outlined.ViewList
                            } else {
                                Icons.Outlined.GridView
                            },
                        contentDescription = if (isGridView) "Switch to list view" else "Switch to grid view",
                        modifier =
                            Modifier
                                .padding(vertical = 8.dp)
                                .testTag(GRID_VIEW_ICON)
                                .clickable {
                                    onEvent(TvShowsEvent.ToggleViewMode)
                                },
                    )
                }
            }
        },
    ) { contentPadding ->
        Column(
            modifier =
                modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .testTag(stringResource(Res.string.tv_shows_screen)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                if (tvShows.itemCount == 0 && tvShows.loadState.refresh is androidx.paging.LoadState.Loading) {
                    TvManiakLoading(
                        modifier = Modifier
                    )
                } else {
                    with(sharedTransitionScope) {
                        AnimatedContent(
                            targetState = isGridView,
                            label = "GridListAnimation",
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                            },
                            modifier = Modifier
                        ) { targetIsGridView ->
                            if (targetIsGridView) {
                                TvShowsGridView(
                                    modifier = Modifier,
                                    onTvShowClick = onTvShowClick,
                                    contentPadding = PaddingValues(top = 16.dp),
                                    tvShows = tvShows,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedContentScope = this@AnimatedContent,
                                    navigationAnimatedContentScope = animatedContentScope,
                                    gridState = gridState
                                )
                            } else {
                                TvShowsListView(
                                    modifier = Modifier,
                                    onTvShowClick = onTvShowClick,
                                    contentPadding = PaddingValues(top = 16.dp),
                                    tvShows = tvShows,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedContentScope = this@AnimatedContent,
                                    navigationAnimatedContentScope = animatedContentScope,
                                    windowSizeClass = windowSizeClass,
                                    listState = listState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
