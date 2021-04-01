package com.pjtsearch.opencontroller.extensions

import androidx.compose.runtime.*
import kotlin.concurrent.thread

@Composable
fun dragMagnitudeTimer(swipeVector: DirectionVector, nextActionTime: MutableState<Long?>, onRun: () -> Unit) {
    DisposableEffect(key1 = swipeVector) {
        var stopped = false
        thread {
            while (swipeVector.magnitude > 0.15 && !stopped) {
                val time = (300 / swipeVector.magnitude).toLong()
                if (nextActionTime.value == null) nextActionTime.value = System.currentTimeMillis() + 500
                if (nextActionTime.value != null && System.currentTimeMillis() > nextActionTime.value!!) {
                    onRun()
                    nextActionTime.value = System.currentTimeMillis() + time
                }
                Thread.sleep(time + 20)
            }
        }
        onDispose { stopped = true }
    }
}