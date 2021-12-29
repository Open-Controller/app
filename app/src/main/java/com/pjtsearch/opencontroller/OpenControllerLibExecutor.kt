package com.pjtsearch.opencontroller

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.*
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
import kotlin.collections.fold
import kotlin.reflect.KClass

sealed interface Panic {
    val msg: String
    val stack: List<StackCtx>
    fun withCtx(ctx: StackCtx): Panic

    data class Type(val expected: KClass<*>?, val actual: KClass<*>?, override val stack: List<StackCtx>) : Panic {
        constructor(stack: List<StackCtx>) : this(null, null, stack)

        override val msg: String
            get() = "Type Panic${if (expected != null) " : Expected $expected, was $actual" else ""}"

        override fun withCtx(ctx: StackCtx): Panic =
            Type(expected, actual, stack + ctx)
    }
}

sealed interface StackCtx {
    data class Fn(
        val lambdaName: String,
        val args: List<Any>
    ) : StackCtx {
        inline fun <reified T> tryCast(item: Any): Result<T, Panic.Type> =
            if (item is T) {
                Ok(item as T)
            } else {
                Err(Panic.Type(T::class, item::class, listOf(this)))
            }

        inline fun <reified T> Result<T, Panic>.ctx(): Result<T, Panic> =
            this.mapError { e -> e.withCtx(this@Fn) }

        fun typePanic(): Panic.Type =
            Panic.Type(listOf(this))

    }
}
    
fun <T> fnCtx(lambdaName: String, args: List<Any>, cb: StackCtx.Fn.() -> T) =
    StackCtx.Fn(lambdaName, args).run(cb)

fun <T, E : Exception> com.github.kittinunf.result.Result<T, E>.toResult(): Result<T, E> =
    this.component1()?.let {
        Ok(it)
    } ?: Err(this.component2()!!)

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
    val params: Map<String, Any>,
    val children: List<Widget>
)

typealias Fn = (List<Any>) -> Result<Any, Panic>

fun asString(item: Any): Result<String, Panic> = fnCtx("asString", listOf(item)) {
    when (item) {
        is String -> Ok("\"$item\"")
        is Int, is Long, is Float, is Boolean -> Ok("$item")
        is Map<*, *> -> binding { "{${item.entries.map { (k, v) -> "${asString(k!!).bind()}: ${asString(v!!).bind()}, " }}" }
        is List<*> -> binding { "[${item.map { "${asString(it!!).bind()}, " }}]" }
        is Result<*, *> -> binding { item.mapBoth({"Ok(${asString(it!!).bind()})"}, {"Err(${asString(it!!).bind()})"}) }
        is Optional<*> -> binding { if (item.isPresent) "Some(${asString(item.get()).bind()})" else "None" }
        is Flow<*> -> Ok("Observable")
        is Function<*> -> Ok("Fn")
        is Panic -> binding { "Panic: ${item.msg}\nStack:\n${item.stack.joinToString("\n") { asString(it).bind() }}" }
        is StackCtx -> when (item) {
            is StackCtx.Fn -> binding {"@${item.lambdaName}, args: ${item.args.joinToString(", ") { asString(it).bind() }}" }
        }
        is House -> binding { "House { id: ${asString(item.id)} displayName: ${asString(item.displayName)} rooms: ${asString(item.rooms)} }" }
        is Room -> binding { "Room { icon: ${asString(item.icon)} displayName: ${asString(item.displayName)} controllers: ${asString(item.controllers)} }" }
        is Controller -> binding { "Controller { brandColor: ${asString(Optional.ofNullable(item.brandColor))} displayName: ${asString(item.displayName)} displayInterface: ${asString(Optional.ofNullable(item.displayInterface))} }" }
        is DisplayInterface -> binding { "DisplayInterface { widgets: ${asString(item.widgets)} }" }
        is Device -> binding { "Device { lambdas: ${asString(item.lambdas)} }" }
        is Widget -> binding { "Widget { widgetType: ${asString(item.widgetType)} params: ${asString(item.params)} children: ${asString(item.children)} }" }

        else -> Err(typePanic())
    }
}

