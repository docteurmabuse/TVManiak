package com.tizzone.tvmaniak.feature.tvShowDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowDetailsUseCase
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailEvent
import com.tizzone.tvmaniak.feature.tvShowDetails.model.TvShowDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TvShowDetailsViewModel(
    private val getTvShowDetails: GetTvShowDetailsUseCase,
) : ViewModel() {
    private var _uiState = MutableStateFlow<TvShowDetailState>(TvShowDetailState.Loading)
    val uiState: StateFlow<TvShowDetailState> =
        _uiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TvShowDetailState.Loading,
            )

    fun onEvent(event: TvShowDetailEvent) {
        when (event) {
            is TvShowDetailEvent.LoadTvShowDetail -> getDetail(event.showId)
        }
    }

    private fun getDetail(showId: Int) =
        viewModelScope.launch {
            _uiState.update { TvShowDetailState.Loading }
            val result = getTvShowDetails(showId)
            _uiState.update {
                when (result) {
                    is Either.Left -> {
                        Logger.e("Error fetching show details: ${result.value}")
                        TvShowDetailState.Error(result.value.toString())
                    }

                    is Either.Right -> {
                        TvShowDetailState.Success(
                            tvShowDetail = result.value.details,
                            cast = result.value.cast,
                        )
                    }
                }
            }
        }
}
