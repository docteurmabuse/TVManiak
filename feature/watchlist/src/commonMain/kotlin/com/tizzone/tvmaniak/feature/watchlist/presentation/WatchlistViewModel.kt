package com.tizzone.tvmaniak.feature.watchlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.domain.tvshows.GetWatchlistUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.RemoveFromWatchListUseCase
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    getWatchlistUseCase: GetWatchlistUseCase,
    private val removeFromWatchListUseCase: RemoveFromWatchListUseCase,
) : ViewModel() {
    private val _isGridView = MutableStateFlow(true)
    val isGridView = _isGridView.asStateFlow()

    val watchlistShows: StateFlow<List<TvShowSummary>> =
        getWatchlistUseCase()
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun removeFromWatchlist(showId: Int) {
        viewModelScope.launch {
            val result = removeFromWatchListUseCase(showId)
            result.fold(
                { error ->
                    Logger.e("WatchlistViewModel") { "Failed to remove from watchlist: $error" }
                },
                {
                    Logger.d("WatchlistViewModel") { "Successfully removed show $showId from watchlist" }
                },
            )
        }
    }
}
