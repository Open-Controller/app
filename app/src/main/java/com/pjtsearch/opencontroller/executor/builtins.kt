package com.pjtsearch.opencontroller.executor

import com.github.michaelbull.result.*
import com.pjtsearch.opencontroller.extensions.mapOr
import com.pjtsearch.opencontroller.extensions.mapOrElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.collections.fold


fun asString(item: Any): Result<String, Panic> = fnCtx("asString", listOf(item)) {
    when (item) {
        is String -> Ok("\"$item\"")
        is Int, is Long, is Float, is Boolean -> Ok("$item")
        is Map<*, *> -> binding {
            "{${
                item.entries.joinToString(", ") { (k, v) ->
                    "${asString(k!!).bind()}: ${
                        asString(
                            v!!
                        ).bind()
                    }"
                }
            }}"
        }
        is List<*> -> binding { "[${item.map { "${asString(it!!).bind()}, " }}]" }
        is Result<*, *> -> binding {
            item.mapBoth(
                { "Ok(${asString(it!!).bind()})" },
                { "Err(${asString(it!!).bind()})" })
        }
        is Optional<*> -> binding { if (item.isPresent) "Some(${asString(item.get()).bind()})" else "None" }
        is Flow<*> -> Ok("Observable")
        is Function<*> -> Ok("Fn")
        is Panic -> binding {
            "Panic: ${item.msg}\nStack:\n${
                item.stack.joinToString("\n") {
                    asString(
                        it
                    ).bind()
                }
            }"
        }
        is StackCtx -> when (item) {
            is StackCtx.Fn -> binding {
                "at ${item.lambdaName}(${
                    item.args.joinToString(", ") {
                        asString(
                            it
                        ).bind()
                    }
                })"
            }
            is StackCtx.Syntax -> binding {
                "in ${item.name}(${
                    item.params.joinToString(", ") {
                        asString(
                            it
                        ).bind()
                    }
                }) ${item.position.file} ${item.position.line}:${item.position.column}"
            }
        }
        is House -> binding {
            "House { id: ${asString(item.id).bind()} displayName: ${asString(item.displayName).bind()} rooms: ${
                asString(
                    item.rooms
                ).bind()
            } }"
        }
        is Room -> binding {
            "Room { icon: ${asString(item.icon).bind()} displayName: ${asString(item.displayName).bind()} controllers: ${
                asString(
                    item.controllers
                ).bind()
            } }"
        }
        is Controller -> binding {
            "Controller { brandColor: ${asString(Optional.ofNullable(item.brandColor)).bind()} displayName: ${
                asString(
                    item.displayName
                ).bind()
            } displayInterface: ${asString(Optional.ofNullable(item.displayInterface)).bind()} }"
        }
        is DisplayInterface -> binding { "DisplayInterface { widgets: ${asString(item.widgets).bind()} }" }
        is Device -> binding { "Device { lambdas: ${asString(item.lambdas).bind()} }" }
        is Widget -> binding {
            "Widget { widgetType: ${asString(item.widgetType).bind()} params: ${
                asString(
                    item.params
                ).bind()
            } children: ${asString(item.children).bind()} }"
        }

        else -> Err(typePanic())
    }
}

fun StackCtx.Fn.eq(args: List<Any>): Result<Any, Panic> {
    val first = args[0]
    val second = args[1]
    return when (first) {
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
        is Optional<*> -> tryCast<Optional<*>>(second).map {
            when (first.isPresent to it.isPresent) {
                true to true -> eq(listOf(first.get(), it.get()))
                false to false -> true
                else -> false
            }
        }
        else -> Err(typePanic())
    }
}

