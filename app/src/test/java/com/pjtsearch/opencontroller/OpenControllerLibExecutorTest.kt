package com.pjtsearch.opencontroller

import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.*
import org.junit.Assert
import org.junit.Test

class OpenControllerLibExecutorTest {
//    TODO: Use modules
//    @Test
//    fun scope() {
//        val executor = OpenControllerLibExecutor()
//        val house = Expr.newBuilder().setHouse(
//            HouseExpr.newBuilder()
//                .setDisplayName(Expr.newBuilder().setString("Test"))
//                .setId(Expr.newBuilder().setString("test"))
//                .putDevices(
//                    "Test",
//                    Expr.newBuilder().setDevice(
//                        DeviceExpr.newBuilder()
//                            .putLambdas("test", Expr.newBuilder().setLambda(
//                                LambdaExpr.newBuilder().setReturn(
//                                    Expr.newBuilder().setCall(CallExpr.newBuilder().setCalling(
//                                        Expr.newBuilder().setCall(CallExpr.newBuilder()
//                                            .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("getLambda")))
//                                            .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("Test1")))
//                                            .addArgs(Expr.newBuilder().setString("test"))
//                                        )
//                                    ))
//                                )
//                            ).build())
//                    ).build()
//                )
//                .putDevices(
//                    "Test1",
//                    Expr.newBuilder().setDevice(
//                        DeviceExpr.newBuilder()
//                            .putLambdas("test", Expr.newBuilder().setLambda(
//                                LambdaExpr.newBuilder().setReturn(
//                                    Expr.newBuilder().setString("123")
//                                )
//                            ).build())
//                    ).build()
//                )
//                .putRooms(
//                    "TestRoom",
//                    Expr.newBuilder().setRoom(
//                        RoomExpr.newBuilder()
//                            .setDisplayName(Expr.newBuilder().setString("TestRoom"))
//                            .setIcon(Expr.newBuilder().setString("ROOM"))
//                            .putControllers("test", Expr.newBuilder().setController(
//                                ControllerExpr.newBuilder()
//                                    .setDisplayName(Expr.newBuilder().setString("TestController"))
//                                    .setBrandColor(Expr.newBuilder().setString("#ffffff"))
//                                    .setDisplayInterface(Expr.newBuilder().setDisplayInterface(
//                                        DisplayInterfaceExpr.newBuilder()
//                                            .addWidgets(Expr.newBuilder().setWidget(
//                                                WidgetExpr.newBuilder()
//                                                    .setWidgetType("test")
//                                                    .putParams("test", Expr.newBuilder().setLambda(
//                                                        LambdaExpr.newBuilder()
//                                                            .setReturn(
//                                                                Expr.newBuilder().setCall(CallExpr.newBuilder().setCalling(
//                                                                    Expr.newBuilder().setCall(CallExpr.newBuilder()
//                                                                        .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("getLambda")))
//                                                                        .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("Test1")))
//                                                                        .addArgs(Expr.newBuilder().setString("test"))
//                                                                    )
//                                                                ))
//                                                            )
//                                                    ).build())
//                                            ))
//                                    ))
//                            ).build())
//                    ).build()
//                ).build()
//            ).build()
//        val res = executor.interpretExpr<House>(house, mapOf(), mapOf(), null).unwrap()!!
//        val fn = res.rooms["TestRoom"]!!.controllers["test"]!!.displayInterface!!.widgets[0].params["test"]!! as Fn
//        Assert.assertEquals("123", fn(listOf()))
//    }

