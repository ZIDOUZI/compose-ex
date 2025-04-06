package zdz.libs.compose.ex.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel as SourceVM
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVI (Model-View-Intent) 架构模式的实现接口。
 * 
 * MVI 是一种单向数据流架构模式，它将用户界面分为三个主要组件：
 * - Model：表示应用程序的状态
 * - View：负责渲染 UI 并将用户操作转发为意图
 * - Intent：表示改变状态的意图
 * 
 * 使用方法：
 * 1. 定义一个实现 [MVI.State] 的状态类
 * 2. 定义一个或多个实现 [MVI.Intent] 的意图类
 * 3. 创建一个继承 [MVI.ViewModel] 的视图模型类
 * 4. 使用 [invoke] 操作符将视图模型与 Composable UI 连接
 * 
 * @sample [zdz.libs.compose.ex.samples.MVISample]
 */
interface MVI<STATE: MVI.State, INTENT: MVI.Intent<STATE>> {
    /**
     * 表示应用程序状态的接口。
     * 
     * 状态应该是不可变的，通常使用数据类实现。
     * 状态包含了渲染 UI 所需的所有数据。
     * 
     * 示例：
     * ```
     * data class CounterState(val count: Int = 0) : MVI.State
     * ```
     */
    interface State
    
    /**
     * 表示用户意图的函数式接口。
     * 
     * 意图代表了用户想要执行的操作，通过 reduce 函数将当前状态转换为新状态。
     * 通常使用密封类（sealed class）实现，以便于区分不同类型的意图。
     * 
     * 示例：
     * ```
     * sealed class CounterIntent : MVI.Intent<CounterState> {
     *     object Increment : CounterIntent() {
     *         override suspend fun reduce(state: CounterState): CounterState {
     *             return state.copy(count = state.count + 1)
     *         }
     *     }
     * }
     * ```
     *
     * @param STATE 状态类型，必须实现 [State] 接口
     */
    fun interface Intent<STATE : State> {
        /**
         * 将当前状态转换为新状态的函数。
         * 
         * @param state 当前状态
         * @return 新的状态
         */
        suspend fun reduce(state: STATE): STATE
    }

    /**
     * MVI 架构的视图模型基类。
     * 
     * 负责管理状态并处理意图，将意图转换为状态变化。
     * 提供了状态流和分发意图的方法。
     * 
     * @param STATE 状态类型，必须实现 [State] 接口
     * @param INTENT 意图类型，必须实现 [Intent] 接口
     */
    abstract class ViewModel<STATE : State, INTENT : Intent<STATE>> : SourceVM() {
        /**
         * 初始状态，子类必须实现此属性提供初始状态值
         */
        protected abstract val initialState: STATE
        
        private val _stateFlow = MutableStateFlow(initialState)
        
        /**
         * 状态流，用于观察状态变化
         */
        val stateFlow: StateFlow<STATE> = _stateFlow.asStateFlow()
        
        /**
         * 当前状态的快照
         */
        val currentState: STATE get() = stateFlow.value
        
        /**
         * 分发意图，触发状态变化
         * 
         * @param intent 要处理的意图
         */
        fun dispatch(intent: INTENT) {
            viewModelScope.launch {
                val newState = intent.reduce(currentState)
                _stateFlow.value = newState
            }
        }
        
        /**
         * 更新状态的辅助方法
         * 
         * @param transform 状态转换函数
         */
        protected fun updateState(transform: (STATE) -> STATE) {
            _stateFlow.value = transform(currentState)
        }
    }

    /**
     * 将视图模型与 Composable UI 连接的操作符函数
     * 
     * 这个函数允许在 Compose UI 中使用 MVI 模式，自动收集状态并提供分发函数
     * 
     * 示例：
     * ```
     * val viewModel = CounterViewModel()
     * MVI(viewModel) { state, dispatch ->
     *     Text("Count: ${state.count}")
     *     Button(onClick = { dispatch(CounterIntent.Increment) }) {
     *         Text("+")
     *     }
     * }
     * ```
     * 
     * @param vm 视图模型实例
     * @param content 构建 UI 的 Composable 函数，接收当前状态和分发函数作为参数
     */
    @Composable
    operator fun invoke(vm: ViewModel<STATE, INTENT>, content: @Composable (state: STATE, dispatch: (INTENT) -> Unit) -> Unit) {
        val state by vm.stateFlow.collectAsState()
        content(state) { intent -> vm.dispatch(intent) }
    }
}