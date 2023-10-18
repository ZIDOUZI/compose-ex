package zdz.libs.compose.ex

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> TabPager(
    items: Iterable<T>,
    heading: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val state = rememberPagerState { items.count() }
    val scope = rememberCoroutineScope()
    
    Column(modifier = modifier) {
        ScrollableTabRow(
            selectedTabIndex = state.currentPage,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            // Add tabs for all of our pages
            items.forEachIndexed { index, it ->
                Tab(
                    text = { heading(it) },
                    selected = state.currentPage == index,
                    onClick = { scope.launch { state.animateScrollToPage(index) } },
                )
            }
        }
        
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = state,
            verticalAlignment = Alignment.Top,
            beyondBoundsPageCount = 0,
            pageContent = { content(items.elementAt(it)) },
        )
    }
}