package com.pjtsearch.opencontroller.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result


fun <T, E : Exception> com.github.kittinunf.result.Result<T, E>.toResult(): Result<T, E> =
    this.component1()?.let {
        Ok(it)
    } ?: Err(this.component2()!!)