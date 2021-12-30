package com.pjtsearch.opencontroller.executor

import com.github.michaelbull.result.*
import com.pjtsearch.opencontroller_lib_proto.Expr
import com.pjtsearch.opencontroller_lib_proto.ExprOrBuilder
import com.pjtsearch.opencontroller_lib_proto.ModuleOrBuilder
import java.io.Serializable
import java.net.Socket
import kotlin.reflect.KClass

sealed interface Panic {
    val msg: String
    val stack: List<StackCtx>
    fun withCtx(ctx: StackCtx): Panic

    data class Type(
        val expected: KClass<*>?,
        val actual: KClass<*>?,
        override val stack: List<StackCtx>
    ) :
        Panic {
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

class OpenControllerLibExecutor(
    private var sockets: Map<String, Socket> = hashMapOf()
) : Serializable {
    private val stdModule = getStdModule(
        { id -> sockets[id] },
        { id, socket -> sockets + (id to socket) },
        { id -> sockets - id }
    )

    fun interpretExpr(
        expr: ExprOrBuilder,
        moduleScope: Map<String, Any>,
        localScope: Map<String, Any>,
    ): Result<Any, Panic> =
        when (expr.innerCase) {
            Expr.InnerCase.REF -> expr.ref.let {
                fnCtx("REF", listOf(it.ref)) {
                    (localScope[it.ref] ?: builtins[it.ref] ?: moduleScope[it.ref])?.let { r ->
                        Ok(r)
                    } ?: Err(typePanic())
                }
            }
            Expr.InnerCase.LAMBDA -> expr.lambda.let {
                Ok<Fn> { args: List<Any> ->
                    fnCtx("LAMBDA", args) {
                        interpretExpr(
                            it.`return`,
                            moduleScope,
                            localScope + mapOf(*it.argsList.mapIndexed { i, arg ->
                                arg to args[i]
                            }.toTypedArray()),
                        ).ctx()
                    }
                }
            }
            Expr.InnerCase.CALL -> expr.call.let {
                fnCtx("CALL", listOf()) {
                    binding {
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
                    }
                }
            }
            Expr.InnerCase.STRING -> Ok(expr.string)
            Expr.InnerCase.INT64 -> Ok(expr.int64)
            Expr.InnerCase.INT32 -> Ok(expr.int32)
            Expr.InnerCase.FLOAT -> Ok(expr.float)
            Expr.InnerCase.BOOL -> Ok(expr.bool)
            Expr.InnerCase.HOUSE -> expr.house.let {
                fnCtx("HOUSE", listOf()) {
                    binding {
                        House(
//                      Evaluate with old house scope
                            tryCast<String>(
                                interpretExpr(
                                    it.id,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind(),
                            tryCast<String>(
                                interpretExpr(
                                    it.displayName,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind(),
                            it.roomsMap.mapValues { (_, roomExpr) ->
                                tryCast<Room>(
                                    interpretExpr(
                                        roomExpr,
                                        moduleScope,
                                        localScope,
                                    ).ctx().bind()
                                ).bind()
                            },
                        )
                    }
                }
            }
            Expr.InnerCase.ROOM -> expr.room.let {
                fnCtx("ROOM", listOf()) {
                    binding {
                        Room(
                            tryCast<String>(
                                interpretExpr(
                                    it.displayName,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind(),
                            tryCast<String>(
                                interpretExpr(
                                    it.icon,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind(),
                            it.controllersMap.mapValues { (_, controllerExpr) ->
                                tryCast<Controller>(
                                    interpretExpr(
                                        controllerExpr,
                                        moduleScope,
                                        localScope,
                                    ).ctx().bind()
                                ).bind()
                            }
                        )
                    }
                }
            }
            Expr.InnerCase.CONTROLLER -> expr.controller.let {
                fnCtx("CONTROLLER", listOf()) {
                    binding {
                        Controller(
                            tryCast<String>(
                                interpretExpr(
                                    it.displayName,
                                    moduleScope,
                                    localScope,
                                ).bind()
                            ).bind(),
                            tryCast<String>(
                                interpretExpr(
                                    it.brandColor,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind(),
                            tryCast<DisplayInterface>(
                                interpretExpr(
                                    it.displayInterface,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind()
                        )
                    }
                }
            }
            Expr.InnerCase.DISPLAY_INTERFACE -> expr.displayInterface.let {
                fnCtx("DISPLAY_INTERFACE", listOf()) {
                    binding {
                        DisplayInterface(it.widgetsList.map { widget ->
                            tryCast<Widget>(
                                interpretExpr(
                                    widget,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind()
                        })
                    }
                }
            }
            Expr.InnerCase.DEVICE -> expr.device.let {
                fnCtx("DEVICE", listOf()) {
                    binding {
                        Device(it.lambdasMap.mapValues { (_, lambdaExpr) ->
                            tryCast<Fn>(
                                interpretExpr(
                                    lambdaExpr,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind()
                        })
                    }
                }
            }
            Expr.InnerCase.WIDGET -> expr.widget.let {
                fnCtx("DEVICE", listOf()) {
                    binding {
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
                                tryCast<Widget>(
                                    interpretExpr(
                                        childExpr,
                                        moduleScope,
                                        localScope,
                                    ).ctx().bind()
                                ).bind()
                            }
                        )
                    }
                }
            }
            Expr.InnerCase.IF -> expr.`if`.let {
                fnCtx("IF", listOf()) {
                    binding {
                        if (tryCast<Boolean>(
                                interpretExpr(
                                    it.condition,
                                    moduleScope,
                                    localScope,
                                ).ctx().bind()
                            ).bind()
                        ) {
                            interpretExpr(
                                it.then,
                                moduleScope,
                                localScope,
                            ).ctx().bind()
                        } else {
                            val elif = it.elifList.find { elif ->
                                tryCast<Boolean>(
                                    interpretExpr(
                                        elif.condition,
                                        moduleScope,
                                        localScope,
                                    ).ctx().bind()
                                ).bind()
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
                    }
                }
            }
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
}