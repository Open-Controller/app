package com.pjtsearch.opencontroller_lib_android

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.Expr
import com.pjtsearch.opencontroller_lib_proto.ExprOrBuilder
import com.pjtsearch.opencontroller_lib_proto.LambdaExpr
import java.io.Serializable
import java.net.Socket


data class House(val id: String, val displayName: String, val rooms: List<Room>)
data class Room(val id: String, val displayName: String, val icon: String, val controllers: List<Controller>)
data class Controller(val id: String, val displayName: String, val brandColor: String, val displayInterface: DisplayInterface)
data class DisplayInterface(val widgets: List<Widget>)

typealias NativeFn = (List<Any?>) -> Any

class OpenControllerLibExecutor(
    private var sockets: Map<String, Socket> = hashMapOf()
) : Serializable {
    fun interpretExpr(
        expr: ExprOrBuilder,
        localScope: Map<String, Any?>,
        globalScope: Map<String, Any?>
    ): Result<Any?, Throwable> =
        runCatching {
            when (expr.innerCase) {
                Expr.InnerCase.REF -> expr.ref.let {
                    localScope[it.ref] ?: globalScope[it.ref]
                }
                Expr.InnerCase.WIDGET -> expr.widget.let {
                    null
                }
                Expr.InnerCase.LAMBDA -> expr.lambda
                Expr.InnerCase.CALL -> expr.call.let {
                    when (val calling = interpretExpr(it.calling, localScope, globalScope).unwrap()) {
                        is LambdaExpr -> interpretExpr(
                            calling.`return`,
                            mapOf(*calling.argsList.mapIndexed { i, arg ->
                                Pair(
                                    arg,
                                    interpretExpr(it.getArgs(i), localScope, globalScope).unwrap()
                                )
                            }.toTypedArray()),
                            globalScope
                        ).unwrap()
                        is Function<*> -> (calling as NativeFn)(it.argsList.map { arg ->
                            interpretExpr(
                                arg,
                                localScope,
                                globalScope
                            ).unwrap()
                        })
                        else -> throw Error("Invalid call $calling")
                    }
                }
                Expr.InnerCase.STRING -> expr.string
                Expr.InnerCase.INT64 -> expr.int64
                Expr.InnerCase.INT32 -> expr.int32
                Expr.InnerCase.FLOAT -> expr.float
                Expr.InnerCase.BOOL -> expr.bool
                Expr.InnerCase.HOUSE -> House()
                Expr.InnerCase.ROOM -> TODO()
                Expr.InnerCase.CONTROLLER -> TODO()
                Expr.InnerCase.DISPLAY_INTERFACE -> TODO()
                Expr.InnerCase.DEVICE -> TODO()
                Expr.InnerCase.INNER_NOT_SET -> TODO()
                null -> TODO()
            }
        }
//        if (args.size < lambda.argsList.size) throw Error("${lambda.id} Expected ${lambda.argsList.size} args, but got ${args.size}")
//        val capturedArgs = args.subList(0, lambda.argsList.size)
//        val availableArgs = ArrayDeque(capturedArgs)
////        println(lambda.innerCase)
//        when (lambda.innerCase) {
//            Lambda.InnerCase.HTTP -> lambda.http.let {
//                val url = (if (it.hasUrl()) it.url else availableArgs.removeFirst() as String)
//                println(url)
//
//                when (if (it.hasMethod()) it.method else availableArgs.removeFirst() as HttpMethod) {
//                    HttpMethod.GET -> listOf(url.httpGet().response().third.get())
//                    HttpMethod.HEAD -> listOf(url.httpHead().response().third.get())
//                    HttpMethod.POST -> listOf(url.httpPost().response().third.get())
//                    HttpMethod.PUT -> listOf(url.httpPut().response().third.get())
//                    HttpMethod.PATCH -> listOf(url.httpPatch().response().third.get())
//                    HttpMethod.DELETE -> listOf(url.httpDelete().response().third.get())
//                    null -> TODO()
//                }
//            }
//            Lambda.InnerCase.TCP -> lambda.tcp.let {
//                val (host, port) = (if (it.hasAddress()) it.address else availableArgs.removeFirst() as String)
//                    .split(":")
//                var client:Socket? = null
//                try {
//                    client = sockets["$host:$port"] ?: Socket(host, port.toInt()).let { s ->
//                        sockets = sockets + ("$host:$port" to s)
//                        s
//                    }
//                } catch (err: Exception) {
//                    err.printStackTrace()
//                }
//                val command = if (it.hasCommand()) it.command else availableArgs.removeFirst() as String
//                client?.outputStream?.write((command+"\r\n").toByteArray())
////                val scanner = client.getInputStream()
////                println("$host:$port")
////                println(lambda.tcp.command)
////                println(scanner.read())
//                Thread.sleep(300)
//                client?.close()
//                sockets = sockets - "$host:$port"
//                listOf()
//            }
//            Lambda.InnerCase.MACRO -> lambda.macro.let {
//                it.lambdasList.forEach { l ->
//                    executeLambda(l, listOf())
//                }
//                listOf()
//            }
//            Lambda.InnerCase.PIPE_ARGS ->
//                lambda.pipeArgs.lambdasList.fold(capturedArgs) { lastResult, curr ->
//                    executeLambda(curr, lastResult).unwrap()
//                }
//            Lambda.InnerCase.DELAY -> lambda.delay.let {
//                Thread.sleep(if (it.hasTime()) it.time.toLong() else availableArgs.removeFirst() as Long)
//                listOf()
//            }
//            Lambda.InnerCase.REF -> lambda.ref.let { ref ->
//                executeLambda(house.devicesList
//                    .find { it.id == if (ref.hasDevice()) ref.device else availableArgs.removeFirst() }
//                    ?.lambdasList
//                    ?.find { it.id == if (ref.hasLambda()) ref.lambda else availableArgs.removeFirst() }!!,
//                    capturedArgs
//                ).unwrap()
//            }
//
//            Lambda.InnerCase.CONCATENATE ->
//                listOf((lambda.concatenate.stringsList + capturedArgs).reduce { last, curr -> last.toString() + curr })
//            Lambda.InnerCase.PUSH_STACK -> lambda.pushStack.let {
//                val newItem = if (it.hasLambda()) it.lambda else availableArgs.removeFirst() as Lambda
//                capturedArgs + executeLambda(newItem, capturedArgs).unwrap()
//            }
//            Lambda.InnerCase.PREPEND_STACK -> lambda.prependStack.let {
//                val newItem = if (it.hasLambda()) it.lambda else availableArgs.removeFirst() as Lambda
//                executeLambda(newItem, capturedArgs).unwrap() + capturedArgs
//            }
//            Lambda.InnerCase.STRING -> listOf(lambda.string)
//            Lambda.InnerCase.SWITCH -> lambda.switch.let {
//                val then = it.conditionsList.firstOrNull { condition ->
//                    executeLambda(condition.`if`, capturedArgs).unwrap()[0] as Boolean
//                }?.then ?: it.`else`
//                executeLambda(then, capturedArgs).unwrap()
//            }
//            Lambda.InnerCase.IS_EQUAL -> lambda.isEqual.let {
//                listOf(when (it.fromCase) {
//                    IsEqualFunc.FromCase.FROM_BOOL -> it.fromBool == if (it.hasToBool()) it.toBool else availableArgs.removeFirst()
//                    IsEqualFunc.FromCase.FROM_STRING -> it.fromString.equals(if (it.hasToString()) it.toString else availableArgs.removeFirst())
//                    IsEqualFunc.FromCase.FROM_FLOAT -> it.fromFloat == if (it.hasToFloat()) it.toFloat else availableArgs.removeFirst()
//                    IsEqualFunc.FromCase.FROM_INT64 -> it.fromInt64 == if (it.hasToInt64()) it.toInt64 else availableArgs.removeFirst()
//                    IsEqualFunc.FromCase.FROM_INT32 -> it.fromInt32 == if (it.hasToInt32()) it.toInt32 else availableArgs.removeFirst()
//                    IsEqualFunc.FromCase.FROM_NOT_SET, null -> {
//                        val arg = availableArgs.removeFirst()
//                        when (it.toCase) {
//                            IsEqualFunc.ToCase.TO_BOOL -> arg as Boolean == it.toBool
//                            IsEqualFunc.ToCase.TO_STRING -> arg as String == it.toString
//                            IsEqualFunc.ToCase.TO_FLOAT -> arg as Float == it.toFloat
//                            IsEqualFunc.ToCase.TO_INT64 -> arg as Long == it.toInt64
//                            IsEqualFunc.ToCase.TO_INT32 -> arg as Int == it.toInt32
//                            IsEqualFunc.ToCase.TO_NOT_SET, null -> {
//                                arg?.equals(availableArgs.removeFirst())
//                            }
//                        }
//                    }
//                })
//            }
//            Lambda.InnerCase.GET_PROP -> lambda.getProp.let {
//                val target = availableArgs.removeFirst()
//                val prop = if (it.hasProp()) it.prop else availableArgs.removeFirst() as String
//                when (target) {
//                    is Message -> {
//                        val descriptor = target.descriptorForType.findFieldByName(prop)
//                        listOf(target.getField(descriptor))
//                    }
//                    is Map<*, *> -> {
//                        listOf(target[prop])
//                    }
//                    else -> TODO()
//                }
//            }
//            Lambda.InnerCase.INT32 -> listOf(lambda.int32)
//            Lambda.InnerCase.INT64 -> listOf(lambda.int64)
//            Lambda.InnerCase.FLOAT -> listOf(lambda.float)
//            Lambda.InnerCase.GET_INDEX -> lambda.getIndex.let {
//                when (val target = availableArgs.removeFirst()) {
//                    is List<*> -> {
//                        val index = if (it.hasIndex()) it.index else availableArgs.removeFirst() as Int
//                        listOf(target[index])
//                    }
//                    else -> TODO()
//                }
//            }
//            Lambda.InnerCase.INNER_NOT_SET -> TODO()
//            null -> TODO()
//        }
}