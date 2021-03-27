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
    fun executeFunc(lambda: LambdaOrBuilder, args: List<Any?>): Result<List<Any?>, Throwable> = runCatching {
        if (args.size < lambda.argsList.size) throw Error("${lambda.id} Expected ${lambda.argsList.size} args, but got ${args.size}")
        val capturedArgs = args.subList(0, lambda.argsList.size)
        var nextArg = 0;
        when (lambda.innerCase) {
            Lambda.InnerCase.HTTP -> {
                val url = (if (lambda.http.hasUrl()) lambda.http.url else capturedArgs[nextArg++] as String)
                                .replace(" ", "%20")

                println(url)

                when (if (lambda.http.hasMethod()) lambda.http.method else capturedArgs[nextArg++] as HttpMethod) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                }
            }
            Lambda.InnerCase.TCP -> {
                val (host, port) = (if (lambda.tcp.hasAddress()) lambda.tcp.address else capturedArgs[nextArg++] as String)
                    .split(":")
                val client = Socket(host, port.toInt())
                val command = if (lambda.tcp.hasCommand()) lambda.tcp.command else capturedArgs[nextArg++] as String
                client.outputStream.write((command+"\r\n").toByteArray())
//                val scanner = client.getInputStream()
//                println("$host:$port")
//                println(lambda.tcp.command)
//                println(scanner.read())
                Thread.sleep(300)
                client.close()
                listOf()
            }
            Lambda.InnerCase.MACRO -> {
                lambda.macro.lambdasList.forEach {
                    executeFunc(it, listOf())
                }
                listOf()
            }
            Lambda.InnerCase.FOLD_ARGS ->
                lambda.foldArgs.lambdasList.fold(capturedArgs) { lastResult, curr ->
                    executeFunc(curr, lastResult).unwrap()
                }
            Lambda.InnerCase.DELAY -> {
                Thread.sleep(if (lambda.delay.hasTime()) lambda.delay.time.toLong() else capturedArgs[nextArg++] as Long)
                listOf()
            }
            Lambda.InnerCase.REF ->
                executeFunc(house.devicesList
                    .find { it.id == if (lambda.ref.hasDevice()) lambda.ref.device else capturedArgs[nextArg++] }
                    ?.lambdasList
                    ?.find { it.id == if (lambda.ref.hasLambda()) lambda.ref.lambda else capturedArgs[nextArg++] }!!, capturedArgs
                ).unwrap()

            Lambda.InnerCase.CONCATENATE ->
                listOf((lambda.concatenate.stringsList + capturedArgs).reduce { last, curr -> last.toString() + curr })
            Lambda.InnerCase.PUSH_STACK -> {
                val newItem = if (lambda.pushStack.hasLambda()) lambda.pushStack.lambda else capturedArgs[nextArg++] as Lambda
                capturedArgs + executeFunc(newItem, capturedArgs).unwrap()
            }
            Lambda.InnerCase.PREPEND_STACK -> {
                val newItem = if (lambda.prependStack.hasLambda()) lambda.prependStack.lambda else capturedArgs[nextArg++] as Lambda
                executeFunc(newItem, capturedArgs).unwrap() + capturedArgs
            }
            Lambda.InnerCase.STRING -> listOf(lambda.string)
            Lambda.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}