class OpenControllerLibExecutor(
    private var sockets: Map<String, Socket> = hashMapOf()
) : Serializable {
    private val builtins: Map<String, Fn> = mapOf(
        "=" to { args: List<Any> -> fnCtx("=", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is String -> tryCast<String>(second).map {
                    first == second
                }
                is Number -> tryCast<Number>(second).map {
                    first == second
                }
                is List<*> -> tryCast<List<*>>(second).map {
                    first == second
                }
                is Map<*, *> -> tryCast<Map<*, *>>(second).map {
                    first == second
                }
                else -> Err(typePanic())
            }
        }},
        "<=" to { args: List<Any> -> fnCtx("<=", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first <= second)
                        is Float -> Ok(first <= second)
                        is Long -> Ok(first <= second)
                        is Double -> Ok(first <= second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first <= second)
                        is Float -> Ok(first <= second)
                        is Long -> Ok(first <= second)
                        is Double -> Ok(first <= second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first <= second)
                        is Float -> Ok(first <= second)
                        is Long -> Ok(first <= second)
                        is Double -> Ok(first <= second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first <= second)
                        is Float -> Ok(first <= second)
                        is Long -> Ok(first <= second)
                        is Double -> Ok(first <= second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "<" to { args: List<Any> -> fnCtx("<", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first < second)
                        is Float -> Ok(first < second)
                        is Long -> Ok(first < second)
                        is Double -> Ok(first < second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first < second)
                        is Float -> Ok(first < second)
                        is Long -> Ok(first < second)
                        is Double -> Ok(first < second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first < second)
                        is Float -> Ok(first < second)
                        is Long -> Ok(first < second)
                        is Double -> Ok(first < second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first < second)
                        is Float -> Ok(first < second)
                        is Long -> Ok(first < second)
                        is Double -> Ok(first < second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        ">" to { args: List<Any> -> fnCtx(">", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first > second)
                        is Float -> Ok(first > second)
                        is Long -> Ok(first > second)
                        is Double -> Ok(first > second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first > second)
                        is Float -> Ok(first > second)
                        is Long -> Ok(first > second)
                        is Double -> Ok(first > second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first > second)
                        is Float -> Ok(first > second)
                        is Long -> Ok(first > second)
                        is Double -> Ok(first > second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first > second)
                        is Float -> Ok(first > second)
                        is Long -> Ok(first > second)
                        is Double -> Ok(first > second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        ">=" to { args: List<Any> -> fnCtx(">=", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first >= second)
                        is Float -> Ok(first >= second)
                        is Long -> Ok(first >= second)
                        is Double -> Ok(first >= second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first >= second)
                        is Float -> Ok(first >= second)
                        is Long -> Ok(first >= second)
                        is Double -> Ok(first >= second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first >= second)
                        is Float -> Ok(first >= second)
                        is Long -> Ok(first >= second)
                        is Double -> Ok(first >= second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first >= second)
                        is Float -> Ok(first >= second)
                        is Long -> Ok(first >= second)
                        is Double -> Ok(first >= second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "&&" to { args: List<Any> -> fnCtx("&&", args) {
            Ok(args[0] as Boolean && args[1] as Boolean)
        }},
        "||" to { args: List<Any> -> fnCtx("||", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Boolean -> when (second) {
                    is Boolean -> Ok(first || second)
                    else -> Err(typePanic())
                }
                is Optional<*> -> Ok(if (first.isPresent) first.get() else second)
                is Result<*, *> -> Ok(first.getOr(second)!!)
                else -> Err(typePanic())
            }
        }},
        "unwrap" to { args: List<Any> -> fnCtx("unwrap", args) {
            when (val item = args[0]) {
                is Optional<*> -> if (item.isPresent) Ok(item.get()) else Err(typePanic())
                is Result<*, *> -> item.mapError { typePanic() }
                else -> Err(typePanic())
            }
        }},
        "+" to { args: List<Any> -> fnCtx("+", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is String -> {
                    when (second) {
                        is String, is Number -> {
                            Ok(first + second)
                        }
                        else -> {
                            Err(typePanic())
                        }
                    }
                }
                is Int -> {
                    when (second) {
                        is Int -> Ok(first + second)
                        is Float -> Ok(first + second)
                        is Long -> Ok(first + second)
                        is Double -> Ok(first + second)
                        is String -> Ok(first.toString() + second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first + second)
                        is Float -> Ok(first + second)
                        is Long -> Ok(first + second)
                        is Double -> Ok(first + second)
                        is String -> Ok(first.toString() + second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first + second)
                        is Float -> Ok(first + second)
                        is Long -> Ok(first + second)
                        is Double -> Ok(first + second)
                        is String -> Ok(first.toString() + second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first + second)
                        is Float -> Ok(first + second)
                        is Long -> Ok(first + second)
                        is Double -> Ok(first + second)
                        is String -> Ok(first.toString() + second)
                        else -> Err(typePanic())
                    }
                }
                is List<*> -> {
                    Ok(first + second)
                }
                is Map<*, *> -> {
                    when (second) {
                        is Map<*, *> -> Ok(first + second)
                        is Pair<*, *> -> Ok(first + second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "-" to { args: List<Any> -> fnCtx("-", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first - second)
                        is Float -> Ok(first - second)
                        is Long -> Ok(first - second)
                        is Double -> Ok(first - second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first - second)
                        is Float -> Ok(first - second)
                        is Long -> Ok(first - second)
                        is Double -> Ok(first - second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first - second)
                        is Float -> Ok(first - second)
                        is Long -> Ok(first - second)
                        is Double -> Ok(first - second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first - second)
                        is Float -> Ok(first - second)
                        is Long -> Ok(first - second)
                        is Double -> Ok(first - second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "*" to { args: List<Any> -> fnCtx("*", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first * second)
                        is Float -> Ok(first * second)
                        is Long -> Ok(first * second)
                        is Double -> Ok(first * second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first * second)
                        is Float -> Ok(first * second)
                        is Long -> Ok(first * second)
                        is Double -> Ok(first * second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first * second)
                        is Float -> Ok(first * second)
                        is Long -> Ok(first * second)
                        is Double -> Ok(first * second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first * second)
                        is Float -> Ok(first * second)
                        is Long -> Ok(first * second)
                        is Double -> Ok(first * second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "/" to { args: List<Any> -> fnCtx("/", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first / second)
                        is Float -> Ok(first / second)
                        is Long -> Ok(first / second)
                        is Double -> Ok(first / second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first / second)
                        is Float -> Ok(first / second)
                        is Long -> Ok(first / second)
                        is Double -> Ok(first / second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first / second)
                        is Float -> Ok(first / second)
                        is Long -> Ok(first / second)
                        is Double -> Ok(first / second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first / second)
                        is Float -> Ok(first / second)
                        is Long -> Ok(first / second)
                        is Double -> Ok(first / second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "%" to { args: List<Any> -> fnCtx("%", args) {
            val first = args[0]
            val second = args[1]
            when (first) {
                is Int -> {
                    when (second) {
                        is Int -> Ok(first % second)
                        is Float -> Ok(first % second)
                        is Long -> Ok(first % second)
                        is Double -> Ok(first % second)
                        else -> Err(typePanic())
                    }
                }
                is Float -> {
                    when (second) {
                        is Int -> Ok(first % second)
                        is Float -> Ok(first % second)
                        is Long -> Ok(first % second)
                        is Double -> Ok(first % second)
                        else -> Err(typePanic())
                    }
                }
                is Long -> {
                    when (second) {
                        is Int -> Ok(first % second)
                        is Float -> Ok(first % second)
                        is Long -> Ok(first % second)
                        is Double -> Ok(first % second)
                        else -> Err(typePanic())
                    }
                }
                is Double -> {
                    when (second) {
                        is Int -> Ok(first % second)
                        is Float -> Ok(first % second)
                        is Long -> Ok(first % second)
                        is Double -> Ok(first % second)
                        else -> Err(typePanic())
                    }
                }
                else -> {
                    Err(typePanic())
                }
            }
        }},
        "getLambda" to { args: List<Any> -> fnCtx("getLambda", args) {
            val device = args[0] as Device
            val lambda = args[1] as String
            Ok(device.lambdas[lambda]!!)
        }},
        "pipe" to { args: List<Any> -> fnCtx("pipe", args) { binding {
            val value = args[0]
            args.drop(1).fold(value) { lastResult, curr ->
                tryCast<Fn>(curr).bind()(listOf(lastResult))
            }
        } }},
        "listOf" to { args: List<Any> -> fnCtx("listOf", args) {
            Ok(args)
        }},
        "mapOf" to { args: List<Any> -> fnCtx("mapOf", args) { binding {
            val pairs = tryCast<List<Pair<Any, Any>>>(args).bind()
            mapOf(*pairs.toTypedArray())
        } }},
        "pair" to { args: List<Any> -> fnCtx("pair", args) {
            val key = args[0]
            val value = args[1]
            Ok(key to value)
        }},
        "index" to { args: List<Any> -> fnCtx("index", args) {
            val input = args[0]
            val path = args.drop(1)
            fun getIndex(input: Any?, path: List<Any>): Any? {
                return if (path.isEmpty()) {
                    input
                } else {
                    when (input) {
//                        TODO: Panic
                        is List<*> -> getIndex(input[path[0] as Int], path.drop(1))
                        is Map<*, *> -> getIndex(input[path[0]], path.drop(1))
                        else -> TODO()
                    }
                }
            }
            Ok(Optional.ofNullable(getIndex(input, path)))
        }},
        "map" to { args: List<Any> -> fnCtx("map", args) { binding {
            val transformer = tryCast<Fn>(args[0]).bind();
            { innerArgs: List<Any> ->
                when (val input = innerArgs[0]) {
                    is Flow<*> -> binding { input.map {
//                        TODO: fix bind
                        it?.let { transformer(listOf(it)).bind() } ?: Err(typePanic()).bind()
                    }}
                    is List<*> -> binding { input.map {
                        it?.let { transformer(listOf(it)).bind() } ?: Err(typePanic()).bind()
                    }}
                    is Result<*, *> -> binding<Result<*, *>, Panic> { input.map {
                        it?.let { transformer(listOf(it)).bind() } ?: Err(typePanic()).bind()
                    }}
                    is Optional<*> -> binding<Optional<*>, Panic> { input.map {
                        it?.let { transformer(listOf(it)).bind() } ?: Err(typePanic()).bind()
                    }}
                    else -> Err(typePanic())
                }
            }
        } }},
        "delay" to { args: List<Any> -> fnCtx("delay", args) {
            val amount = args[0] as Int
            Thread.sleep(amount.toLong())
            Ok(Unit)
        }},
        "asString" to { args: List<Any> -> asString(args[0]) }
    )

    private val stdModule = Device(
        mapOf(
            "httpRequest" to { args: List<Any> -> fnCtx("httpRequest", args) {
                val url = args[0] as String
                when (args[1] as String) {
                    "GET" -> Ok(url.httpGet().responseString().third.toResult())
                    "HEAD" -> Ok(url.httpHead().responseString().third.toResult())
                    "POST" -> Ok(url.httpPost().responseString().third.toResult())
                    "PUT" -> Ok(url.httpPut().responseString().third.toResult())
                    "PATCH" -> Ok(url.httpPatch().responseString().third.toResult())
                    "DELETE" -> Ok(url.httpDelete().responseString().third.toResult())
                    else -> Err(typePanic())
                }
            }},
            "tcpRequest" to { args: List<Any> -> fnCtx("tcpRequest", args) {
                val (host, port) = (args[0] as String).split(":")
                var client:Socket? = null
                try {
                    client = sockets["$host:$port"] ?: Socket(host, port.toInt()).let { s ->
                        sockets = sockets + ("$host:$port" to s)
                        s
                    }
                } catch (err: Exception) {
                    err.printStackTrace()
                }
                val command = args[1] as String
                client?.outputStream?.write((command+"\r\n").toByteArray())
                Thread.sleep(300)
                client?.close()
                sockets = sockets - "$host:$port"
                Ok(Unit)
            }},
            "observeTime" to { args: List<Any> -> fnCtx("observeTime", args) {
                val interval = args[0] as Number
                Ok(flow {
                    while (true) {
                        emit(Date().time)
                        delay(interval.toLong())
                    }
                })
            }},
        )
    )

    fun interpretExpr(
        expr: ExprOrBuilder,
        moduleScope: Map<String, Any>,
        localScope: Map<String, Any>,
    ): Result<Any, Panic> =
        when (expr.innerCase) {
            Expr.InnerCase.REF -> expr.ref.let { fnCtx("REF", listOf(it.ref)) {
                (localScope[it.ref] ?: builtins[it.ref] ?: moduleScope[it.ref])?.let { r ->
                    Ok(r)
                } ?: Err(typePanic())
            }}
            Expr.InnerCase.LAMBDA -> expr.lambda.let {
                Ok<Fn> { args: List<Any> -> fnCtx("LAMBDA", args) {
                    interpretExpr(
                        it.`return`,
                        moduleScope,
                        localScope + mapOf(*it.argsList.mapIndexed { i, arg ->
                            arg to args[i]
                        }.toTypedArray()),
                    ).ctx()
                }}
            }
            Expr.InnerCase.CALL -> expr.call.let { fnCtx("CALL", listOf()) { binding {
                val fn = interpretExpr(
                    it.calling,
                    moduleScope,
                    localScope,
                ).ctx().bind()
                tryCast<Fn>(fn).bind()(
                    it.argsList.map { arg ->
                        interpretExpr(
                            arg,
                            moduleScope,
                            localScope,
                        ).ctx().bind()
                    },
                ).ctx().bind()
            }} }
            Expr.InnerCase.STRING -> Ok(expr.string)
            Expr.InnerCase.INT64 -> Ok(expr.int64)
            Expr.InnerCase.INT32 -> Ok(expr.int32)
            Expr.InnerCase.FLOAT -> Ok(expr.float)
            Expr.InnerCase.BOOL -> Ok(expr.bool)
            Expr.InnerCase.HOUSE -> expr.house.let { fnCtx("HOUSE", listOf()) { binding {
                House(
//                      Evaluate with old house scope
                    tryCast<String>(interpretExpr(
                        it.id,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind(),
                    tryCast<String>(interpretExpr(
                        it.displayName,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind(),
                    it.roomsMap.mapValues { (_, roomExpr) ->
                        tryCast<Room>(interpretExpr(
                            roomExpr,
                            moduleScope,
                            localScope,
                        ).ctx().bind()).bind()
                    },
                )
            }} }
            Expr.InnerCase.ROOM -> expr.room.let { fnCtx("ROOM", listOf()) { binding {
                Room(
                    tryCast<String>(interpretExpr(
                        it.displayName,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind(),
                    tryCast<String>(interpretExpr(
                        it.icon,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind(),
                    it.controllersMap.mapValues { (_, controllerExpr) ->
                        tryCast<Controller>(interpretExpr(
                            controllerExpr,
                            moduleScope,
                            localScope,
                        ).ctx().bind()).bind()
                    }
                )
            }} }
            Expr.InnerCase.CONTROLLER -> expr.controller.let { fnCtx("CONTROLLER", listOf()) { binding {
                Controller(
                    tryCast<String>(interpretExpr(
                        it.displayName,
                        moduleScope,
                        localScope,
                    ).bind()).bind(),
                    tryCast<String>(interpretExpr(
                        it.brandColor,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind(),
                    tryCast<DisplayInterface>(interpretExpr(
                        it.displayInterface,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind()
                )
            }} }
            Expr.InnerCase.DISPLAY_INTERFACE -> expr.displayInterface.let { fnCtx("DISPLAY_INTERFACE", listOf()) { binding {
                DisplayInterface(it.widgetsList.map { widget ->
                    tryCast<Widget>(interpretExpr(
                        widget,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind()
                })
            }} }
            Expr.InnerCase.DEVICE -> expr.device.let { fnCtx("DEVICE", listOf()) { binding {
                Device(it.lambdasMap.mapValues { (_, lambdaExpr) ->
                    tryCast<Fn>(interpretExpr(
                        lambdaExpr,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind()
                })
            }} }
            Expr.InnerCase.WIDGET -> expr.widget.let { fnCtx("DEVICE", listOf()) { binding {
                Widget(
                    it.widgetType,
                    it.paramsMap.mapValues { (_, paramExpr) ->
                        interpretExpr(
                            paramExpr,
                            moduleScope,
                            localScope,
                        ).ctx().bind()
                    },
                    it.childrenList.map { childExpr ->
                        tryCast<Widget>(interpretExpr(
                            childExpr,
                            moduleScope,
                            localScope,
                        ).ctx().bind()).bind()
                    }
                )
            }} }
            Expr.InnerCase.IF -> expr.`if`.let { fnCtx("IF", listOf()) { binding {
                if (tryCast<Boolean>(interpretExpr(
                        it.condition,
                        moduleScope,
                        localScope,
                    ).ctx().bind()).bind()) {
                    interpretExpr(
                        it.then,
                        moduleScope,
                        localScope,
                    ).ctx().bind()
                } else {
                    val elif = it.elifList.find { elif ->
                        tryCast<Boolean>(interpretExpr(
                            elif.condition,
                            moduleScope,
                            localScope,
                        ).ctx().bind()).bind()
                    }
                    if (elif != null) {
                        interpretExpr(
                            elif.then,
                            moduleScope,
                            localScope,
                        ).ctx().bind()
                    } else {
                        interpretExpr(
                            it.`else`,
                            moduleScope,
                            localScope,
                        ).ctx().bind()
                    }
                }
            }} }
            Expr.InnerCase.INNER_NOT_SET -> TODO()
            null -> TODO()
        }

    fun interpretModule(
        module: ModuleOrBuilder,
    ): Result<Any, Panic> = binding {
        interpretExpr(
            module.body,
            mapOf("std" to stdModule) +
                    module.importsMap.mapValues { (_, m) -> interpretModule(m).bind() },
            mapOf(),
        ).bind()
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