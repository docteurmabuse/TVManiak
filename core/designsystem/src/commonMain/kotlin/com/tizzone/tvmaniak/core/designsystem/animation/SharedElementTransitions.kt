package com.tizzone.tvmaniak.core.designsystem.animation

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@OptIn(ExperimentalAnimationSpecApi::class)
object SharedElementTransitions {
    const val TRANSITION_DURATION = 400
    const val FADE_DURATION = 150

    fun <T> materialContainerTransformSpec() =
        tween<T>(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing,
        )

    fun <T> materialSharedAxisXSpec() =
        tween<T>(
            durationMillis = TRANSITION_DURATION,
            easing = LinearOutSlowInEasing,
        )

    fun <T> materialFadeSpec() =
        tween<T>(
            durationMillis = FADE_DURATION,
            easing = LinearOutSlowInEasing,
        )

    fun <T> materialExpressiveSpring() =
        spring<T>(
            dampingRatio = 0.8f,
            stiffness = 380f,
        )

    fun <T> materialSharpSpring() =
        spring<T>(
            dampingRatio = 1f,
            stiffness = 1600f,
        )

    @OptIn(ExperimentalSharedTransitionApi::class)
    val materialContainerBoundsTransform =
        BoundsTransform { initialBounds, targetBounds ->
            keyframes {
                durationMillis = TRANSITION_DURATION
                initialBounds at 0 using LinearOutSlowInEasing
                targetBounds at TRANSITION_DURATION
            }
        }

    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationSpecApi::class)
    val arcMotionBoundsTransform =
        BoundsTransform { initialBounds, targetBounds ->
            keyframes {
                durationMillis = TRANSITION_DURATION
                initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                targetBounds at TRANSITION_DURATION
            }
        }

    @OptIn(ExperimentalSharedTransitionApi::class)
    val smoothBoundsTransform =
        BoundsTransform { _, _ ->
            materialExpressiveSpring()
        }

    val fadeInTransition =
        fadeIn(
            animationSpec = materialFadeSpec(),
        )

    val fadeOutTransition =
        fadeOut(
            animationSpec = materialFadeSpec(),
        )

    val slideInFromRight =
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = materialSharedAxisXSpec(),
        ) + fadeInTransition

    val slideOutToLeft =
        slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = materialSharedAxisXSpec(),
        ) + fadeOutTransition

    val slideInFromLeft =
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = materialSharedAxisXSpec(),
        ) + fadeInTransition

    val slideOutToRight =
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = materialSharedAxisXSpec(),
        ) + fadeOutTransition
}

data class SharedElementKeys(
    val tvShowId: Int,
) {
    val imageKey = "tv_show_image_$tvShowId"
    val titleKey = "tv_show_title_$tvShowId"
    val ratingKey = "tv_show_rating_$tvShowId"
    val cardKey = "tv_show_card_$tvShowId"
    val detailKey = "tv_show_detail_$tvShowId"
}
