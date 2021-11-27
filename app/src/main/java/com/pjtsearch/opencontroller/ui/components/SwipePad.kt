package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import com.pjtsearch.opencontroller.extensions.DirectionVector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SwipePad(modifier: Modifier = Modifier, onAction: (DirectionVector) -> Unit = {}) {
    var startPosition: Offset? by remember { mutableStateOf(null) }
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    var nextActionTime: Long? by remember { mutableStateOf(null) }
    var hasRun by remember { mutableStateOf(false) }
    var swipeVector: DirectionVector by remember { mutableStateOf(DirectionVector.Zero) }
    fun reset() {
        startPosition = null
        swipeVector = DirectionVector.Zero
        nextActionTime = null
        hasRun = false
    }
    fun runAction(vec: DirectionVector) {
        onAction(vec)
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    DisposableEffect(key1 = swipeVector) {
        var stopped = false
        scope.launch {
            while (swipeVector.magnitude > 0.15 && !stopped) {
                val time = (300 / swipeVector.magnitude).toLong()
                if (nextActionTime == null) nextActionTime = System.currentTimeMillis() + 500
                if (nextActionTime != null && System.currentTimeMillis() > nextActionTime!!) {
                    hasRun = true
                    runAction(swipeVector)
                    nextActionTime = System.currentTimeMillis() + time
                }
                delay(time + 20)
            }
        }
        onDispose { stopped = true }
    }
    Box(modifier
        .pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = {
                    if (!hasRun) {
                        runAction(swipeVector)
                    }
                    reset()
                },
                onDragCancel = { reset() },
                onDragStart = { reset() }
            ) { change, _ ->
                change.consumePositionChange()
                if (startPosition == null) startPosition = change.previousPosition
                val vec = (change.position - startPosition!!)
                val swipeOffset = Offset(vec.x / 600f, vec.y / -600f)
                val dirs = listOf(
                    DirectionVector.Up(swipeOffset.dot(Offset(0f, 1f))),
                    DirectionVector.Down(swipeOffset.dot(Offset(0f, -1f))),
                    DirectionVector.Left(swipeOffset.dot(Offset(-1f, 0f))),
                    DirectionVector.Right(swipeOffset.dot(Offset(1f, 0f)))
                )
                swipeVector = dirs.maxByOrNull { d -> d.magnitude }!!

            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { runAction(DirectionVector.Zero) }
    )
}

infix fun Offset.dot(that: Offset) =
    this.x * that.x + this.y * that.y