    @Test
    fun closureScope() {
        val executor = OpenControllerLibExecutor()
        val lambda = Expr.newBuilder().setLambda(
            LambdaExpr.newBuilder().addArgs("arg").setReturn(
                Expr.newBuilder().setCall(CallExpr.newBuilder().setCalling(
                    Expr.newBuilder().setLambda(LambdaExpr.newBuilder().setReturn(
                        Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("arg"))
                    ))
                ))
            )
        ).build()
        val res = executor.interpretExpr(lambda, mapOf(), mapOf()).unwrap() as Fn
        Assert.assertEquals("123", res(listOf("123")))
    }

    @Test
    fun module() {
        val executor = OpenControllerLibExecutor()
        val module = Module.newBuilder()
            .putImports("otherModule", Module.newBuilder().setBody(Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder().addArgs("arg").setReturn(
                    Expr.newBuilder().setCall(CallExpr.newBuilder()
                        .setCalling(
                            Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("+"))
                        )
                        .addArgs(Expr.newBuilder().setString("added "))
                        .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("arg")))
                    )
                )
            )).build())
            .setBody(Expr.newBuilder().setLambda(
                LambdaExpr.newBuilder().setReturn(
                    Expr.newBuilder().setCall(
                        CallExpr.newBuilder()
                            .setCalling(
                                Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("otherModule"))
                            )
                            .addArgs(Expr.newBuilder().setString("123"))
                    )
                )
            ).build())
        val res = executor.interpretModule(module).unwrap() as Fn
        Assert.assertEquals("added 123", res(listOf()))
    }

    @Test
    fun builtins() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(Expr.newBuilder().setLambda(
        LambdaExpr.newBuilder()
            .setReturn(
                Expr.newBuilder().setCall(CallExpr.newBuilder()
                    .addArgs(Expr.newBuilder().setString("https://jsonplaceholder.typicode.com/posts/1"))
                    .addArgs(Expr.newBuilder().setString("GET"))
                    .setCalling(
                        Expr.newBuilder().setCall(CallExpr.newBuilder()
                            .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("std")))
                            .addArgs(Expr.newBuilder().setString("httpRequest"))
                            .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("getLambda")))
                        )
                    )
                )
            )
        )).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
        Assert.assertEquals("""
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """.trimIndent(), res(listOf()))
    }

    @Test
    fun args() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(Expr.newBuilder().setLambda(
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
                                    Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("+"))
                                )
                            )
                        )
                        .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("method")))
                        .setCalling(
                            Expr.newBuilder().setCall(CallExpr.newBuilder()
                                .addArgs(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("std")))
                                .addArgs(Expr.newBuilder().setString("httpRequest"))
                                .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("getLambda")))
                            )
                        )
                    )
                )
        )).build()
        val res = executor.interpretModule(lambda).unwrap() as Fn
        Assert.assertEquals("""
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """.trimIndent(), res(listOf("GET", "1")))
    }


    @Test
    fun list() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(Expr.newBuilder().setCall(
            CallExpr.newBuilder()
                .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("listOf")))
                .addArgs(Expr.newBuilder().setString("1"))
                .addArgs(Expr.newBuilder().setString("2"))
                .addArgs(Expr.newBuilder().setString("3"))
        )).build()
        val res = executor.interpretModule(lambda).unwrap() as List<String>
        Assert.assertEquals(listOf("1", "2", "3"), res)
    }

    @Test
    fun map() {
        val executor = OpenControllerLibExecutor()
        val lambda = Module.newBuilder().setBody(Expr.newBuilder().setCall(
            CallExpr.newBuilder()
                .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("mapOf")))
                .addArgs(Expr.newBuilder().setCall(CallExpr.newBuilder()
                    .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("pair")))
                    .addArgs(Expr.newBuilder().setString("1"))
                    .addArgs(Expr.newBuilder().setString("a"))
                ))
                .addArgs(Expr.newBuilder().setCall(CallExpr.newBuilder()
                    .setCalling(Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("pair")))
                    .addArgs(Expr.newBuilder().setString("2"))
                    .addArgs(Expr.newBuilder().setString("b"))
                ))
        )).build()
        val res = executor.interpretModule(lambda).unwrap() as Map<String, String>
        Assert.assertEquals(mapOf("1" to "a", "2" to "b"), res)
    }
}