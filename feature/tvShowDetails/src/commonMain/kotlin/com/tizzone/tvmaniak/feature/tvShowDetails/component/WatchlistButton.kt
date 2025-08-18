package com.tizzone.tvmaniak.feature.tvShowDetails.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.add_to_watchlist
import com.tizzone.tvmaniak.resources.remove_from_watchlist
import org.jetbrains.compose.resources.stringResource

@Composable
fun WatchlistButton(
    isInWatchlist: Boolean,
    id: Int,
    onWatchlistClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = true,
        onClick = { onWatchlistClick(id) },
        label = {
            Text(if (isInWatchlist) stringResource(Res.string.remove_from_watchlist) else stringResource(Res.string.add_to_watchlist) )
        },
        modifier = modifier,
        leadingIcon = {
            if (isInWatchlist) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Remove from watchlist",
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to watchlist",
                )
            }
        },
    )
}
