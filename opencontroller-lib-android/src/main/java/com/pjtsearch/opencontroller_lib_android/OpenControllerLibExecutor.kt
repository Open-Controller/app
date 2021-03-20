package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.get
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.*
import kotlin.concurrent.thread

import org.luaj.vm2.lib.jse.JsePlatform
import java.net.Socket

class OpenControllerLibExecutor(val house: HouseOrBuilder) {
    fun executeAction(action: ActionOrBuilder, args: List<Any?>): Result<List<Any?>, Throwable> = runCatching{
        when (action.innerCase) {
            Action.InnerCase.HTTP_ACTION -> {
                val url = when (action.httpAction.urlInnerCase) {
                    HttpAction.UrlInnerCase.URL -> action.httpAction.url
                    HttpAction.UrlInnerCase.URL_SCRIPT ->
                        executeAction(action.httpAction.urlScript, args).unwrap()[0] as String
                    HttpAction.UrlInnerCase.URLINNER_NOT_SET -> TODO()
                }
                when (action.httpAction.method) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                    HttpMethod.UNRECOGNIZED -> TODO()
                }
            }
            Action.InnerCase.TCP_ACTION -> {
                val (host, port) = action.tcpAction.address.split(":")
                val client = Socket(host, port.toInt())
                val command = when (action.tcpAction.commandInnerCase) {
                    TCPAction.CommandInnerCase.COMMAND ->
                        action.tcpAction.command
                    TCPAction.CommandInnerCase.COMMAND_SCRIPT ->
                        executeAction(action.tcpAction.commandScript, args).unwrap()[0] as String
                    TCPAction.CommandInnerCase.COMMANDINNER_NOT_SET -> TODO()
                }
                client.outputStream.write((command+"\r\n").toByteArray())
//                val scanner = client.getInputStream()
//                println("$host:$port")
//                println(action.tcpAction.command)
//                println(scanner.read())
                Thread.sleep(300)
                client.close()
                listOf()
            }
            Action.InnerCase.MACRO_ACTION -> {
                action.macroAction.actionsList.forEach {
                    executeAction(it, listOf())
                }
                listOf()
            }
            Action.InnerCase.DELAY_ACTION -> {
                Thread.sleep(action.delayAction.time.toLong())
                listOf()
            }
            Action.InnerCase.REF_ACTION ->
                executeAction(house.devicesList
                    .find { it.id == action.refAction.device }
                    ?.actionsList
                    ?.find { it.id == action.refAction.action }!!, args).unwrap()
            Action.InnerCase.GET_ARG_ACTION -> {
                println(action.getArgAction.arg)
                println(action)
//                println(action.argsList)
                listOf(args[action.argsList.indexOf(action.getArgAction.arg)])
            }
            Action.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}