package com.pjtsearch.opencontroller.extensions

import com.github.kittinunf.fuel.httpGet
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_proto.House

fun resolveHouseRef(houseRef: HouseRef): Result<House, Throwable> = runCatching {
    when (houseRef.innerCase) {
        HouseRef.InnerCase.NETWORK_HOUSE_REF ->
            House.parseFrom(houseRef.networkHouseRef.url.httpGet().response().third.get())
        HouseRef.InnerCase.INNER_NOT_SET -> throw Error("House ref not set")
    }
}