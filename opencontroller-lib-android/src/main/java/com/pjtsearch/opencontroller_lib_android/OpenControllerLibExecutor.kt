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
    fun executeFunc(func: FuncOrBuilder, args: List<Any?>): Result<List<Any?>, Throwable> = runCatching {
        if (args.size < func.argsList.size) throw Error("${func.id} Expected ${func.argsList.size} args, but got ${args.size}")
        val capturedArgs = args.subList(0, func.argsList.size)
        when (func.innerCase) {
            Func.InnerCase.HTTP -> {
                val url = when (func.http.urlInnerCase) {
                    HttpFunc.UrlInnerCase.URL -> func.http.url
                    HttpFunc.UrlInnerCase.URL_SCRIPT ->
                        executeFunc(func.http.urlScript, capturedArgs).unwrap()[0] as String
                    HttpFunc.UrlInnerCase.URLINNER_NOT_SET -> TODO()
                }

                when (func.http.method) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                    HttpMethod.UNRECOGNIZED -> TODO()
                }
            }
            Func.InnerCase.TCP -> {
                val (host, port) = func.tcp.address.split(":")
                val client = Socket(host, port.toInt())
                val command = when (func.tcp.commandInnerCase) {
                    TCPFunc.CommandInnerCase.COMMAND ->
                        func.tcp.command
                    TCPFunc.CommandInnerCase.COMMAND_SCRIPT ->
                        executeFunc(func.tcp.commandScript, capturedArgs).unwrap()[0] as String
                    TCPFunc.CommandInnerCase.COMMANDINNER_NOT_SET -> TODO()
                }
                client.outputStream.write((command+"\r\n").toByteArray())
//                val scanner = client.getInputStream()
//                println("$host:$port")
//                println(func.tcp.command)
//                println(scanner.read())
                Thread.sleep(300)
                client.close()
                listOf()
            }
            Func.InnerCase.MACRO -> {
                func.macro.funcsList.forEach {
                    executeFunc(it, listOf())
                }
                listOf()
            }
            Func.InnerCase.PIPE ->
                func.pipe.funcsList.fold(capturedArgs) { lastResult, curr ->
                    executeFunc(curr, lastResult).unwrap()
                }
            Func.InnerCase.DELAY -> {
                Thread.sleep(func.delay.time.toLong())
                listOf()
            }
            Func.InnerCase.REF ->
                executeFunc(house.devicesList
                    .find { it.id == func.ref.device }
                    ?.funcsList
                    ?.find { it.id == func.ref.func }!!, capturedArgs).unwrap()
//            Func.InnerCase.GET_ARG -> {
//                println(func.getArgFunc.arg)
//                println(func)
////                println(func.argsList)
//                listOf(args[func.argsList.indexOf(func.getArgFunc.arg)])
//            }

            Func.InnerCase.CONCATENATE -> listOf(capturedArgs.reduce { last, curr -> last.toString() + curr })
            Func.InnerCase.PUSH_STACK -> capturedArgs + executeFunc(func.pushStack.func, capturedArgs).unwrap()
            Func.InnerCase.PREPEND_STACK -> executeFunc(func.prependStack.func, capturedArgs).unwrap() + capturedArgs
            Func.InnerCase.STRING -> listOf(func.string.string)
            Func.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}