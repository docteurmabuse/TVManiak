package com.tizzone.tvmaniak.feature.watchlist.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.tizzone.tvmaniak.feature.watchlist.presentation.components.EmptyWatchlistContent
import com.tizzone.tvmaniak.feature.watchlist.presentation.components.WatchlistContent
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.switch_grid_view
import com.tizzone.tvmaniak.resources.switch_list_view
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WatchlistScreenRoute(onNavigateToDetails: (Int) -> Unit) {
    WatchlistScreen(
        onNavigateToDetails = onNavigateToDetails,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel = koinInject(),
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val watchlistShows by viewModel.watchlistShows.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlist") },
                actions = {
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription =
                                if (isGridView) {
                                    stringResource(Res.string.switch_list_view)
                                } else {
                                    stringResource(Res.string.switch_grid_view)
                                },
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        if (watchlistShows.isEmpty()) {
            EmptyWatchlistContent(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            )
        } else {
            WatchlistContent(
                shows = watchlistShows,
                isGridView = isGridView,
                onShowClick = onNavigateToDetails,
                onRemoveFromWatchlist = { showId ->
                    viewModel.removeFromWatchlist(showId)
                },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            )
        }
    }
}
