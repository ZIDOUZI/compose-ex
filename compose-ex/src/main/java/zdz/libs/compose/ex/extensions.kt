package zdz.libs.compose.ex

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp

@Composable
fun @receiver:DrawableRes Int.AsIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    description: String? = null
) {
    val painter = painterResource(this)
    val color = tint.takeOrElse { colorScheme.onSurface }
    Icon(
        painter = painter,
        contentDescription = description,
        tint = color,
        modifier = modifier
    )
}
@Composable
fun @receiver:DrawableRes Int.AsIcon(
    tint: Color = Color.Unspecified,
    size: Dp,
    description: String? = null
) {
    val painter = painterResource(this)
    val color = tint.takeOrElse { colorScheme.onSurface }
    Icon(
        painter = painter,
        contentDescription = description,
        tint = color,
        modifier = Modifier.size(size)
    )
}

inline val @receiver:StringRes Int.str
    @Composable
    @ReadOnlyComposable
    get() = stringResource(id = this)

inline val @receiver:DrawableRes Int.ptr
    @Composable
    get() = painterResource(id = this)

@Composable
fun Color.status(enabled: Boolean) =
    rememberUpdatedState(newValue = if (enabled) this else copy(alpha = 0.38f))

fun Color.takeIf(predicate: (Color) -> Boolean): Color =
    if (predicate(this)) this else Color.Unspecified

fun Color.takeUnless(predicate: (Color) -> Boolean): Color =
    if (predicate(this)) Color.Unspecified else this

fun Modifier.square(calcSize: (Int, Int) -> Int = { w, h -> maxOf(w, h) }) =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.measuredWidth
        val height = placeable.measuredHeight
        val size = calcSize(width, height)
        layout(size, size) {
            placeable.placeRelative((size - width) / 2, (size - height) / 2)
        }
    }