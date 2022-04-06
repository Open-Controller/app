package com.pjtsearch.opencontroller.extensions

import java.util.*


fun <T, U> Optional<T>.mapOr(default: U, transform: (T) -> U): U {
    return if (this.isPresent) {
        transform(this.get())
    } else {
        default
    }
}

fun <T, U> Optional<T>.mapOrElse(getDefault: () -> U, transform: (T) -> U): U {
    return if (this.isPresent) {
        transform(this.get())
    } else {
        getDefault()
    }
}