package com.pjtsearch.opencontroller_lib_android

import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

class OpenControllerLibExecutorTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var executor: OpenControllerLibExecutor

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(
                    """{"test": 123}"""
                )
        )
        executor = OpenControllerLibExecutor()
//            House.newBuilder().setId("").setDisplayName("")
//                .addDevices(
//                    Device.newBuilder().setId("Test").putAllLambdas(
//                        mapOf(
//                            Pair(
//                                "test",
//                                LambdaExpr.newBuilder().addAllArgs(listOf())
//                                    .setReturn(Expr.newBuilder().setString("test")).build()
//                            )
//                        )
//                    )
//                ).build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLambda() =
        executor.interpretExpr(
            Expr.newBuilder()
                .setLambda(LambdaExpr.newBuilder().setReturn(Expr.newBuilder().setString("test"))),
            mapOf(),
            mapOf()
        ).unwrap()
            .let {
                assertEquals(
                    LambdaExpr.newBuilder().setReturn(Expr.newBuilder().setString("test")).build(),
                    it
                )
            }

    @Test
    fun testCallLambda() =
        executor.interpretExpr(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder().setCalling(
                    Expr.newBuilder()
                        .setLambda(
                            LambdaExpr.newBuilder().setReturn(Expr.newBuilder().setString("test"))
                        )
                )
            ),
            mapOf(),
            mapOf()
        ).unwrap()
            .let {
                assertEquals(
                    "test",
                    it
                )
            }

    @Test
    fun testRef() =
        executor.interpretExpr(
            Expr.newBuilder().setRef(
                RefExpr.newBuilder().setRef("test")
            ),
            mapOf(Pair("test", 123F)),
            mapOf()
        ).unwrap()
            .let {
                assertEquals(
                    123F,
                    it
                )
            }

    @Test
    fun testCallLambdaArgs() =
        executor.interpretExpr(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder().addAllArgs(listOf(Expr.newBuilder().setInt32(123).build()))
                    .setCalling(
                        Expr.newBuilder()
                            .setLambda(
                                LambdaExpr.newBuilder().setReturn(
                                    Expr.newBuilder().setRef(RefExpr.newBuilder().setRef("test"))
                                ).addAllArgs(listOf("test"))
                            )
                    )
            ),
            mapOf(),
            mapOf()
        ).unwrap()
            .let {
                assertEquals(
                    123,
                    it
                )
            }

    @Test
    fun testNestedCall() =
        executor.interpretExpr(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder().addAllArgs(listOf(Expr.newBuilder().setInt32(123).build()))
                    .setCalling(
                        Expr.newBuilder()
                            .setLambda(
                                LambdaExpr.newBuilder().setReturn(
                                    Expr.newBuilder().setCall(
                                        CallExpr.newBuilder().addAllArgs(
                                            listOf(
                                                Expr.newBuilder()
                                                    .setRef(RefExpr.newBuilder().setRef("test"))
                                                    .build()
                                            )
                                        )
                                            .setCalling(
                                                Expr.newBuilder()
                                                    .setLambda(
                                                        LambdaExpr.newBuilder().setReturn(
                                                            Expr.newBuilder().setRef(
                                                                RefExpr.newBuilder().setRef("test")
                                                            )
                                                        ).addAllArgs(listOf("test"))
                                                    )
                                            )
                                    )
                                ).addAllArgs(listOf("test"))
                            )
                    )
            ),
            mapOf(),
            mapOf()
        ).unwrap()
            .let {
                assertEquals(
                    123,
                    it
                )
            }

    @Test
    fun testCallNative() =
        executor.interpretExpr(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder().addAllArgs(listOf(Expr.newBuilder().setInt32(123).build()))
                    .setCalling(
                        Expr.newBuilder()
                            .setRef(
                                RefExpr.newBuilder().setRef("nativeFn")
                            )
                    )
            ),
            mapOf(),
            mapOf(Pair("nativeFn") { args: List<Any> -> args[0] as Int + 2 }),
        ).unwrap()
            .let {
                assertEquals(
                    125,
                    it
                )
            }

    @Test
    fun testNestedCallScope() =
        executor.interpretExpr(
            Expr.newBuilder().setCall(
                CallExpr.newBuilder().addAllArgs(listOf(Expr.newBuilder().setInt32(123).build()))
                    .setCalling(
                        Expr.newBuilder()
                            .setLambda(
                                LambdaExpr.newBuilder().setReturn(
                                    Expr.newBuilder().setCall(
                                        CallExpr.newBuilder()
                                            .setCalling(
                                                Expr.newBuilder()
                                                    .setLambda(
                                                        LambdaExpr.newBuilder().setReturn(
                                                            Expr.newBuilder().setRef(
                                                                RefExpr.newBuilder().setRef("test")
                                                            )
                                                        )
                                                    )
                                            )
                                    )
                                ).addAllArgs(listOf("test"))
                            )
                    )
            ),
            mapOf(),
            mapOf()
        ).unwrap()
            .let {
                assertEquals(
                    null,
                    it
                )
            }

//    @Test
//    fun testHttp() =
//        executor.executeLambda(
//            Lambda.newBuilder().setHttp(HttpFunc.newBuilder()
//                .setMethod(HttpMethod.GET)
//                .setUrl(mockWebServer.url("/").toString())
//            ).build(),
//            listOf()
//        ).unwrap()
//            .let { it as List<ByteArray>}
//            .let { String(it[0]) }
//            .let {
//                assertEquals("""{"test": 123}""", it)
//                assertEquals(1, mockWebServer.requestCount)
//            }
}