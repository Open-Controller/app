/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.pjtsearch.opencontroller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller.executor.*
import com.pjtsearch.opencontroller_lib_proto.*
import org.junit.Assert
import org.junit.Test
import java.util.*

class OpenControllerLibExecutorTest {
    @Test
    fun closureScope() {
        val executor = OpenControllerLibExecutor()
        val lambda = Expr.newBuilder().setLambda(
            LambdaExpr.newBuilder().addArgs("arg").setReturn(
                Expr.newBuilder().setCall(
                    CallExpr.newBuilder().setCalling(
                        Expr.newBuilder().setLambda(
                            LambdaExpr.newBuilder().setReturn(
                                Expr.newBuilder()
                                    .setRef(RefExpr.newBuilder().setRef("arg"))
                            )
                        )
                    )
                )
            )
        ).build()
        val res = executor.interpretExpr(lambda, mapOf(), mapOf()).unwrap() as Fn
        Assert.assertEquals(Ok("123"), res(listOf("123")))
    }

    @Test
    fun module() {
        val executor = OpenControllerLibExecutor()
        val module = Module.newBuilder()
            .putImports(
                "otherModule", Module.newBuilder().setBody(
                    Expr.newBuilder().setLambda(
                        LambdaExpr.newBuilder().addArgs("arg").setReturn(
                            Expr.newBuilder().setCall(
                                CallExpr.newBuilder()
                                    .setCalling(
                                        Expr.newBuilder()
                                            .setRef(RefExpr.newBuilder().setRef("+"))
                                    )
                                    .addArgs(Expr.newBuilder().setString("added "))
                                    .addArgs(
                                        Expr.newBuilder()
                                            .setRef(RefExpr.newBuilder().setRef("arg"))
                                    )
                            )
                        )
                    )
                ).build()
            )
            .setBody(
                Expr.newBuilder().setLambda(
                    LambdaExpr.newBuilder().setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder().setRef(
                                        RefExpr.newBuilder().setRef("otherModule")
                                    )
                                )
                                .addArgs(Expr.newBuilder().setString("123"))
                        )
                    )
                ).build()
            )
        val res = executor.interpretModule(module).unwrap() as Fn
        Assert.assertEquals(Ok("added 123"), res(listOf()))
    }

    @Test
    fun builtins() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .addArgs(
                                    Expr.newBuilder()
                                        .setString("https://jsonplaceholder.typicode.com/posts/1")
                                )
                                .addArgs(Expr.newBuilder().setString("GET"))
                                .setCalling(
                                    Expr.newBuilder().setCall(
                                        CallExpr.newBuilder()
                                            .addArgs(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder().setRef("std")
                                                )
                                            )
                                            .addArgs(
                                                Expr.newBuilder().setString("httpRequest")
                                            )
                                            .setCalling(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder()
                                                        .setRef("getLambda")
                                                )
                                            )
                                    )
                                )
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
//        Second Ok for http result
        Assert.assertEquals(
            Ok(
                Ok(
                    """
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """.trimIndent()
                )
            ), res(listOf())
        )
    }

    @Test
    fun args() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .addArgs("method")
                    .addArgs("postNumber")
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .addArgs(
                                    Expr.newBuilder().setCall(
                                        CallExpr.newBuilder()
                                            .addArgs(
                                                Expr.newBuilder()
                                                    .setString("https://jsonplaceholder.typicode.com/posts/")
                                            )
                                            .addArgs(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder()
                                                        .setRef("postNumber")
                                                )
                                            )
                                            .setCalling(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder().setRef("+")
                                                )
                                            )
                                    )
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("method"))
                                )
                                .setCalling(
                                    Expr.newBuilder().setCall(
                                        CallExpr.newBuilder()
                                            .addArgs(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder().setRef("std")
                                                )
                                            )
                                            .addArgs(
                                                Expr.newBuilder().setString("httpRequest")
                                            )
                                            .setCalling(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder()
                                                        .setRef("getLambda")
                                                )
                                            )
                                    )
                                )
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
//        Second Ok for http result
        Assert.assertEquals(
            Ok(
                Ok(
                    """
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """.trimIndent()
                )
            ), res(listOf("GET", "1"))
        )
    }


    @Test
    fun list() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder()
                    .setCalling(
                        Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("listOf"))
                    )
                    .addArgs(Expr.newBuilder().setString("1"))
                    .addArgs(Expr.newBuilder().setString("2"))
                    .addArgs(Expr.newBuilder().setString("3"))
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as List<String>
        Assert.assertEquals(listOf("1", "2", "3"), res)
    }

    @Test
    fun map() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder()
                    .setCalling(
                        Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("mapOf"))
                    )
                    .addArgs(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("pair"))
                                )
                                .addArgs(Expr.newBuilder().setString("1"))
                                .addArgs(Expr.newBuilder().setString("a"))
                        )
                    )
                    .addArgs(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("pair"))
                                )
                                .addArgs(Expr.newBuilder().setString("2"))
                                .addArgs(Expr.newBuilder().setString("b"))
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Map<String, String>
        Assert.assertEquals(mapOf("1" to "a", "2" to "b"), res)
    }

    @Test
    fun index() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .addArgs("of")
                    .addArgs("i")
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("index"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("of"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("i"))
                                )
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
        Assert.assertEquals(
            Ok(Optional.of("a")),
            res(listOf(mapOf("1" to "a", "2" to "b"), "1"))
        )
        Assert.assertEquals(Ok(Optional.of("2")), res(listOf(listOf("1", "2", "3"), 1)))
    }

    @Test
    fun indexDeep() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .addArgs("of")
                    .addArgs("i")
                    .addArgs("i2")
                    .addArgs("i3")
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("index"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("of"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("i"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("i2"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("i3"))
                                )
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
        Assert.assertEquals(
            Ok(Optional.of("b")),
            res(
                listOf(
                    mapOf("1" to listOf("1", mapOf("a" to "b"), "3"), "2" to "b"),
                    "1",
                    1,
                    "a"
                )
            )
        )
    }


    @Test
    fun mapResult() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .addArgs("of")
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder().setCall(
                                        CallExpr.newBuilder()
                                            .setCalling(
                                                Expr.newBuilder().setRef(
                                                    RefExpr.newBuilder().setRef("map")
                                                )
                                            )
                                            .addArgs(
                                                Expr.newBuilder().setLambda(
                                                    LambdaExpr.newBuilder().setReturn(
                                                        Expr.newBuilder()
                                                            .setString("mapped")
                                                    )
                                                )
                                            )
                                    )
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("of"))
                                )
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
//        Two Oks because should return the mapped result
        Assert.assertEquals(Ok(Ok("mapped")), res(listOf(Ok("asdf"))))
    }

    @Test
    fun orOptional() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .addArgs("of")
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("||"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("of"))
                                )
                                .addArgs(Expr.newBuilder().setString("replaced"))
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
        Assert.assertEquals(Ok("replaced"), res(listOf(Optional.empty<Nothing>())))
        Assert.assertEquals(Ok("test"), res(listOf(Optional.ofNullable("test"))))
    }

    @Test
    fun unwrapResult() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(
            Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder()
                    .addArgs("of")
                    .setReturn(
                        Expr.newBuilder().setCall(
                            CallExpr.newBuilder()
                                .setCalling(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("unwrap"))
                                )
                                .addArgs(
                                    Expr.newBuilder()
                                        .setRef(RefExpr.newBuilder().setRef("of"))
                                )
                        )
                    )
            )
        ).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
        Assert.assertEquals(Ok("test"), res(listOf(Ok("test"))))
        Assert.assertEquals(
            Err(
                Panic.Type(
                    null, null, listOf(
                        StackCtx.Fn("unwrap", listOf(Err("test"))),
                        StackCtx.Syntax("CALL", listOf(), CodePosition(0, 0, "")),
                        StackCtx.Syntax(
                            "LAMBDA",
                            listOf(Err("test")),
                            CodePosition(0, 0, "")
                        )
                    )
                )
            ),
            res(listOf(Err("test")))
        )
    }
}