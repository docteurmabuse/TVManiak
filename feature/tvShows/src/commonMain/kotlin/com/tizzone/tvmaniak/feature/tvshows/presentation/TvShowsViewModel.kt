package com.tizzone.tvmaniak.feature.tvshows.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.domain.tvshows.AddToWatchListUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.RemoveFromWatchListUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.SearchTvShowsUseCase
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.model.TvShowsEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TvShowsViewModel(
    private val getTvShows: GetTvShowsUseCase,
    private val searchTvShows: SearchTvShowsUseCase,
    private val removeFromWatchList: RemoveFromWatchListUseCase,
    private val addToWatchList: AddToWatchListUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView = _isGridView.asStateFlow()

    val tvShows: Flow<PagingData<TvShowSummary>> =
        _searchQuery
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    getTvShows()
                } else {
                    searchTvShows(query).map { searchResults ->
                        PagingData.from(searchResults)
                    }
                }
            }.distinctUntilChanged()
            .cachedIn(viewModelScope)

    fun onEvent(event: TvShowsEvent) {
        when (event) {
            is TvShowsEvent.ToggleViewMode -> {
                _isGridView.value = !_isGridView.value
            }

            is TvShowsEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.query
            }

            is TvShowsEvent.SearchTvShows -> {
                _searchQuery.value = event.query
            }

            is TvShowsEvent.AddToWatchList -> {
                addTvShowToWatchList(event.showId)
            }

            is TvShowsEvent.RemoveFromWatchList -> {
                removeTvShowFromWatchList(event.showId)
            }
        }
    }

    private fun addTvShowToWatchList(tvShowId: Int) {
        viewModelScope.launch {
            val result = addToWatchList(tvShowId)
            result.fold(
                { error ->
                    Logger.e("TvShowsViewModel") { "Failed to add to watchlist: $error" }
                },
                {
                    Logger.d("TvShowsViewModel") { "Successfully added show $tvShowId to watchlist" }
                },
            )
        }
    }

    private fun removeTvShowFromWatchList(tvShowId: Int) {
        viewModelScope.launch {
            val result = removeFromWatchList(tvShowId)
            result.fold(
                { error ->
                    Logger.e("TvShowsViewModel") { "Failed to remove from watchlist: $error" }
                },
                {
                    Logger.d("TvShowsViewModel") { "Successfully removed show $tvShowId from watchlist" }
                },
            )
        }
    }
}
