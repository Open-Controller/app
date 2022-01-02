package com.pjtsearch.opencontroller.extensions

import com.github.kittinunf.fuel.httpGet
import com.github.michaelbull.result.*
import com.pjtsearch.opencontroller.executor.House
import com.pjtsearch.opencontroller.executor.OpenControllerLibExecutor
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_proto.Module

fun resolveHouseRef(houseRef: HouseRef): Result<House, Panic> =
    when (houseRef.innerCase) {
        HouseRef.InnerCase.NETWORK_HOUSE_REF ->
            OpenControllerLibExecutor().interpretModule(
                Module.parseFrom(
                    houseRef.networkHouseRef.url.httpGet().response().third.get()
                )
            ).map {it as House}
        HouseRef.InnerCase.INNER_NOT_SET -> TODO()
        null -> TODO()
    }
