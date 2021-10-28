package com.pjtsearch.opencontroller

import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.*
import org.junit.Assert
import org.junit.Test

class OpenControllerLibExecutorTest {
    @Test
    fun scope() {
        val executor = OpenControllerLibExecutor()
        val house = Expr.newBuilder().setHouse(
            HouseExpr.newBuilder()
                .setDisplayName(Expr.newBuilder().setString("Test"))
                .setId(Expr.newBuilder().setString("test"))
                .putDevices(
                    "Test",
                    Expr.newBuilder().setDevice(
                        DeviceExpr.newBuilder()
                            .putLambdas("test", Expr.newBuilder().setLambda(
                                LambdaExpr.newBuilder().setReturn(
                                    Expr.newBuilder().setCall(CallExpr.newBuilder().setCalling(
                                        Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("Test1_test"))
                                    ))
                                )
                            ).build())
                    ).build()
                )
                .putDevices(
                    "Test1",
                    Expr.newBuilder().setDevice(
                        DeviceExpr.newBuilder()
                            .putLambdas("test", Expr.newBuilder().setLambda(
                                LambdaExpr.newBuilder().setReturn(
                                    Expr.newBuilder().setString("123")
                                )
                            ).build())
                    ).build()
                )
                .putRooms(
                    "TestRoom",
                    Expr.newBuilder().setRoom(
                        RoomExpr.newBuilder()
                            .setDisplayName(Expr.newBuilder().setString("TestRoom"))
                            .setIcon(Expr.newBuilder().setString("ROOM"))
                            .putControllers("test", Expr.newBuilder().setController(
                                ControllerExpr.newBuilder()
                                    .setDisplayName(Expr.newBuilder().setString("TestController"))
                                    .setBrandColor(Expr.newBuilder().setString("#ffffff"))
                                    .setDisplayInterface(Expr.newBuilder().setDisplayInterface(
                                        DisplayInterfaceExpr.newBuilder()
                                            .addWidgets(Expr.newBuilder().setWidget(
                                                WidgetExpr.newBuilder()
                                                    .setWidgetType("test")
                                                    .putParams("test", Expr.newBuilder().setLambda(
                                                        LambdaExpr.newBuilder()
                                                            .setReturn(
                                                                Expr.newBuilder().setCall(CallExpr.newBuilder().setCalling(
                                                                    Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("Test_test"))
                                                                ))
                                                            )
                                                    ).build())
                                            ))
                                    ))
                            ).build())
                    ).build()
                ).build()
            ).build()
        val res = executor.interpretExpr<House>(house, mapOf(), null).unwrap()!!
        val fn = res.rooms["TestRoom"]!!.controllers["test"]!!.displayInterface!!.widgets[0].params["test"]!! as Fn
        Assert.assertEquals("123", fn(listOf(), res.scope))
    }

    @Test
    fun builtins() {
        val executor = OpenControllerLibExecutor()
        val lambda = Expr.newBuilder().setLambda(
        LambdaExpr.newBuilder()
            .setReturn(
                Expr.newBuilder().setCall(CallExpr.newBuilder()
                    .addArgs(Expr.newBuilder().setString("https://jsonplaceholder.typicode.com/posts/1"))
                    .addArgs(Expr.newBuilder().setString("GET"))
                    .setCalling(
                        Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("httpRequest"))
                    )
                )
            )
        ).build()
        val res = executor.interpretExpr<Fn>(lambda, mapOf(), null).unwrap()!!
        Assert.assertEquals("""
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """.trimIndent(), res(listOf(), null))
    }

    @Test
    fun args() {
        val executor = OpenControllerLibExecutor()
        val lambda = Expr.newBuilder().setLambda(
            LambdaExpr.newBuilder()
                .addArgs("method")
                .addArgs("postNumber")
                .setReturn(
                    Expr.newBuilder().setCall(CallExpr.newBuilder()
                        .addArgs(
                            Expr.newBuilder().setCall(CallExpr.newBuilder()
                                .addArgs(Expr.newBuilder().setString("https://jsonplaceholder.typicode.com/posts/"))
                                .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("postNumber")))
                                .setCalling(
                                    Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("concat"))
                                )
                            )
                        )
                        .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("method")))
                        .setCalling(
                            Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("httpRequest"))
                        )
                    )
                )
        ).build()
        val res = executor.interpretExpr<Fn>(lambda, mapOf(), null).unwrap()!!
        Assert.assertEquals("""
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """.trimIndent(), res(listOf("GET", "1"), null))
    }
}