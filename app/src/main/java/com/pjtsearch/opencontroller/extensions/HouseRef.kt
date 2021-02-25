package com.pjtsearch.opencontroller.extensions

import com.github.kittinunf.fuel.httpGet
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.pjtsearch.opencontroller_lib_proto.House

data class HouseRef(val url: String)

fun resolveHouseRef(ref: HouseRef): Result<House, Throwable> = runCatching {
    House.parseFrom(ref.url.httpGet().response().third.get())
}
