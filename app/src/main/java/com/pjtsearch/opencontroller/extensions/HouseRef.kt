package com.pjtsearch.opencontroller.extensions

import com.github.kittinunf.fuel.httpGet
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller.House
import com.pjtsearch.opencontroller.OpenControllerLibExecutor
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_proto.Expr
import com.pjtsearch.opencontroller_lib_proto.HouseExpr
import com.pjtsearch.opencontroller_lib_proto.Module

fun resolveHouseRef(houseRef: HouseRef): Result<House, Throwable> = runCatching {
    when (houseRef.innerCase) {
        HouseRef.InnerCase.NETWORK_HOUSE_REF ->
            OpenControllerLibExecutor().interpretModule<House>(
                Module.parseFrom(
                    houseRef.networkHouseRef.url.httpGet().response().third.get()
                )
            ).unwrap()!!
        HouseRef.InnerCase.INNER_NOT_SET -> throw Error("House ref not set")
        null -> throw Error("House ref is null")
    }
}