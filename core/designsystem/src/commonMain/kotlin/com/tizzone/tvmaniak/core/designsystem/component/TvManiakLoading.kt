package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.tv_shows_screen
import org.jetbrains.compose.resources.stringResource

@Composable
fun TvManiakLoading(modifier: Modifier) {
    Box(
        modifier =
            modifier
                .testTag(stringResource(Res.string.tv_shows_screen))
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
