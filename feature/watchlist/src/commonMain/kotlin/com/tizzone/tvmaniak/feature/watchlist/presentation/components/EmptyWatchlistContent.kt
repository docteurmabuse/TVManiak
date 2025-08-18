package com.tizzone.tvmaniak.feature.watchlist.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.tizzone.tvmaniak.core.designsystem.spacing.TvManiakSpacing
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.empty_watchlist
import com.tizzone.tvmaniak.resources.watchlist_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyWatchlistContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TvManiakSpacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.empty_watchlist),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.watchlist_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
