package com.tizzone.tvmaniak.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tizzone.tvmaniak.feature.tvShowDetails.navigation.detailTvShowScreen
import com.tizzone.tvmaniak.feature.tvShowDetails.navigation.navigateToTvShowDetail
import com.tizzone.tvmaniak.feature.tvshows.navigation.TvShowsNavigation
import com.tizzone.tvmaniak.feature.tvshows.navigation.tvShowsGraph
import com.tizzone.tvmaniak.feature.watchlist.navigation.watchlistGraph
import com.tizzone.tvmaniak.model.TvManiakBottomNavigation

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvManiakNavGraph(
    modifier: Modifier = Modifier,
    startDestination: Any = TvShowsNavigation,
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowSizeClass,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
        ) {
            TvManiakBottomNavigation.entries.forEach { destination ->
                when (destination) {
                    TvManiakBottomNavigation.HOME -> {
                        tvShowsGraph(
                            onTvShowClick = { tvShowId ->
                                navController.navigateToTvShowDetail(tvShowId)
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            windowSizeClass = windowSizeClass,
                        )
                        detailTvShowScreen(
                            onBackClick = navController::navigateUp,
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }

                    TvManiakBottomNavigation.WATCHLIST -> {
                        watchlistGraph(
                            onTvShowClick = { tvShowId ->
                                navController.navigateToTvShowDetail(tvShowId)
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }
                }
            }
        }
    }
}
