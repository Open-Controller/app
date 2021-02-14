package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.awaitUnit
import com.pjtsearch.opencontroller_lib_proto.*
import kotlin.concurrent.thread

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
    house.deviceList
        .find { it.id == actionRef.device }
        ?.actionList
        ?.find { it.id == actionRef.action }

fun resolveDynamicValueRef(dynamicValueRef: DynamicValueRefOrBuilder, house: HouseOrBuilder): DynamicValueOrBuilder? =
    house.deviceList
        .find { it.id == dynamicValueRef.device }
        ?.dynamicValueList
        ?.find { it.id == dynamicValueRef.dynamicValue }

fun subscribeDynamicValue(dynamicValue: DynamicValueOrBuilder, cb: (Any) -> Unit): () -> Unit {
    var running = true
    dynamicValue.dynamicResourceList.forEach {
        when (it.innerCase) {
            DynamicResource.InnerCase.DATE_RESOURCE -> thread{ while (running) {
                Thread.sleep(100)
                cb(System.currentTimeMillis())
            }}
            DynamicResource.InnerCase.INNER_NOT_SET -> TODO()
        }
    }
    return { running = false }
}