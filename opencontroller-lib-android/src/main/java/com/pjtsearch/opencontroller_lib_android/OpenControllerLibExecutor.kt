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
        var nextArg = 0;
        when (func.innerCase) {
            Func.InnerCase.HTTP -> {
                val url = (if (func.http.hasUrl()) func.http.url else capturedArgs[nextArg++] as String)
                                .replace(" ", "%20")

                println(url)

                when (func.http.method) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                }
            }
            Func.InnerCase.TCP -> {
                val (host, port) = (if (func.tcp.hasAddress()) func.tcp.address else capturedArgs[nextArg++] as String)
                    .split(":")
                val client = Socket(host, port.toInt())
                val command = if (func.tcp.hasCommand()) func.tcp.command else capturedArgs[nextArg++] as String
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
                Thread.sleep(if (func.delay.hasTime()) func.delay.time.toLong() else capturedArgs[nextArg++] as Long)
                listOf()
            }
            Func.InnerCase.REF ->
                executeFunc(house.devicesList
                    .find { it.id == if (func.ref.hasDevice()) func.ref.device else capturedArgs[nextArg++] }
                    ?.funcsList
                    ?.find { it.id == if (func.ref.hasFunc()) func.ref.func else capturedArgs[nextArg++] }!!, capturedArgs
                ).unwrap()

            Func.InnerCase.CONCATENATE ->
                listOf((func.concatenate.stringsList + capturedArgs).reduce { last, curr -> last.toString() + curr })
            Func.InnerCase.PUSH_STACK -> capturedArgs + executeFunc(func.pushStack.func, capturedArgs).unwrap()
            Func.InnerCase.PREPEND_STACK -> executeFunc(func.prependStack.func, capturedArgs).unwrap() + capturedArgs
            Func.InnerCase.STRING -> listOf(func.string.string)
            Func.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}