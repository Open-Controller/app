package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.unwrap
import com.google.protobuf.Message
import com.pjtsearch.opencontroller_lib_proto.*

import java.net.Socket

class OpenControllerLibExecutor(private val house: HouseOrBuilder) {
    fun executeLambda(lambda: LambdaOrBuilder, args: List<Any?>): Result<List<Any?>, Throwable> = runCatching {
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
                    executeLambda(it, listOf())
                }
                listOf()
            }
            Lambda.InnerCase.FOLD_ARGS ->
                lambda.foldArgs.lambdasList.fold(capturedArgs) { lastResult, curr ->
                    executeLambda(curr, lastResult).unwrap()
                }
            Lambda.InnerCase.DELAY -> with(lambda.delay){
                Thread.sleep(if (this.hasTime()) this.time.toLong() else availableArgs.removeFirst() as Long)
                listOf()
            }
            Lambda.InnerCase.REF -> with(lambda.ref) {
                executeLambda(house.devicesList
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
                capturedArgs + executeLambda(newItem, capturedArgs).unwrap()
            }
            Lambda.InnerCase.PREPEND_STACK -> with(lambda.prependStack) {
                val newItem = if (this.hasLambda()) this.lambda else availableArgs.removeFirst() as Lambda
                executeLambda(newItem, capturedArgs).unwrap() + capturedArgs
            }
            Lambda.InnerCase.STRING -> listOf(lambda.string)
            Lambda.InnerCase.SWITCH -> with(lambda.switch) {
                val then = this.conditionsList.first {
                    executeLambda(it.`if`, capturedArgs).unwrap()[0] as Boolean
                }.then ?: this.`else`
                executeLambda(then, capturedArgs).unwrap()
            }
            Lambda.InnerCase.IS_EQUAL -> with(lambda.isEqual) {
                listOf(when (this.fromCase) {
                    IsEqualFunc.FromCase.FROM_BOOL -> this.fromBool == this.toBool
                    IsEqualFunc.FromCase.FROM_STRING -> this.fromString.equals(this.toString)
                    IsEqualFunc.FromCase.FROM_FLOAT -> this.fromFloat == this.toFloat
                    IsEqualFunc.FromCase.FROM_INT64 -> this.fromInt64 == this.toInt64
                    IsEqualFunc.FromCase.FROM_INT32 -> this.fromInt32 == this.toInt32
                    IsEqualFunc.FromCase.FROM_NOT_SET -> {
                        when (this.toCase) {
                            IsEqualFunc.ToCase.TO_BOOL -> availableArgs.removeFirst() as Boolean == this.toBool
                            IsEqualFunc.ToCase.TO_STRING -> (availableArgs.removeFirst() as String).equals(this.toString)
                            IsEqualFunc.ToCase.TO_FLOAT -> availableArgs.removeFirst() as Float == this.toFloat
                            IsEqualFunc.ToCase.TO_INT64 -> availableArgs.removeFirst() as Long == this.toInt64
                            IsEqualFunc.ToCase.TO_INT32 -> availableArgs.removeFirst() as Int == this.toInt32
                            IsEqualFunc.ToCase.TO_NOT_SET -> {
                                availableArgs.removeFirst()?.equals(availableArgs.removeFirst())
                            }
                        }
                    }
                })
            }
            Lambda.InnerCase.GET_PROP -> with(lambda.getProp){
                when (val target = availableArgs.removeFirst()) {
                    is Message -> {
                        val descriptor = target.descriptorForType.findFieldByName(this.prop)
                        listOf(target.getField(descriptor))
                    }
                    is Map<*, *> -> {
                        listOf(target[this.prop])
                    }
                    else -> TODO()
                }
            }
            Lambda.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}