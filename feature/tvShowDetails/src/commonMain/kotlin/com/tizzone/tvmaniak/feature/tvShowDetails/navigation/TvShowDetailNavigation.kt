package com.tizzone.tvmaniak.feature.tvShowDetails.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tizzone.tvmaniak.feature.tvShowDetails.presentation.TvShowDetailsViewModel
import com.tizzone.tvmaniak.feature.tvShowDetails.route.TvShowDetailScreenRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
internal class TvShowDetailScreen(
    val id: Int,
)

fun NavController.navigateToTvShowDetail(
    id: Int,
    navOptions: NavOptions? = null,
) = navigate(
    route = TvShowDetailScreen(id),
    navOptions = navOptions,
)

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.detailTvShowScreen(
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<TvShowDetailScreen> { entry ->
        val viewModel = koinViewModel<TvShowDetailsViewModel>()
        val id = entry.toRoute<TvShowDetailScreen>().id

        TvShowDetailScreenRoute(
            tvShowId = id,
            viewModel = viewModel,
            onBackClick = onBackClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable,
        )
    }
}
