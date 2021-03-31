package com.pjtsearch.opencontroller.ui.components

import android.graphics.Color
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_proto.Controller
import kotlin.concurrent.thread
import kotlin.math.atan2


@Composable
fun SwipePad(onAction: (DirectionVector) -> Unit = {}) {
    var startPosition: Offset? by remember { mutableStateOf(null) }
    val view = LocalView.current
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
        thread {
            while (swipeVector.magnitude > 0.15 && !stopped) {
                val time = (100 / swipeVector.magnitude).toLong()
                if (nextActionTime == null) nextActionTime = System.currentTimeMillis() + time
                if (nextActionTime != null && System.currentTimeMillis() > nextActionTime!!) {
                    hasRun = true
                    runAction(swipeVector)
                    nextActionTime = System.currentTimeMillis() + time
                }
                Thread.sleep(time + 20)
            }
        }
        onDispose { stopped = true }
    }

    Box(Modifier.padding(top = 50.dp)) {
        Box(
            Modifier
                .background(MaterialTheme.colors.primary, shapes.small)
                .height(350.dp)
                .width(400.dp)
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
        ) {
            Column {
                Text(swipeVector.toString())
            }
        }
    }
}

sealed class DirectionVector {
    abstract val magnitude: Float
    data class Up(override val magnitude: Float) : DirectionVector()
    data class Down(override val magnitude: Float) : DirectionVector()
    data class Left(override val magnitude: Float) : DirectionVector()
    data class Right(override val magnitude: Float) : DirectionVector()
    object Zero : DirectionVector() { override val magnitude = 0f }
}

infix fun Offset.dot(that: Offset) =
    this.x * that.x + this.y * that.y