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
            Func.InnerCase.HTTP_FUNC -> {
                val url = when (func.httpFunc.urlInnerCase) {
                    HttpFunc.UrlInnerCase.URL -> func.httpFunc.url
                    HttpFunc.UrlInnerCase.URL_SCRIPT ->
                        executeFunc(func.httpFunc.urlScript, capturedArgs).unwrap()[0] as String
                    HttpFunc.UrlInnerCase.URLINNER_NOT_SET -> TODO()
                }
                when (func.httpFunc.method) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                    HttpMethod.UNRECOGNIZED -> TODO()
                }
            }
            Func.InnerCase.TCP_FUNC -> {
                val (host, port) = func.tcpFunc.address.split(":")
                val client = Socket(host, port.toInt())
                val command = when (func.tcpFunc.commandInnerCase) {
                    TCPFunc.CommandInnerCase.COMMAND ->
                        func.tcpFunc.command
                    TCPFunc.CommandInnerCase.COMMAND_SCRIPT ->
                        executeFunc(func.tcpFunc.commandScript, capturedArgs).unwrap()[0] as String
                    TCPFunc.CommandInnerCase.COMMANDINNER_NOT_SET -> TODO()
                }
                client.outputStream.write((command+"\r\n").toByteArray())
//                val scanner = client.getInputStream()
//                println("$host:$port")
//                println(func.tcpFunc.command)
//                println(scanner.read())
                Thread.sleep(300)
                client.close()
                listOf()
            }
            Func.InnerCase.MACRO_FUNC -> {
                func.macroFunc.funcsList.forEach {
                    executeFunc(it, listOf())
                }
                listOf()
            }
            Func.InnerCase.PIPE_FUNC ->
                func.pipeFunc.funcsList.fold(capturedArgs) { lastResult, curr ->
                    executeFunc(curr, lastResult).unwrap()
                }
            Func.InnerCase.DELAY_FUNC -> {
                Thread.sleep(func.delayFunc.time.toLong())
                listOf()
            }
            Func.InnerCase.REF_FUNC ->
                executeFunc(house.devicesList
                    .find { it.id == func.refFunc.device }
                    ?.funcsList
                    ?.find { it.id == func.refFunc.func }!!, capturedArgs).unwrap()
//            Func.InnerCase.GET_ARG_FUNC -> {
//                println(func.getArgFunc.arg)
//                println(func)
////                println(func.argsList)
//                listOf(args[func.argsList.indexOf(func.getArgFunc.arg)])
//            }

            Func.InnerCase.CONCATENATE_FUNC -> listOf(capturedArgs.reduce { last, curr -> last.toString() + curr })
            Func.InnerCase.PUSH_STACK_FUNC -> capturedArgs + executeFunc(func.pushStackFunc.func, capturedArgs).unwrap()
            Func.InnerCase.PREPEND_STACK_FUNC -> executeFunc(func.prependStackFunc.func, capturedArgs).unwrap() + capturedArgs
            Func.InnerCase.STRING_FUNC -> listOf(func.stringFunc.string)
            Func.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}