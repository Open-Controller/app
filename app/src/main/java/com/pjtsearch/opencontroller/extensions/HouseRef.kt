package com.pjtsearch.opencontroller.extensions

import com.github.kittinunf.fuel.httpGet
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.pjtsearch.opencontroller_lib_proto.House

interface HouseRef {
    val displayName: String
    fun resolve(): Result<House, Throwable>
}

class NetworkHouseRef(override val displayName: String, private val url: String) : HouseRef {
    override fun resolve(): Result<House, Throwable> = runCatching {
        House.parseFrom(url.httpGet().response().third.get())
    }
}