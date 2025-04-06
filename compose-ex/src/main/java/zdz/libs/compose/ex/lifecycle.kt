package zdz.libs.compose.ex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun OnLifecycle(callback: (Lifecycle.Event) -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val callbackState by rememberUpdatedState(callback)

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            callbackState(event)
        }

        lifecycle.addObserver(observer)

        onDispose { lifecycle.removeObserver(observer) }
    }
}

@Composable
fun OnResume(callback: () -> Unit) {
    OnLifecycle {
        if (it == Lifecycle.Event.ON_RESUME) callback()
    }
}

@Composable
fun OnPause(callback: () -> Unit) {
    OnLifecycle {
        if (it == Lifecycle.Event.ON_PAUSE) callback()
    }
}