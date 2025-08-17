
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakError
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakLoading
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.presentation.components.TvShowSummaryItemGrid
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.no_tv_shows
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun TvShowsGridView(
    contentPadding: PaddingValues,
    tvShows: LazyPagingItems<TvShowSummary>,
    onTvShowClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigationAnimatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
) {
    var isDetailScopeActive by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = modifier
            .padding(contentPadding)
            .fillMaxWidth(),
    ) {
        when (val refreshState = tvShows.loadState.refresh) {
            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TvManiakError(
                        modifier = Modifier.fillMaxWidth(),
                        text = refreshState.error.message ?: stringResource(Res.string.no_tv_shows)
                    )
                }
            }
            is LoadState.NotLoading -> {
                if (tvShows.itemCount == 0 && tvShows.loadState.append.endOfPaginationReached) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TvManiakError(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.no_tv_shows)
                        )
                    }
                } else {
                    items(
                        count = tvShows.itemCount,
                        key = tvShows.itemKey { it.id }
                    ) { index ->
                        val tvShow = tvShows[index]
                        tvShow?.let { currentShow ->
                            TvShowSummaryItemGrid(
                                modifier = Modifier,
                                onTvShowClick = {
                                    isDetailScopeActive = true
                                    onTvShowClick(currentShow.id)
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedContentScope = animatedContentScope,
                                navigationAnimatedContentScope = navigationAnimatedContentScope,
                                isDetailScopeActive = isDetailScopeActive,
                                id = currentShow.id,
                                url = currentShow.smallImageUrl,
                                name = currentShow.name,
                                rating = currentShow.rating ?: 0f,
                            )
                        }
                    }
                }
            }
            is LoadState.Loading -> {
                if (tvShows.itemCount == 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TvManiakLoading(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    }
                }
            }
        }
        when (val appendState = tvShows.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TvManiakLoading(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TvManiakError(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        text = appendState.error.message ?: stringResource(Res.string.no_tv_shows)
                    )
                }
            }
            is LoadState.NotLoading -> {
                // No additional UI needed
            }
        }
    }
}
