package zdz.libs.compose.ex

import android.view.ViewConfiguration
import androidx.compose.foundation.Indication
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.foundation.clickable as click


fun Modifier.draggable(
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (Offset) -> Unit,
) = this.pointerInput(Unit) {
    detectDragGestures(
        onDragStart = onDragStart, onDragEnd = onDragEnd, onDragCancel = onDragCancel
    ) { change, offset ->
        change.consume()
        onDrag(offset)
    }
}

fun Modifier.clickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = this.composed {
    click(
        interactionSource ?: remember { MutableInteractionSource() },
        indication,
        enabled,
        onClickLabel,
        role,
        onClick
    )
}

private val ripple = ripple()

fun Modifier.repeatable(
    enabled: Boolean = true,
    delayMillis: Int? = null,
    minimumMillis: Int = 10,
    onEnd: (() -> Unit)? = null,
    onRepeat: () -> Unit,
) = this.composed(inspectorInfo = debugInspectorInfo {
    properties["enabled"] = enabled
    properties["delayMillis"] = delayMillis
    properties["onEnd"] = onEnd
    properties["onRepeat"] = onRepeat
}) {
    // rememberUpdatedState is needed on State objects accessed from the gesture callback,
    // otherwise the callback would only get the outdated values.
    val rememberedIsEnabled by rememberUpdatedState(enabled)
    val rememberedOnRepeat by rememberUpdatedState(onRepeat)
    val rememberedOnEnd by rememberUpdatedState(onEnd)
    
    val interaction = remember { MutableInteractionSource() }
    val longPressTimeout = (delayMillis ?: ViewConfiguration.getLongPressTimeout()).milliseconds
    
    val scope = rememberCoroutineScope()
    
    Modifier
        .pointerInput(Unit) {
            detectTapGestures(onPress = { offset ->
                if (rememberedIsEnabled) {
                    try {
                        val job = scope.launch {
                            var count = 0
                            
                            try {
                                delay(longPressTimeout)
                                var repeatInterval = 100L
                                
                                while (rememberedIsEnabled) {
                                    rememberedOnRepeat()
                                    
                                    delay(repeatInterval)
                                    
                                    if (count++ == 10 && repeatInterval > minimumMillis) repeatInterval = 10
                                    if (count++ == 100 && repeatInterval > minimumMillis) repeatInterval = 1
                                    // TODO
                                }
                            } finally {
                                withContext(NonCancellable) { if (count == 0) rememberedOnRepeat() }
                            }
                        }
                        
                        val pressInteraction = PressInteraction.Press(offset)
                        interaction.emit(pressInteraction)
                        
                        val success = tryAwaitRelease()
                        
                        val endInteraction = if (success) PressInteraction.Release(pressInteraction)
                        else PressInteraction.Cancel(pressInteraction)
                        
                        interaction.emit(endInteraction)
                        
                        job.cancelAndJoin()
                    } finally {
                        rememberedOnEnd?.invoke()
                    }
                }
            })
        }
        .indication(interactionSource = interaction, indication = ripple)
}