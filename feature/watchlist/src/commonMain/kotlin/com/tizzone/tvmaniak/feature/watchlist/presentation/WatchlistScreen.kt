package com.tizzone.tvmaniak.feature.watchlist.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WatchlistScreenRoute(
) {
    WatchlistScreen()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WatchlistScreen(
) {
    Text(text = "Watchlist")
}
