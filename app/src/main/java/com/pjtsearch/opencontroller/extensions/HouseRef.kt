package com.pjtsearch.opencontroller.extensions

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.michaelbull.result.*
import com.pjtsearch.opencontroller.executor.House
import com.pjtsearch.opencontroller.executor.OpenControllerLibExecutor
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_proto.Module

// TODO: Should be panic?
fun resolveHouseRef(houseRef: HouseRef): Result<Result<House, Panic>, Throwable> = binding {
    when (houseRef.innerCase) {
        HouseRef.InnerCase.NETWORK_HOUSE_REF -> {
            OpenControllerLibExecutor().interpretModule(
                runCatching {
                    Module.parseFrom(
                        houseRef.networkHouseRef.url.httpGet().response().third.toResult().bind()
                    )
                }.bind()
            ).map { it as House }
        }
        HouseRef.InnerCase.INNER_NOT_SET -> TODO()
        null -> TODO()
    }
}