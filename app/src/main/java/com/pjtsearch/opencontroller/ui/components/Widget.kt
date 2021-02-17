package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientView
import androidx.compose.material.AmbientContentColor;
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.icons
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import com.pjtsearch.opencontroller_lib_proto.Button.OptionalIconCase
import kotlin.concurrent.thread

@Composable
fun Widget(widget: WidgetOrBuilder, executor: OpenControllerLibExecutor, modifier: Modifier = Modifier) {
    val view = AmbientView.current
    when (widget.innerCase) {
        InnerCase.BUTTON ->
            Providers(AmbientContentColor provides MaterialTheme.colors.primary) {
                Box(Modifier
                .widthIn(65.dp, 70.dp)
                .heightIn(65.dp, 75.dp)
                .padding(2.dp)
                .clip(shapes.medium)
                .border(2.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.3f), shapes.medium)
                .clickable (role = Role.Button){
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    thread {
                        executor.executeAction(widget.button.action)
                    }
                }, Alignment.Center) {
                    when (widget.button.optionalIconCase) {
                        OptionalIconCase.ICON -> Icon(
                            icons[widget.button.icon]!!,
                            widget.button.text)
                        OptionalIconCase.OPTIONALICON_NOT_SET -> Text(widget.button.text)
                    }
                }
            }
        InnerCase.DYNAMIC_TEXT -> {
            var value: Any? by remember { mutableStateOf(null) }
            DisposableEffect(widget.dynamicText.value) {
                val unsub = executor.subscribeDynamicValue(widget.dynamicText.value) { value = it }
                onDispose { unsub() }
            }
            Text(value.toString(), modifier)
        }
        InnerCase.ROW -> Row(modifier, Arrangement.SpaceBetween) {
            widget.row.childList.map {
                Widget(it, executor)
            }
        }
        InnerCase.COLUMN -> Column(modifier, Arrangement.Top) {
            widget.column.childList.map {
                Widget(it, executor)
            }
        }
        InnerCase.ARROW_LAYOUT -> Column(modifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Widget(widget.arrowLayout.top, executor)
            }
            Row {
                Widget(widget.arrowLayout.left, executor)
                Widget(widget.arrowLayout.center, executor)
                Widget(widget.arrowLayout.right, executor)
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Widget(widget.arrowLayout.bottom, executor)
            }
        }
        InnerCase.SPACE -> Spacer(modifier)
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }
}