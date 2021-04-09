package com.pjtsearch.opencontroller_lib_android

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.unwrap
import com.google.protobuf.Message
import com.pjtsearch.opencontroller_lib_proto.*
import java.io.Serializable
import java.lang.Exception

import java.net.Socket

class OpenControllerLibExecutor(private val house: HouseOrBuilder,
                                private var sockets: Map<String, Socket> = hashMapOf()) : Serializable {
    fun executeLambda(lambda: LambdaOrBuilder, args: List<Any?>): Result<List<Any?>, Throwable> = runCatching {
        if (args.size < lambda.argsList.size) throw Error("${lambda.id} Expected ${lambda.argsList.size} args, but got ${args.size}")
        val capturedArgs = args.subList(0, lambda.argsList.size)
        val availableArgs = ArrayDeque(capturedArgs);
//        println(lambda.innerCase)
        when (lambda.innerCase) {
            Lambda.InnerCase.HTTP -> lambda.http.let {
                val url = (if (it.hasUrl()) it.url else availableArgs.removeFirst() as String)
                println(url)

                when (if (it.hasMethod()) it.method else availableArgs.removeFirst() as HttpMethod) {
                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
                }
            }
            Lambda.InnerCase.TCP -> lambda.tcp.let {
                val (host, port) = (if (it.hasAddress()) it.address else availableArgs.removeFirst() as String)
                    .split(":")
                var client:Socket? = null;
                try {
                    client = sockets["$host:$port"] ?: Socket(host, port.toInt()).let { s ->
                        sockets = sockets + ("$host:$port" to s)
                        s
                    }
                } catch (err: Exception) {
                    err.printStackTrace()
                }
                val command = if (it.hasCommand()) it.command else availableArgs.removeFirst() as String
                client?.outputStream?.write((command+"\r\n").toByteArray())
//                val scanner = client.getInputStream()
//                println("$host:$port")
//                println(lambda.tcp.command)
//                println(scanner.read())
                Thread.sleep(300)
                client?.close()
                sockets = sockets - "$host:$port"
                listOf()
            }
            Lambda.InnerCase.MACRO -> lambda.macro.let {
                it.lambdasList.forEach { l ->
                    executeLambda(l, listOf())
                }
                listOf()
            }
            Lambda.InnerCase.PIPE_ARGS ->
                lambda.pipeArgs.lambdasList.fold(capturedArgs) { lastResult, curr ->
                    executeLambda(curr, lastResult).unwrap()
                }
            Lambda.InnerCase.DELAY -> lambda.delay.let {
                Thread.sleep(if (it.hasTime()) it.time.toLong() else availableArgs.removeFirst() as Long)
                listOf()
            }
            Lambda.InnerCase.REF -> lambda.ref.let { ref ->
                executeLambda(house.devicesList
                    .find { it.id == if (ref.hasDevice()) ref.device else availableArgs.removeFirst() }
                    ?.lambdasList
                    ?.find { it.id == if (ref.hasLambda()) ref.lambda else availableArgs.removeFirst() }!!,
                    capturedArgs
                ).unwrap()
            }

            Lambda.InnerCase.CONCATENATE ->
                listOf((lambda.concatenate.stringsList + capturedArgs).reduce { last, curr -> last.toString() + curr })
            Lambda.InnerCase.PUSH_STACK -> lambda.pushStack.let {
                val newItem = if (it.hasLambda()) it.lambda else availableArgs.removeFirst() as Lambda
                capturedArgs + executeLambda(newItem, capturedArgs).unwrap()
            }
            Lambda.InnerCase.PREPEND_STACK -> lambda.prependStack.let {
                val newItem = if (it.hasLambda()) it.lambda else availableArgs.removeFirst() as Lambda
                executeLambda(newItem, capturedArgs).unwrap() + capturedArgs
            }
            Lambda.InnerCase.STRING -> listOf(lambda.string)
            Lambda.InnerCase.SWITCH -> lambda.switch.let {
                val then = it.conditionsList.firstOrNull {
                    executeLambda(it.`if`, capturedArgs).unwrap()[0] as Boolean
                }?.then ?: it.`else`
                executeLambda(then, capturedArgs).unwrap()
            }
            Lambda.InnerCase.IS_EQUAL -> lambda.isEqual.let {
                listOf(when (it.fromCase) {
                    IsEqualFunc.FromCase.FROM_BOOL -> it.fromBool == if (it.hasToBool()) it.toBool else availableArgs.removeFirst()
                    IsEqualFunc.FromCase.FROM_STRING -> it.fromString.equals(if (it.hasToString()) it.toString else availableArgs.removeFirst())
                    IsEqualFunc.FromCase.FROM_FLOAT -> it.fromFloat == if (it.hasToFloat()) it.toFloat else availableArgs.removeFirst()
                    IsEqualFunc.FromCase.FROM_INT64 -> it.fromInt64 == if (it.hasToInt64()) it.toInt64 else availableArgs.removeFirst()
                    IsEqualFunc.FromCase.FROM_INT32 -> it.fromInt32 == if (it.hasToInt32()) it.toInt32 else availableArgs.removeFirst()
                    IsEqualFunc.FromCase.FROM_NOT_SET -> {
                        val arg = availableArgs.removeFirst()
                        when (it.toCase) {
                            IsEqualFunc.ToCase.TO_BOOL -> arg as Boolean == it.toBool
                            IsEqualFunc.ToCase.TO_STRING -> (arg as String).equals(it.toString)
                            IsEqualFunc.ToCase.TO_FLOAT -> arg as Float == it.toFloat
                            IsEqualFunc.ToCase.TO_INT64 -> arg as Long == it.toInt64
                            IsEqualFunc.ToCase.TO_INT32 -> arg as Int == it.toInt32
                            IsEqualFunc.ToCase.TO_NOT_SET -> {
                                arg?.equals(availableArgs.removeFirst())
                            }
                        }
                    }
                })
            }
            Lambda.InnerCase.GET_PROP -> lambda.getProp.let {
                when (val target = availableArgs.removeFirst()) {
                    is Message -> {
                        val descriptor = target.descriptorForType.findFieldByName(it.prop)
                        listOf(target.getField(descriptor))
                    }
                    is Map<*, *> -> {
                        listOf(target[it.prop])
                    }
                    else -> TODO()
                }
            }
            Lambda.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }
    }
}