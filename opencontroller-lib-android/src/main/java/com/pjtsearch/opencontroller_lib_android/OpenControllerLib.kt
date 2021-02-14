package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.awaitUnit
import com.pjtsearch.opencontroller_lib_proto.*

fun executeAction(action: ActionOrBuilder): Any =
    when (action.innerCase) {
        Action.InnerCase.HTTP_ACTION -> when (action.httpAction.method) {
            HttpMethod.GET -> action.httpAction.url.httpGet().response()
            HttpMethod.HEAD -> action.httpAction.url.httpHead().response()
            HttpMethod.POST -> action.httpAction.url.httpPost().response()
            HttpMethod.PUT -> action.httpAction.url.httpPut().response()
            HttpMethod.PATCH -> action.httpAction.url.httpPatch().response()
            HttpMethod.DELETE -> action.httpAction.url.httpDelete().response()
            HttpMethod.UNRECOGNIZED -> TODO()
        }
        Action.InnerCase.TCP_ACTION -> TODO()
        Action.InnerCase.INNER_NOT_SET -> TODO()
    }

fun resolveActionRef(actionRef: ActionRefOrBuilder, house: HouseOrBuilder): ActionOrBuilder? =
    house.devicesList
        .find { it.id == actionRef.device }
        ?.actionsList
        ?.find { it.id == actionRef.action }
