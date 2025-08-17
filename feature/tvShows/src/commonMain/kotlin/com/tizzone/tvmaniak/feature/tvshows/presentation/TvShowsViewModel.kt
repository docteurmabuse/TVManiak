package com.tizzone.tvmaniak.feature.tvshows.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.SearchTvShowsUseCase
import com.tizzone.tvmaniak.core.model.TvShowSummary
import com.tizzone.tvmaniak.feature.tvshows.model.TvShowsEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TvShowsViewModel(
    private val getTvShows: GetTvShowsUseCase,
    private val searchTvShows: SearchTvShowsUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView = _isGridView.asStateFlow()
    val tvShows: Flow<PagingData<TvShowSummary>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                Logger.d("TvShowsViewModel") { "Using getTvShows() for empty query" }
                getTvShows()
            } else {
                // Create a fresh flow for each search to ensure proper reset
                flow {
                    try {
                        val searchResults = searchTvShows(query)
                        emit(PagingData.from(searchResults))
                    } catch (e: Exception) {
                        Logger.e("TvShowsViewModel") { "Search failed: ${e.message}" }
                        emit(PagingData.empty())
                    }
                }
            }
        }
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
        }
    }
}
