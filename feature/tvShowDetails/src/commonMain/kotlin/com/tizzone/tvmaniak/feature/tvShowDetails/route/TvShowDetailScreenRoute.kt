package com.tizzone.tvmaniak.feature.tvShowDetails.route

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailEvent
import com.tizzone.tvmaniak.feature.tvShowDetails.presentation.TvShowDetailScreen
import com.tizzone.tvmaniak.feature.tvShowDetails.presentation.TvShowDetailsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowDetailScreenRoute(
    tvShowId: Int,
    viewModel: TvShowDetailsViewModel,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onEvent(TvShowDetailEvent.LoadTvShowDetail(tvShowId))
    }
    TvShowDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEvent = viewModel::onEvent,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
    )
}
