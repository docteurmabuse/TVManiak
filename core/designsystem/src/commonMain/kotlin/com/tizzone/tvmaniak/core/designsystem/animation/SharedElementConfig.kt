package com.tizzone.tvmaniak.core.designsystem.animation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
object SharedElementConfig {
    @Composable
    fun SharedTransitionScope.sharedImageElement(
        key: String,
        animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    ): Modifier =
        Modifier.sharedElement(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = SharedElementTransitions.smoothBoundsTransform,
            placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize,
        )

    @Composable
    fun SharedTransitionScope.sharedTextElement(
        key: String,
        animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    ): Modifier =
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = SharedElementTransitions.fadeInTransition,
            exit = SharedElementTransitions.fadeOutTransition,
            boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
        )

    @Composable
    fun SharedTransitionScope.sharedContainerElement(
        key: String,
        animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    ): Modifier =
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = SharedElementTransitions.materialContainerBoundsTransform,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
        )

    @Composable
    fun SharedTransitionScope.sharedRatingElement(
        key: String,
        animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    ): Modifier =
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = fadeIn(SharedElementTransitions.materialFadeSpec()),
            exit = fadeOut(SharedElementTransitions.materialFadeSpec()),
            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
        )
}
