package com.pjtsearch.opencontroller.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapOr
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


fun <T, U> Optional<T>.mapOr(default: U, transform: (T) -> U): U {
    return if (this.isPresent) {
        transform(this.get())
    } else {
        default
    }
}