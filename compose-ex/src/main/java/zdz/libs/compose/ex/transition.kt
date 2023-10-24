package zdz.libs.compose.ex

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

private val positive: Boolean.(Int) -> Int = { if (this) it else -it }
private val negative: Boolean.(Int) -> Int = { if (this) -it else it }

sealed class Direction(val enter: Boolean.(Int) -> Int, val exit: Boolean.(Int) -> Int) {
    sealed class Vertical(enter: Boolean.(Int) -> Int, exit: Boolean.(Int) -> Int) :
        Direction(enter, exit) {
        data object Up : Vertical(positive, negative)
        data object Down : Vertical(negative, positive)
    }

    sealed class Horizontal(enter: Boolean.(Int) -> Int, exit: Boolean.(Int) -> Int) :
        Direction(enter, exit) {
        data object Left : Horizontal(positive, negative)
        data object Right : Horizontal(negative, positive)
    }
}

fun getTransformBy(direction: Direction.Vertical) =
    fun AnimatedContentTransitionScope<Boolean>.(): ContentTransform =
        fadeIn() + slideInVertically { direction.enter(targetState, it) } togetherWith
                fadeOut() + slideOutVertically { direction.exit(targetState, it) }

fun getTransformBy(direction: Direction.Horizontal) =
    fun AnimatedContentTransitionScope<Boolean>.(): ContentTransform =
        fadeIn() + slideInHorizontally { direction.enter(targetState, it) } togetherWith
                fadeOut() + slideOutHorizontally { direction.exit(targetState, it) }
