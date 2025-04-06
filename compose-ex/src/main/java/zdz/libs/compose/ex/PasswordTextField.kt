package zdz.libs.compose.ex

import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.delay

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    hasNextFocus: Boolean = false,
    betterVisualTransformation: Boolean = true,
    onFocusLost: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    // magic value to relaunch effect
    // TODO: have a flash when add or delete chars
    var key by remember { mutableStateOf<Boolean?>(null) }

    var visualTransformation by remember { mutableStateOf(VisualTransformation.None) }

    LaunchedEffect(key1 = visible, key2 = key) {
        if (visible) {
            visualTransformation = VisualTransformation.None
            return@LaunchedEffect
        }
        if (key != null && betterVisualTransformation) {
            visualTransformation = VisualTransformation {
                val text = it.foldIndexed(StringBuilder()) { i, acc, c ->
                    if (i + 1 == it.length) acc.append(c) else acc.append('\u2022')
                }.toString()
                TransformedText(AnnotatedString(text), OffsetMapping.Identity)
            }
            delay(1000)
        }
        visualTransformation = PasswordVisualTransformation()
    }

    val focus = LocalFocusManager.current
    TextField(
        value = password,
        onValueChange = {
            key = if (it.isNotEmpty() && it.substring(0, it.length - 1) == password)
                key?.not() ?: true // change key value when password added chars in the end.
            else null // to relaunch effect and stopped if there is delaying now
            password = it
        },
        modifier = modifier.onFocusChange(onLost = { onFocusLost(password) }),
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        singleLine = true,
        visualTransformation = visualTransformation,
        trailingIcon = {
            IconButton(
                onClick = {
                    visible = !visible
                    key = null // avoid the effect of `if (key != null) { ... }` to run
                },
                modifier = Modifier.focusable(false),
                content = (if (visible) R.drawable.baseline_visibility_off_24
                else R.drawable.baseline_visibility_24).icon
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (hasNextFocus) focus.moveFocus(FocusDirection.Next) else focus.clearFocus()
        })
    )
}