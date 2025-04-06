package zdz.libs.compose.ex.model

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel as SourceVM

interface MVI<STATE: MVI.State, INTENT: MVI.Intent<STATE>> {
    interface State
    fun interface Intent<STATE : State> {
        suspend fun reduce(state: STATE): STATE
    }

    abstract class ViewModel<STATE : State, INTENT : Intent<STATE>> : SourceVM() {
        abstract val state: STATE
    }

    @Composable
    operator fun invoke(vm: ViewModel<STATE, INTENT>)
}