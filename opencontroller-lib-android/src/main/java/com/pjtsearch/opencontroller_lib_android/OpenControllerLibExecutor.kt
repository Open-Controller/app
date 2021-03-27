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
        val availableArgs = ArrayDeque(capturedArgs);
        when (lambda.innerCase) {
            Lambda.InnerCase.HTTP -> with(lambda.http){
                val url = (if (this.hasUrl()) this.url else availableArgs.removeFirst() as String)
                                .replace(" ", "%20")

                println(url)

                when (if (this.hasMethod()) this.method else availableArgs.removeFirst() as HttpMethod) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                }
            }
            Lambda.InnerCase.TCP -> with(lambda.tcp){
                val (host, port) = (if (this.hasAddress()) this.address else availableArgs.removeFirst() as String)
                    .split(":")
                val client = Socket(host, port.toInt())
                val command = if (this.hasCommand()) this.command else availableArgs.removeFirst() as String
                client.outputStream.write((command+"\r\n").toByteArray())
//                val scanner = client.getInputStream()
//                println("$host:$port")
//                println(lambda.tcp.command)
//                println(scanner.read())
                Thread.sleep(300)
                client.close()
                listOf()
            }
            Lambda.InnerCase.MACRO -> with(lambda.macro){
                this.lambdasList.forEach {
                    executeFunc(it, listOf())
                }
                listOf()
            }
            Lambda.InnerCase.FOLD_ARGS ->
                lambda.foldArgs.lambdasList.fold(capturedArgs) { lastResult, curr ->
                    executeFunc(curr, lastResult).unwrap()
                }
            Lambda.InnerCase.DELAY -> with(lambda.delay){
                Thread.sleep(if (this.hasTime()) this.time.toLong() else availableArgs.removeFirst() as Long)
                listOf()
            }
            Lambda.InnerCase.REF -> with(lambda.ref) {
                executeFunc(house.devicesList
                    .find { it.id == if (this.hasDevice()) this.device else availableArgs.removeFirst() }
                    ?.lambdasList
                    ?.find { it.id == if (this.hasLambda()) this.lambda else availableArgs.removeFirst() }!!,
                    capturedArgs
                ).unwrap()
            }

            Lambda.InnerCase.CONCATENATE ->
                listOf((lambda.concatenate.stringsList + capturedArgs).reduce { last, curr -> last.toString() + curr })
            Lambda.InnerCase.PUSH_STACK -> with(lambda.pushStack) {
                val newItem = if (this.hasLambda()) this.lambda else availableArgs.removeFirst() as Lambda
                capturedArgs + executeFunc(newItem, capturedArgs).unwrap()
            }
            Lambda.InnerCase.PREPEND_STACK -> with(lambda.prependStack) {
                val newItem = if (this.hasLambda()) this.lambda else availableArgs.removeFirst() as Lambda
                executeFunc(newItem, capturedArgs).unwrap() + capturedArgs
            }
            Lambda.InnerCase.STRING -> listOf(lambda.string)
            Lambda.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}