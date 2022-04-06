package com.pjtsearch.opencontroller.extensions

sealed class DirectionVector {
    abstract val magnitude: Float

    data class Up(override val magnitude: Float) : DirectionVector()
    data class Down(override val magnitude: Float) : DirectionVector()
    data class Left(override val magnitude: Float) : DirectionVector()
    data class Right(override val magnitude: Float) : DirectionVector()
    object Zero : DirectionVector() {
        override val magnitude = 0f
    }
}