val builtinFns: Map<String, Fn> = mapOf<String, Fn>(
    "=" to { args: List<Any> ->
        fnCtx("=", args) {
            eq(args)
        }
    },
    "<=" to { args: List<Any> ->
        fnCtx("<=", args) {
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
        }
    },
    "<" to { args: List<Any> ->
        fnCtx("<", args) {
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
        }
    },
    ">" to { args: List<Any> ->
        fnCtx(">", args) {
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
        }
    },
    ">=" to { args: List<Any> ->
        fnCtx(">=", args) {
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
        }
    },
    "&&" to { args: List<Any> ->
        fnCtx("&&", args) {
            Ok(args[0] as Boolean && args[1] as Boolean)
        }
    },
    "||" to { args: List<Any> ->
        fnCtx("||", args) {
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
        }
    },
    "unwrap" to { args: List<Any> ->
        fnCtx("unwrap", args) {
            when (val item = args[0]) {
                is Optional<*> -> if (item.isPresent) Ok(item.get()) else Err(typePanic())
                is Result<*, *> -> item.mapError { typePanic() }
                else -> Err(typePanic())
            }
        }
    },
    "+" to { args: List<Any> ->
        fnCtx("+", args) {
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
        }
    },
    "-" to { args: List<Any> ->
        fnCtx("-", args) {
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
        }
    },
    "*" to { args: List<Any> ->
        fnCtx("*", args) {
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
        }
    },
    "/" to { args: List<Any> ->
        fnCtx("/", args) {
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
        }
    },
    "%" to { args: List<Any> ->
        fnCtx("%", args) {
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
        }
    },
    "getLambda" to { args: List<Any> ->
        fnCtx("getLambda", args) {
            val device = args[0] as Device
            val lambda = args[1] as String
            Ok(device.lambdas[lambda]!!)
        }
    },
    "pipe" to { args: List<Any> ->
        fnCtx("pipe", args) {
            binding {
                val value = args[0]
                args.drop(1).fold(value) { lastResult, curr ->
                    tryCast<Fn>(curr).bind()(listOf(lastResult))
                }
            }
        }
    },
    "listOf" to { args: List<Any> ->
        fnCtx("listOf", args) {
            Ok(args)
        }
    },
    "mapOf" to { args: List<Any> ->
        fnCtx("mapOf", args) {
            binding {
                val pairs = tryCast<List<Pair<Any, Any>>>(args).bind()
                mapOf(*pairs.toTypedArray())
            }
        }
    },
    "pair" to { args: List<Any> ->
        fnCtx("pair", args) {
            val key = args[0]
            val value = args[1]
            Ok(key to value)
        }
    },
    "index" to { args: List<Any> ->
        fnCtx("index", args) {
            val input = args[0]
            val path = args.drop(1)
            fun getIndex(input: Any?, path: List<Any>): Any? {
                return if (path.isEmpty()) {
                    input
                } else {
                    when (input) {
                        is List<*> -> getIndex(input[path[0] as Int], path.drop(1))
                        is Map<*, *> -> getIndex(input[path[0]], path.drop(1))
                        else -> Err(typePanic())
                    }
                }
            }
            Ok(Optional.ofNullable(getIndex(input, path)))
        }
    },
    "map" to { args: List<Any> ->
        fnCtx("map", args) {
            binding {
                val transformer = tryCast<Fn>(args[0]).bind();
                { innerArgs: List<Any> ->
                    when (val input = innerArgs[0]) {
                        is Flow<*> -> binding {
                            input.map {
//                        TODO: fix bind
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        is List<*> -> binding {
                            input.map {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        is Result<*, *> -> binding<Result<*, *>, Panic> {
                            input.map {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        is Optional<*> -> binding<Optional<*>, Panic> {
                            input.map {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        else -> Err(typePanic())
                    }
                }
            }
        }
    },
    "mapOr" to { args: List<Any> ->
        fnCtx("mapOr", args) {
            binding {
                val default = args[0];
                val transformer = tryCast<Fn>(args[1]).bind();
                { innerArgs: List<Any> ->
                    when (val input = innerArgs[0]) {
                        is Result<*, *> -> binding<Any, Panic> {
                            input.mapOr(default) {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        is Optional<*> -> binding<Any, Panic> {
                            input.mapOr(default) {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        else -> Err(typePanic())
                    }
                }
            }
        }
    },
    "mapOrElse" to { args: List<Any> ->
        fnCtx("mapOrElse", args) {
            binding {
                val getDefault = tryCast<Fn>(args[0]).bind();
                val transformer = tryCast<Fn>(args[1]).bind();
                { innerArgs: List<Any> ->
                    when (val input = innerArgs[0]) {
                        is Result<*, *> -> binding<Any, Panic> {
                            input.mapOrElse({ getDefault(listOf()) }) {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        is Optional<*> -> binding<Any, Panic> {
                            input.mapOrElse({ getDefault(kotlin.collections.listOf()) }) {
                                it?.let { transformer(listOf(it)).bind() }
                                    ?: Err(typePanic()).bind()
                            }
                        }
                        else -> Err(typePanic())
                    }
                }
            }
        }
    },
    "delay" to { args: List<Any> ->
        fnCtx("delay", args) {
            val amount = args[0] as Int
            Thread.sleep(amount.toLong())
            Ok(Unit)
        }
    },
    "asString" to { args: List<Any> -> asString(args[0]) }
)
val builtinValues = mapOf<String, Any>(
    "None" to Optional.empty<Any>()
)
val builtins = builtinFns + builtinValues
