package com.tizzone.tvmaniak.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.ui.graphics.vector.ImageVector
import com.tizzone.tvmaniak.feature.tvshows.navigation.TvShowsNavigation
import com.tizzone.tvmaniak.feature.watchlist.navigation.WatchlistNavigation
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.nav_tv_shows
import com.tizzone.tvmaniak.resources.nav_watchlist
import org.jetbrains.compose.resources.StringResource

enum class TvManiakBottomNavigation(
    val route: Any,
    val titleResource: StringResource,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        route = TvShowsNavigation,
        titleResource = Res.string.nav_tv_shows,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    WATCHLIST(
        route = WatchlistNavigation,
        titleResource = Res.string.nav_watchlist,
        selectedIcon = Icons.Filled.WatchLater,
        unselectedIcon = Icons.Outlined.WatchLater,
    ),
}
