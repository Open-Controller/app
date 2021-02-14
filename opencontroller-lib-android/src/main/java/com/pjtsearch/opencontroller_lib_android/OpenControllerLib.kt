package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.pjtsearch.opencontroller_lib_proto.*
import kotlin.concurrent.thread

import org.luaj.vm2.lib.jse.JsePlatform

fun executeAction(actionRef: ActionRefOrBuilder, house: HouseOrBuilder): Any {
    val action = resolveActionRef(actionRef, house)!!
    return when (action.innerCase) {
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
        Action.InnerCase.MACRO_ACTION -> action.macroAction.actionList.forEach {
            thread { executeAction(it, house) }
        }
        Action.InnerCase.INNER_NOT_SET -> TODO()
        null -> TODO()
    }
}

private fun resolveActionRef(actionRef: ActionRefOrBuilder, house: HouseOrBuilder): ActionOrBuilder? =
    house.deviceList
        .find { it.id == actionRef.device }
        ?.actionList
        ?.find { it.id == actionRef.action }

fun subscribeDynamicValue(dynamicValueRef: DynamicValueRefOrBuilder, house: HouseOrBuilder, cb: (Any) -> Unit): () -> Unit {
    val dynamicValue = resolveDynamicValueRef(dynamicValueRef, house)!!
    var running = true
    val globals = JsePlatform.standardGlobals()
    val chunk = globals.load(dynamicValue.script)
    dynamicValue.dynamicResourceList.forEach {
        when (it.innerCase) {
            DynamicResource.InnerCase.DATE_RESOURCE -> thread {
                while (running) {
                    Thread.sleep(100)
                    globals.set("date", System.currentTimeMillis().toInt() * -1)
                    cb(chunk.call().toString())
                }
            }
            DynamicResource.InnerCase.INNER_NOT_SET -> TODO()
        }
    }
    return { running = false }
}

private fun resolveDynamicValueRef(dynamicValueRef: DynamicValueRefOrBuilder, house: HouseOrBuilder): DynamicValueOrBuilder? =
    house.deviceList
        .find { it.id == dynamicValueRef.device }
        ?.dynamicValueList
        ?.find { it.id == dynamicValueRef.dynamicValue }