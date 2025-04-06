package zdz.libs.compose.ex.samples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import zdz.libs.compose.ex.model.MVI

object MVISample : MVI<MVISample.State, MVISample.Intent> {

    data class State(
        val count: Int = 0,
        val history: List<String> = emptyList()
    ) : MVI.State

    sealed class Intent : MVI.Intent<State> {
        /**
         * 增加计数的意图
         */
        object Increment : Intent() {
            override suspend fun reduce(state: State): State {
                return state.copy(
                    count = state.count + 1,
                    history = state.history + "增加到 ${state.count + 1}"
                )
            }
        }

        /**
         * 减少计数的意图
         */
        object Decrement : Intent() {
            override suspend fun reduce(state: State): State {
                return state.copy(
                    count = state.count - 1,
                    history = state.history + "减少到 ${state.count - 1}"
                )
            }
        }

        /**
         * 重置计数的意图
         */
        object Reset : Intent() {
            override suspend fun reduce(state: State): State {
                return State(history = state.history + "重置到 0")
            }
        }
    }

    class ViewModel : MVI.ViewModel<State, Intent>() {
        override val initialState: State = State()
    }

    @Composable
    fun CounterScreen(viewModel: MVISample.ViewModel = MVISample.ViewModel()) {
        MVISample(viewModel) { state, dispatch ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 计数显示
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "当前计数: ${state.count}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { dispatch(MVISample.Intent.Decrement) }) {
                        Text(text = "减少")
                    }

                    Button(onClick = { dispatch(MVISample.Intent.Reset) }) {
                        Text(text = "重置")
                    }

                    Button(onClick = { dispatch(MVISample.Intent.Increment) }) {
                        Text(text = "增加")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 历史记录
                if (state.history.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "操作历史",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            state.history.takeLast(5).forEach { action ->
                                Text(text = "• $action", modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
