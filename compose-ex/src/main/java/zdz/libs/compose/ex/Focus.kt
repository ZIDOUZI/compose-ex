package zdz.libs.compose.ex

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo


fun Modifier.onFocusChange(onFocusLost: (previous: FocusState?, next: FocusState) -> Unit) =
    this then FocusChangeElement(onFocusLost)

fun Modifier.onFocusChange(
    onLost: () -> Unit = {},
    onGained: () -> Unit = {},
    onInitial: (FocusState) -> Unit = {},
) = this then FocusChangeElement { previous, next ->
    if (previous?.isFocused == true && !next.isFocused) onLost()
    if (previous?.isFocused == false && next.isFocused) onGained()
    if (previous == null) onInitial(next)
}

private data class FocusChangeElement(
    val onFocusChange: (previous: FocusState?, next: FocusState) -> Unit
) : ModifierNodeElement<FocusChangeNode>() {
    override fun create() = FocusChangeNode(onFocusChange)
    override fun update(node: FocusChangeNode) {
        node.onFocusChange = onFocusChange
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "onFocusChanged"
        properties["onFocusChanged"] = onFocusChange
    }
}

private class FocusChangeNode(
    var onFocusChange: (previous: FocusState?, next: FocusState) -> Unit
) : FocusEventModifierNode, Modifier.Node() {
    var focusState: FocusState? = null
    override fun onFocusEvent(focusState: FocusState) {
        onFocusChange(this.focusState, focusState)
        this.focusState = focusState
    }
}