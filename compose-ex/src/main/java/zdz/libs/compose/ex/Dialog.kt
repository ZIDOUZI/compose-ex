package zdz.libs.compose.ex

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@JvmInline
value class Dialog private constructor(private val visible: MutableState<Boolean>) {
    
    fun show() {
        visible.value = true
    }
    
    fun hide() {
        visible.value = false
    }
    
    val value get() = visible.value
    
    companion object {
        
        @Composable
        operator fun invoke(
            confirmLabel: String,
            onConfirm: Dialog.() -> Unit,
            modifier: Modifier = Modifier,
            visible: Boolean = false,
            title: String? = null,
            text: String? = null,
            dismissLabel: String? = null,
            onDismiss: Dialog.() -> Unit = { hide() },
            neutralLabel: String? = null,
            onNeutral: (Dialog.() -> Unit)? = null,
            icon: @Composable (() -> Unit)? = null,
            shape: Shape = AlertDialogDefaults.shape,
            containerColor: Color = AlertDialogDefaults.containerColor,
            iconContentColor: Color = AlertDialogDefaults.iconContentColor,
            titleContentColor: Color = AlertDialogDefaults.titleContentColor,
            textContentColor: Color = AlertDialogDefaults.textContentColor,
            tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
            properties: DialogProperties = DialogProperties(),
        ): Dialog = invoke(
            confirmLabel = confirmLabel,
            onConfirm = onConfirm,
            modifier = modifier,
            visible = visible,
            title = title,
            content = {
                text?.let {
                    Text(
                        text = it,
                        style = typography.bodyMedium,
                        color = textContentColor
                    )
                }
            },
            dismissLabel = dismissLabel,
            onDismiss = onDismiss,
            neutralLabel = neutralLabel,
            onNeutral = onNeutral,
            icon = icon,
            shape = shape,
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            tonalElevation = tonalElevation,
            properties = properties
        )
        
        @Composable
        operator fun invoke(
            modifier: Modifier = Modifier,
            visible: Boolean = false,
            title: String? = null,
            content: @Composable (Dialog.() -> Unit)? = null,
            confirmLabel: String? = null,
            onConfirm: (Dialog.() -> Unit)? = null,
            dismissLabel: String? = null,
            onDismiss: Dialog.() -> Unit = { hide() },
            neutralLabel: String? = null,
            onNeutral: (Dialog.() -> Unit)? = null,
            icon: @Composable (() -> Unit)? = null,
            shape: Shape = AlertDialogDefaults.shape,
            containerColor: Color = AlertDialogDefaults.containerColor,
            iconContentColor: Color = AlertDialogDefaults.iconContentColor,
            titleContentColor: Color = AlertDialogDefaults.titleContentColor,
            tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
            properties: DialogProperties = DialogProperties(),
        ): Dialog = remember { Dialog(mutableStateOf(visible)) }.also { d ->
            if (d.visible.value) AlertDialog(
                confirmLabel = confirmLabel,
                onConfirm = { onConfirm?.invoke(d) },
                onDismiss = { onDismiss(d) },
                modifier = modifier,
                title = title,
                content = { content?.invoke(d) },
                dismissLabel = dismissLabel,
                neutralLabel = neutralLabel,
                onNeutral = { onNeutral?.invoke(d) },
                icon = icon,
                shape = shape,
                containerColor = containerColor,
                iconContentColor = iconContentColor,
                titleContentColor = titleContentColor,
                tonalElevation = tonalElevation,
                properties = properties,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable (() -> Unit)? = null,
    confirmLabel: String? = null,
    onConfirm: (() -> Unit)? = null,
    dismissLabel: String? = null,
    neutralLabel: String? = null,
    onNeutral: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
) = BasicAlertDialog(
    onDismissRequest = onDismiss,
    properties = properties,
    modifier = modifier
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            icon?.let {
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    Box(
                        Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
            }
            title?.let {
                Box(
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(if (icon == null) Alignment.Start else Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = it,
                        style = typography.headlineSmall,
                        color = titleContentColor
                    )
                }
            }
            Box(
                Modifier
                    .weight(weight = 1f, fill = false)
                    .padding(bottom = 24.dp)
                    .align(Alignment.Start)
            ) {
                content?.invoke()
            }
            Box(modifier = Modifier.align(Alignment.End)) {
                CompositionLocalProvider(LocalContentColor provides colorScheme.primary) {
                    Row {
                        neutralLabel?.let {
                            TextButton(
                                onClick = { onNeutral?.invoke() },
                                text = it,
                                style = typography.labelLarge,
                                color = colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        dismissLabel?.let {
                            TextButton(
                                onClick = onDismiss,
                                text = it,
                                style = typography.labelLarge,
                                color = colorScheme.primary
                            )
                        }
                        confirmLabel?.let {
                            TextButton(
                                onClick = { onConfirm?.invoke() },
                                text = it,
                                style = typography.labelLarge,
                                color = colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}