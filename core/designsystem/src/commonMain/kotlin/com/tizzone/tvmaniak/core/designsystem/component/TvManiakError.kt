package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.error
import com.tizzone.tvmaniak.resources.something_went_wrong
import org.jetbrains.compose.resources.stringResource

@Composable
fun TvManiakError(
    modifier: Modifier = Modifier,
    text: String = stringResource(Res.string.something_went_wrong),
) {
    Box(
        modifier =
            modifier
                .testTag(stringResource(Res.string.error)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
