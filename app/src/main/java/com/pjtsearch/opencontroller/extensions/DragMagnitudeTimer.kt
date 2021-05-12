package com.pjtsearch.opencontroller.extensions

import androidx.compose.runtime.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DragMagnitudeTimer(swipeVector: DirectionVector, nextActionTime: MutableState<Long?>, onRun: () -> Unit) {
    DisposableEffect(key1 = swipeVector) {
        var stopped = false
        GlobalScope.launch {
            while (swipeVector.magnitude > 0.15 && !stopped) {
                val time = (300 / swipeVector.magnitude).toLong()
                if (nextActionTime.value == null) nextActionTime.value = System.currentTimeMillis() + 500
                if (nextActionTime.value != null && System.currentTimeMillis() > nextActionTime.value!!) {
                    onRun()
                    nextActionTime.value = System.currentTimeMillis() + time
                }
                delay(time + 20)
            }
        }
        onDispose { stopped = true }
    }
}