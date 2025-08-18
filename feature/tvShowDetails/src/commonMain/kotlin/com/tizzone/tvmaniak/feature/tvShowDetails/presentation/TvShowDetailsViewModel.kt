package com.tizzone.tvmaniak.feature.tvShowDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.domain.tvshows.AddToWatchListUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowDetailsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.RemoveFromWatchListUseCase
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailEvent
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TvShowDetailsViewModel(
    private val getTvShowDetails: GetTvShowDetailsUseCase,
    private val addToWatchList: AddToWatchListUseCase,
    private val removeFromWatchList: RemoveFromWatchListUseCase,
) : ViewModel() {
    private var tvShowId = MutableStateFlow<Int?>(null)
    private val _uiState = MutableStateFlow<TvShowDetailState>(TvShowDetailState.Loading)
    val uiState =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TvShowDetailState.Loading,
        )

    fun onEvent(event: TvShowDetailEvent) {
        when (event) {
            is TvShowDetailEvent.LoadTvShowDetail -> {
                tvShowId.value = event.showId
                loadTvShowDetails(event.showId)
            }

            is TvShowDetailEvent.AddToWatchList -> {
                addTvShowToWatchList(event.showId)
            }

            is TvShowDetailEvent.RemoveFromWatchList -> {
                removeTvShowFromWatchList(event.showId)
            }
        }
    }

    private fun loadTvShowDetails(showId: Int) {
        viewModelScope.launch {
            _uiState.value = TvShowDetailState.Loading
            val result = getTvShowDetails(showId)
            result.fold(
                { error ->
                    Logger.e("TvShowDetailsViewModel") { "Failed to fetch show details: $error" }
                    _uiState.value = TvShowDetailState.Error(error.toString())
                },
                { details ->
                    _uiState.value =
                        TvShowDetailState.Success(
                            tvShowDetail = details.details,
                            cast = details.cast,
                        )
                },
            )
        }
    }

    private fun updateWatchlistStatus(isInWatchlist: Boolean) {
        val currentState = _uiState.value
        if (currentState is TvShowDetailState.Success) {
            _uiState.value =
                currentState.copy(
                    tvShowDetail = currentState.tvShowDetail.copy(isInWatchlist = isInWatchlist),
                )
        }
    }

    private fun addTvShowToWatchList(tvShowId: Int) {
        viewModelScope.launch {
            val result = addToWatchList(tvShowId)
            result.fold(
                { error ->
                    Logger.e("TvShowDetailsViewModel") { "Failed to add to watchlist: $error" }
                },
                {
                    updateWatchlistStatus(true)
                },
            )
        }
    }

    private fun removeTvShowFromWatchList(tvShowId: Int) {
        viewModelScope.launch {
            val result = removeFromWatchList(tvShowId)
            result.fold(
                { error ->
                    Logger.e("TvShowDetailsViewModel") { "Failed to remove from watchlist: $error" }
                },
                {
                    updateWatchlistStatus(false)
                },
            )
        }
    }
}
