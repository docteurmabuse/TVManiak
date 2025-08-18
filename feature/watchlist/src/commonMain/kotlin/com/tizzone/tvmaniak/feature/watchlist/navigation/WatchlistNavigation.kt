package com.tizzone.tvmaniak.feature.watchlist.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tizzone.tvmaniak.feature.watchlist.presentation.WatchlistScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object WatchlistNavigation

@Serializable
internal data object WatchlistScreen

@Serializable
internal data object TvShowDetailsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.watchlistGraph(
    onTvShowClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<WatchlistNavigation> { entry ->
        WatchlistScreenRoute(
            onNavigateToDetails = onTvShowClick,
        )
    }
}
