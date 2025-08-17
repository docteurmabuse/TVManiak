package com.tizzone.tvmaniak.feature.tvshows.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tizzone.tvmaniak.feature.tvshows.presentation.TvShowsViewModel
import com.tizzone.tvmaniak.feature.tvshows.route.TvShowsScreenRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TvShowsNavigation

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.tvShowsGraph(
    onTvShowClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    windowSizeClass: WindowSizeClass,
) {
    composable<TvShowsNavigation> { entry ->
        val viewModel = koinViewModel<TvShowsViewModel>()
        TvShowsScreenRoute(
            viewModel = viewModel,
            onTvShowClick = onTvShowClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable,
            windowSizeClass = windowSizeClass,
        )
    }
}
