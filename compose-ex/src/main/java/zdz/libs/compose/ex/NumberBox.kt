package zdz.libs.compose.ex

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
private inline fun <N> NumberBoxCore(
    value: N,
    noinline commit: () -> Unit,
    crossinline change: (plus: Boolean) -> Unit,
    crossinline textChanged: (String) -> Unit?,
    modifier: Modifier,
    innerModifier: Modifier,
    noinline present: @Composable (@Composable () -> Unit) -> Unit,
    enabled: Boolean,
) {
    var valueString by remember(value) { mutableStateOf(value.toString()) }
    val focus = LocalFocusManager.current
    val iconColor by colorScheme.onSurfaceVariant.status(enabled = enabled)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            painter = R.drawable.baseline_remove_24.ptr,
            contentDescription = "minus",
            tint = iconColor,
            modifier = Modifier
                .clip(ShapeDefaults.Small)
                .repeatable(enabled = enabled, onEnd = commit) {
                    change(false)
                    valueString = value.toString()
                }
                .square()
        )
        BasicTextField(
            value = valueString,
            enabled = enabled,
            onValueChange = {
                valueString = it
                textChanged(it)
            },
            modifier = innerModifier,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = colorScheme.onSurface.status(enabled = enabled).value
            ),
            keyboardActions = KeyboardActions(onDone = {
                focus.clearFocus()
                textChanged(valueString) ?: run { valueString = value.toString() }
            }),
            singleLine = true,
            decorationBox = present
        )
        Icon(
            painter = R.drawable.baseline_add_24.ptr,
            contentDescription = "plus",
            tint = iconColor,
            modifier = Modifier
                .clip(ShapeDefaults.Small)
                .repeatable(enabled = enabled, onEnd = commit) {
                    change(true)
                    valueString = value.toString()
                }
                .square()
        )
    }
}

@Composable
fun NumberBox(
    value: Int,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    range: IntRange = 0..100,
    delta: Int = 1,
    enabled: Boolean = true,
    /** 必须调用一次传入的lambda函数 */
    present: @Composable (@Composable () -> Unit) -> Unit = { it() },
    onValueChanged: (Int) -> Unit,
) {
    var localValue by remember(value) { mutableIntStateOf(value) }
    val update: (Int) -> Unit = { localValue = it.coerceIn(range) }
    val localEnabled = enabled && (localValue != range.first || localValue != range.last)
    NumberBoxCore(
        value = localValue,
        commit = { onValueChanged(localValue) },
        change = { update(localValue + if (it) delta else -delta) },
        textChanged = { it.toIntOrNull()?.let(update) },
        present = present,
        modifier = modifier,
        innerModifier = innerModifier,
        enabled = localEnabled,
    )
}

@Composable
fun NumberBox(
    value: Float,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    range: ClosedRange<Float> = 0f..1f,
    delta: Float = Float.MIN_VALUE,
    enabled: Boolean = true,
    /** 必须调用一次传入的lambda函数 */
    present: @Composable (@Composable () -> Unit) -> Unit = { it() },
    onValueChanged: (Float) -> Unit,
) {
    var localValue by remember(value) { mutableFloatStateOf(value) }
    val update: (Float) -> Unit = { localValue = it.coerceIn(range) }
    val localEnabled = enabled && (localValue != range.start || localValue != range.endInclusive)
    NumberBoxCore(
        value = localValue,
        commit = { onValueChanged(localValue) },
        change = { update(localValue + if (it) delta else -delta) },
        textChanged = { it.toFloatOrNull()?.let(update) },
        present = present,
        modifier = modifier,
        innerModifier = innerModifier,
        enabled = localEnabled,
    )
}