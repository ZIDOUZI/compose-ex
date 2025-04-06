package zdz.libs.compose.ex

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val titleHeadingPadding = Modifier.padding(start = 8.dp)

/**
 * @param[header]可组合的标题槽位
 * @param[modifier]修饰符,修饰[content]所在的Column
 * @param[fab]悬浮按钮槽位
 * @param[fabPosition]悬浮按钮放置的位置
 * @param[content]中间的内容
 */
@Composable
fun Title(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    fab: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.End,
    content: @Composable (BoxScope.(PaddingValues) -> Unit)? = null,
) = Scaffold(
    floatingActionButton = fab,
    floatingActionButtonPosition = fabPosition,
    modifier = modifier,
    containerColor = colorScheme.background
) {
    Column {
        header()
        Box(modifier = Modifier.fillMaxSize()) {
            content?.invoke(this, it)
        }
    }
}

@Composable
fun Heading(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) = Heading(
    title = title,
    modifier = modifier,
    subtitle = { subtitle?.let { Text(text = it) } }
)

@Composable
fun Heading(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: @Composable (() -> Unit),
) = Column(modifier = titleHeadingPadding.then(modifier)) {
    Text(text = title, style = typography.displayMedium)
    subtitle()
}

