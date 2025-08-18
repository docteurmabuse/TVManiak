package com.tizzone.tvmaniak.feature.tvShowDetails.presentation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakError
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakLoading
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakTopBar
import com.tizzone.tvmaniak.feature.tvShowDetails.component.Detail
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailEvent
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailState
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.close_button
import com.tizzone.tvmaniak.resources.tv_shows_screen
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailScreen(
    uiState: TvShowDetailState,
    onBackClick: () -> Unit,
    onEvent: (TvShowDetailEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val title = mutableStateOf("")

    Scaffold(
        modifier =
            Modifier
                .testTag(stringResource(Res.string.tv_shows_screen)),
        topBar = {
            TvManiakTopBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(Res.string.close_button),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(4.dp),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        when (uiState) {
            TvShowDetailState.Loading -> {
                TvManiakLoading(
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is TvShowDetailState.Error -> {
                TvManiakError(
                    modifier = Modifier.padding(innerPadding),
                    text = uiState.message,
                )
            }

            is TvShowDetailState.Success -> {
                title.value = uiState.tvShowDetail.name
                Detail(
                    modifier = Modifier.padding(),
                    tvShowDetail = uiState.tvShowDetail,
                    cast = uiState.cast,
                    onEvent = onEvent,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                )
            }
        }
    }
}
