package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.pjtsearch.opencontroller_lib_proto.*
import kotlin.concurrent.thread

import org.luaj.vm2.lib.jse.JsePlatform
import java.net.Socket

class OpenControllerLibExecutor(val house: HouseOrBuilder) {
    fun executeAction(actionRef: ActionRefOrBuilder): Result<Any, Throwable> = runCatching{
        val action = resolveActionRef(actionRef)!!
        when (action.innerCase) {
            Action.InnerCase.HTTP_ACTION -> when (action.httpAction.method) {
                HttpMethod.GET -> action.httpAction.url.httpGet().response().third.get()
                HttpMethod.HEAD -> action.httpAction.url.httpHead().response().third.get()
                HttpMethod.POST -> action.httpAction.url.httpPost().response().third.get()
                HttpMethod.PUT -> action.httpAction.url.httpPut().response().third.get()
                HttpMethod.PATCH -> action.httpAction.url.httpPatch().response().third.get()
                HttpMethod.DELETE -> action.httpAction.url.httpDelete().response().third.get()
                HttpMethod.UNRECOGNIZED -> TODO()
            }
            Action.InnerCase.TCP_ACTION -> {
                val (host, port) = action.tcpAction.address.split(":")
                val client = Socket(host, port.toInt())
                client.outputStream.write(action.tcpAction.command.toByteArray())
                val scanner = client.getInputStream()
                scanner.read()
                client.close()
            }
            Action.InnerCase.MACRO_ACTION -> action.macroAction.actionList.map {
                executeAction(it)
            }
            Action.InnerCase.DELAY_ACTION -> {
                Thread.sleep(action.delayAction.time.toLong())
            }
            Action.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }

    private fun resolveActionRef(
        actionRef: ActionRefOrBuilder,
    ): ActionOrBuilder? =
        house.deviceList
            .find { it.id == actionRef.device }
            ?.actionList
            ?.find { it.id == actionRef.action }

    fun subscribeDynamicValue(
        dynamicValueRef: DynamicValueRefOrBuilder,
        cb: (Any) -> Unit
    ): () -> Unit {
        val dynamicValue = resolveDynamicValueRef(dynamicValueRef)!!
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

    private fun resolveDynamicValueRef(
        dynamicValueRef: DynamicValueRefOrBuilder,
    ): DynamicValueOrBuilder? =
        house.deviceList
            .find { it.id == dynamicValueRef.device }
            ?.dynamicValueList
            ?.find { it.id == dynamicValueRef.dynamicValue }
}