package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tizzone.tvmaniak.core.designsystem.color.AppColors.YellowRating
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.favorite_icon
import org.jetbrains.compose.resources.stringResource

@Composable
fun TvManiakRating(
    modifier: Modifier = Modifier,
    rating: Float,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.primary,
            text = rating.toString(),
            style = MaterialTheme.typography.titleSmall,
        )
        Icon(
            modifier = Modifier,
            imageVector = Icons.Default.Star,
            contentDescription = stringResource(Res.string.favorite_icon),
            tint = YellowRating,
        )
    }
}
