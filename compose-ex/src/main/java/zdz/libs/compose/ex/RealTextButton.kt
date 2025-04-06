package zdz.libs.compose.ex

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) = TextButton(onClick = onClick, modifier = modifier, enabled = enabled) {
    Text(text = text, style = style, color = color)
}