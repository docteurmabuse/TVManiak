package com.tizzone.tvmaniak.feature.tvShowDetails.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementKeys
import com.tizzone.tvmaniak.core.designsystem.animation.SharedElementTransitions
import com.tizzone.tvmaniak.core.designsystem.component.RichHtmlText
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakImage
import com.tizzone.tvmaniak.core.designsystem.component.TvManiakRating
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.cast_section
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Detail(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    tvShowDetail: TvShowDetail,
    cast: List<ShowCastSummary>,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(SharedElementKeys(tvShowDetail.id).detailKey),
                        animatedVisibilityScope = animatedContentScope,
                        boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
                        ),
            horizontalAlignment = Alignment.Start,
        ) {
              TvManiakImage(
                modifier =
                    Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth(),
                imageUrl = tvShowDetail.largeImageUrl,
                contentDescription = tvShowDetail.name,
                cacheKey = SharedElementKeys(tvShowDetail.id).imageKey,
            )
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = tvShowDetail.name,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier =
                        Modifier
                            .wrapContentWidth()

                )
                TvManiakRating(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    rating = tvShowDetail.rating ?: 0f,
                )
            }

            RichHtmlText(
                html = tvShowDetail.summary,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
            )

            // Cast Section
            if (cast.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.cast_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                ) {
                    items(cast) { actor ->
                        CastMemberCard(actor)
                    }
                }
            }
        }
    }
}
