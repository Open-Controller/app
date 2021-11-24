package com.pjtsearch.opencontroller

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.Expr
import com.pjtsearch.opencontroller_lib_proto.ExprOrBuilder
import com.pjtsearch.opencontroller_lib_proto.ModuleOrBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.Serializable
import java.net.Socket
import java.util.*
import kotlin.reflect.KMutableProperty0


data class House(
    val id: String,
    val displayName: String,
    val rooms: Map<String, Room>,
)

data class Room(
    val displayName: String,
    val icon: String,
    val controllers: Map<String, Controller>
)

data class Controller(
    val displayName: String,
    val brandColor: String?,
    val displayInterface: DisplayInterface?
)

data class DisplayInterface(
    val widgets: List<Widget>
)

data class Device(
    val lambdas: Map<String, Fn>
)

data class Widget(
    val widgetType: String,
    val params: Map<String, Any?>,
    val children: List<Widget>
)

typealias Fn = (List<Any?>) -> Any?

class OpenControllerLibExecutor(
    private var sockets: Map<String, Socket> = hashMapOf()
) : Serializable {
    private val builtins: Map<String, Fn> = mapOf(
        "+" to { args: List<Any?> ->
            val first = args[0]
            val second = args[1]
            if (first is String) {
                if (second is String || second is Int || second is Float || second is Long || second is Double) {
                    first + second
                } else {
                    TODO()
                }
            } else if (first is Int) {
                when (second) {
                    is Int -> first + second
                    is Float -> first + second
                    is Long -> first + second
                    is Double -> first + second
                    is String -> first.toString() + second
                    else -> TODO()
                }
            } else if (first is Float) {
                when (second) {
                    is Int -> first + second
                    is Float -> first + second
                    is Long -> first + second
                    is Double -> first + second
                    is String -> first.toString() + second
                    else -> TODO()
                }
            } else if (first is Long) {
                when (second) {
                    is Int -> first + second
                    is Float -> first + second
                    is Long -> first + second
                    is Double -> first + second
                    is String -> first.toString() + second
                    else -> TODO()
                }
            } else if (first is Double) {
                when (second) {
                    is Int -> first + second
                    is Float -> first + second
                    is Long -> first + second
                    is Double -> first + second
                    is String -> first.toString() + second
                    else -> TODO()
                }
            } else {
                TODO()
            }
        },
        "-" to { args: List<Any?> ->
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> first - second
                        is Float -> first - second
                        is Long -> first - second
                        is Double -> first - second
                        else -> TODO()
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> first - second
                        is Float -> first - second
                        is Long -> first - second
                        is Double -> first - second
                        else -> TODO()
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> first - second
                        is Float -> first - second
                        is Long -> first - second
                        is Double -> first - second
                        else -> TODO()
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> first - second
                        is Float -> first - second
                        is Long -> first - second
                        is Double -> first - second
                        else -> TODO()
                    }
                }
                else -> {
                    TODO()
                }
            }
        },
        "*" to { args: List<Any?> ->
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> first * second
                        is Float -> first * second
                        is Long -> first * second
                        is Double -> first * second
                        else -> TODO()
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> first * second
                        is Float -> first * second
                        is Long -> first * second
                        is Double -> first * second
                        else -> TODO()
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> first * second
                        is Float -> first * second
                        is Long -> first * second
                        is Double -> first * second
                        else -> TODO()
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> first * second
                        is Float -> first * second
                        is Long -> first * second
                        is Double -> first * second
                        else -> TODO()
                    }
                }
                else -> {
                    TODO()
                }
            }
        },
        "/" to { args: List<Any?> ->
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> first / second
                        is Float -> first / second
                        is Long -> first / second
                        is Double -> first / second
                        else -> TODO()
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> first / second
                        is Float -> first / second
                        is Long -> first / second
                        is Double -> first / second
                        else -> TODO()
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> first / second
                        is Float -> first / second
                        is Long -> first / second
                        is Double -> first / second
                        else -> TODO()
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> first / second
                        is Float -> first / second
                        is Long -> first / second
                        is Double -> first / second
                        else -> TODO()
                    }
                }
                else -> {
                    TODO()
                }
            }
        },
        "%" to { args: List<Any?> ->
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> first % second
                        is Float -> first % second
                        is Long -> first % second
                        is Double -> first % second
                        else -> TODO()
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> first % second
                        is Float -> first % second
                        is Long -> first % second
                        is Double -> first % second
                        else -> TODO()
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> first % second
                        is Float -> first % second
                        is Long -> first % second
                        is Double -> first % second
                        else -> TODO()
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> first % second
                        is Float -> first % second
                        is Long -> first % second
                        is Double -> first % second
                        else -> TODO()
                    }
                }
                else -> {
                    TODO()
                }
            }
        },
        "getLambda" to { args: List<Any?> ->
            val device = args[0] as Device
            val lambda = args[1] as String
            device.lambdas.get(lambda)
        },
        "pipe" to { args: List<Any?> ->
            val value = args[0]
            args.drop(1).fold(value) { lastResult, curr ->
                (curr as Fn)(listOf(lastResult))
            }
        },
        "map" to { args: List<Any?> ->
            val transformer = args[0] as Fn
            { innerArgs: List<Any?> ->
                val input = innerArgs[0] as Flow<*>
                input.map {
                    transformer(listOf(it))
                }
            }
        }
    )

    private val stdModule = Device(mapOf(
        "httpRequest" to { args: List<Any?> ->
            val url = args[0] as String
            when (args[1] as String) {
                "GET" -> url.httpGet().response().third.get().decodeToString()
                "HEAD" -> url.httpHead().response().third.get().decodeToString()
                "POST" -> url.httpPost().response().third.get().decodeToString()
                "PUT" -> url.httpPut().response().third.get().decodeToString()
                "PATCH" -> url.httpPatch().response().third.get().decodeToString()
                "DELETE" -> url.httpDelete().response().third.get().decodeToString()
                else -> TODO()
            }
        },
        "observeTime" to { args: List<Any?> ->
            val interval = args[0] as Number
            flow {
                while (true) {
                    emit(Date().time)
                    delay(interval.toLong())
                }
            }
        },
    ))

    fun <T> interpretExpr(
        expr: ExprOrBuilder,
        moduleScope: Map<String, Any?>,
        localScope: Map<String, Any?>,
    ): Result<T?, Throwable> =
        runCatching {
            when (expr.innerCase) {
                Expr.InnerCase.REF -> expr.ref.let {
                    localScope[it.ref] as T ?:
                    builtins[it.ref] as T ?:
                    moduleScope[it.ref] as T
                }
                Expr.InnerCase.LAMBDA -> expr.lambda.let {
                    { args: List<Any?> ->
                        interpretExpr<T>(
                            it.`return`,
                            moduleScope,
                            localScope + mapOf(*it.argsList.mapIndexed { i, arg ->
                                arg to args[i]
                            }.toTypedArray()),
                        ).unwrap()
                    } as T
                }
                Expr.InnerCase.CALL -> expr.call.let {
                    interpretExpr<Fn>(
                        it.calling,
                        moduleScope,
                        localScope,
                    ).unwrap()!!(
                        it.argsList.map { arg ->
                            interpretExpr<Any>(
                                arg,
                                moduleScope,
                                localScope,
                            ).unwrap()
                        },
                    ) as T
                }
                Expr.InnerCase.STRING -> expr.string as T
                Expr.InnerCase.INT64 -> expr.int64 as T
                Expr.InnerCase.INT32 -> expr.int32 as T
                Expr.InnerCase.FLOAT -> expr.float as T
                Expr.InnerCase.BOOL -> expr.bool as T
                Expr.InnerCase.HOUSE -> expr.house.let {
                    House(
//                      Evaluate with old house scope
                        interpretExpr<String>(
                            it.id,
                            moduleScope,
                            localScope,
                        ).unwrap()!!,
                        interpretExpr<String>(
                            it.displayName,
                            moduleScope,
                            localScope,
                        ).unwrap()!!,
                        it.roomsMap.mapValues { (_, roomExpr) ->
                            interpretExpr<Room>(
                                roomExpr,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        },
                    ) as T
                }
                Expr.InnerCase.ROOM -> expr.room.let {
                    Room(
                        interpretExpr<String>(
                            it.displayName,
                            moduleScope,
                            localScope,
                        ).unwrap()!!,
                        interpretExpr<String>(
                            it.icon,
                            moduleScope,
                            localScope,
                        ).unwrap()!!,
                        it.controllersMap.mapValues { (_, controllerExpr) ->
                            interpretExpr<Controller>(
                                controllerExpr,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        }
                    ) as T
                }
                Expr.InnerCase.CONTROLLER -> expr.controller.let {
                    Controller(
                        interpretExpr<String>(
                            it.displayName,
                            moduleScope,
                            localScope,
                        ).unwrap()!!,
                        interpretExpr<String>(
                            it.brandColor,
                            moduleScope,
                            localScope,
                        ).unwrap()!!,
                        interpretExpr<DisplayInterface>(
                            it.displayInterface,
                            moduleScope,
                            localScope,
                        ).unwrap()!!
                    ) as T
                }
                Expr.InnerCase.DISPLAY_INTERFACE -> expr.displayInterface.let {
                    DisplayInterface(it.widgetsList.map { widget ->
                        interpretExpr<Widget>(
                            widget,
                            moduleScope,
                            localScope,
                        ).unwrap()!!
                    }) as T
                }
                Expr.InnerCase.DEVICE -> expr.device.let {
                    Device(it.lambdasMap.mapValues { (_, lambdaExpr) ->
                        interpretExpr<Fn>(
                            lambdaExpr,
                            moduleScope,
                            localScope,
                        ).unwrap()!!
                    }) as T
                }
                Expr.InnerCase.WIDGET -> expr.widget.let {
                    Widget(
                        it.widgetType,
                        it.paramsMap.mapValues { (_, paramExpr) ->
                            interpretExpr<Any>(
                                paramExpr,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        },
                        it.childrenList.map { childExpr ->
                            interpretExpr<Widget>(
                                childExpr,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        }
                    ) as T
                }
                Expr.InnerCase.IF -> expr.`if`.let {
                    if (interpretExpr<Boolean>(
                        it.condition,
                        moduleScope,
                        localScope,
                    ).unwrap()!!) {
                        interpretExpr<T>(
                            it.then,
                            moduleScope,
                            localScope,
                        ).unwrap()!!
                    } else {
                        val elif = it.elifList.find { elif ->
                            interpretExpr<Boolean>(
                                elif.condition,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        }
                        if (elif != null) {
                            interpretExpr<T>(
                                elif.then,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        } else {
                            interpretExpr<T>(
                                it.`else`,
                                moduleScope,
                                localScope,
                            ).unwrap()!!
                        }
                    }
                }
                Expr.InnerCase.INNER_NOT_SET -> TODO()
                null -> TODO()
            }
        }

    fun <T> interpretModule(
        module: ModuleOrBuilder,
    ): Result<T?, Throwable> = runCatching {
        interpretExpr<T>(
            module.body,
            mapOf("std" to stdModule) +
                    module.importsMap.mapValues { (_, m) -> interpretModule<Any>(m).unwrap() },
            mapOf(),
        ).unwrap()
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