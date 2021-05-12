package com.pjtsearch.opencontroller_lib_android

import com.github.michaelbull.result.unwrap
import com.pjtsearch.opencontroller_lib_proto.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.Assert.*
import org.junit.Before
import org.junit.rules.Stopwatch
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class OpenControllerLibExecutorTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var executor: OpenControllerLibExecutor

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"test": 123}"""
        ))
        executor = OpenControllerLibExecutor(House.newBuilder().setId("").setDisplayName("")
            .addDevices(
                Device.newBuilder().setId("Test").addLambdas(
                    Lambda.newBuilder().setId("test").setHttp(HttpFunc.newBuilder()
                        .setMethod(HttpMethod.GET)
                        .setUrl(mockWebServer.url("/").toString())
                    ).build()
                )
            ).build())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testHttp() =
        executor.executeLambda(
            Lambda.newBuilder().setHttp(HttpFunc.newBuilder()
                .setMethod(HttpMethod.GET)
                .setUrl(mockWebServer.url("/").toString())
            ).build(),
            listOf()
        ).unwrap()
            .let { it as List<ByteArray>}
            .let { String(it[0]) }
            .let {
                assertEquals("""{"test": 123}""", it)
                assertEquals(1, mockWebServer.requestCount)
            }

    @Test
    fun testTCP() =
        executor.executeLambda(
            Lambda.newBuilder().setTcp(TCPFunc.newBuilder()
                .setCommand("")
                .setAddress("127.0.0.1:" + mockWebServer.port)
            ).build(),
            listOf()
        ).unwrap()
            .let {
                assertEquals(listOf<Nothing>(), it)
                assertEquals(1, mockWebServer.requestCount)
            }

    @Test
    fun testMacro() =
        executor.executeLambda(
            Lambda.newBuilder().setMacro(MacroFunc.newBuilder().addAllLambdas(listOf(
                Lambda.newBuilder().setTcp(TCPFunc.newBuilder()
                    .setCommand("")
                    .setAddress("127.0.0.1:" + mockWebServer.port)
                ).build(),
                Lambda.newBuilder().setHttp(HttpFunc.newBuilder()
                    .setMethod(HttpMethod.GET)
                    .setUrl(mockWebServer.url("/").toString())
                ).build()
            ))).build(),
            listOf()
        ).unwrap()
            .let {
                assertEquals(listOf<Nothing>(), it)
                assertEquals(2, mockWebServer.requestCount)
            }

    @Test
    fun testPipeArgs() =
        executor.executeLambda(
            Lambda.newBuilder().setPipeArgs(PipeArgsFunc.newBuilder().addAllLambdas(listOf(
                Lambda.newBuilder().setTcp(TCPFunc.newBuilder()
                    .setCommand("")
                    .setAddress("127.0.0.1:" + mockWebServer.port)
                ).build(),
                Lambda.newBuilder().setHttp(HttpFunc.newBuilder()
                    .setMethod(HttpMethod.GET)
                    .setUrl(mockWebServer.url("/").toString())
                ).build()
            ))).build(),
            listOf()
        ).unwrap()
            .let { it as List<ByteArray>}
            .let { String(it[0]) }
            .let {
                assertEquals("""{"test": 123}""", it)
                assertEquals(2, mockWebServer.requestCount)
            }

    @Test
    fun testDelay() =
        Pair(
            System.currentTimeMillis(), executor.executeLambda(
            Lambda.newBuilder().setMacro(MacroFunc.newBuilder().addAllLambdas(listOf(
                Lambda.newBuilder()
                    .setDelay(DelayFunc.newBuilder().setTime(2500)).build()
            ))).build(),
            listOf()
        ).unwrap())
            .let {
                assertThat(System.currentTimeMillis() - it.first,
                    allOf(greaterThan(2470), lessThan(2530)
                ))
            }

    @Test
    fun testRef() =
        executor.executeLambda(
            Lambda.newBuilder().setRef(RefFunc.newBuilder()
                .setDevice("Test")
                .setLambda("test")
            ).build(),
            listOf()
        ).unwrap()
            .let { it as List<ByteArray>}
            .let { String(it[0]) }
            .let {
                assertEquals("""{"test": 123}""", it)
                assertEquals(1, mockWebServer.requestCount)
            }

    @Test
    fun testString() = executor.executeLambda(
        Lambda.newBuilder().setString("test").build(),
        listOf()
    ).unwrap()
        .let { it as List<String>}
        .let { it[0] }
        .let {
            assertEquals("test", it)
        }

    @Test
    fun testConcatenate() =
        executor.executeLambda(
            Lambda.newBuilder().addAllArgs(listOf("1", "2"))
                .setConcatenate(ConcatenateFunc.newBuilder().build()).build(),
            listOf("tes", "t")
        ).unwrap()
            .let { it as List<String>}
            .let { it[0] }
            .let {
                assertEquals("test", it)
            }

    @Test
    fun testConcatenateArgs() =
        executor.executeLambda(
            Lambda.newBuilder()
                .setConcatenate(ConcatenateFunc.newBuilder()
                    .addAllStrings(listOf("tes", "t")).build()).build(),
            listOf()
        ).unwrap()
            .let { it as List<String>}
            .let { it[0] }
            .let {
                assertEquals("test", it)
            }

    @Test
    fun testPushStack() =
        executor.executeLambda(
            Lambda.newBuilder().setPipeArgs(PipeArgsFunc.newBuilder().addAllLambdas(listOf(
                Lambda.newBuilder().setPushStack(PushStackFunc.newBuilder()
                    .setLambda(Lambda.newBuilder().setString("tes").build())).build(),
                Lambda.newBuilder().addArgs("1").setPushStack(PushStackFunc.newBuilder()
                    .setLambda(Lambda.newBuilder().setString("t").build())).build(),
                Lambda.newBuilder().addAllArgs(listOf("1", "2"))
                    .setConcatenate(ConcatenateFunc.newBuilder().build()).build()
            ))),
            listOf()
        ).unwrap()
            .let { it as List<String>}
            .let { it[0] }
            .let {
                assertEquals("test", it)
            }

    @Test
    fun testPrependStack() =
        executor.executeLambda(
            Lambda.newBuilder().setPipeArgs(PipeArgsFunc.newBuilder().addAllLambdas(listOf(
                Lambda.newBuilder().setPushStack(PushStackFunc.newBuilder()
                    .setLambda(Lambda.newBuilder().setString("t").build())).build(),
                Lambda.newBuilder().addArgs("1").setPrependStack(PrependStackFunc.newBuilder()
                    .setLambda(Lambda.newBuilder().setString("tes").build())).build(),
                Lambda.newBuilder().addAllArgs(listOf("1", "2"))
                    .setConcatenate(ConcatenateFunc.newBuilder().build()).build()
            ))),
            listOf()
        ).unwrap()
            .let { it as List<String>}
            .let { it[0] }
            .let {
                assertEquals("test", it)
            }

    @Test
    fun testIsEqual() =
        listOf(
            executor.executeLambda(
                Lambda.newBuilder().addArgs("from")
                    .setIsEqual(IsEqualFunc.newBuilder().setToString("test")),
                listOf("test")
            ).unwrap(),
            executor.executeLambda(
                Lambda.newBuilder().addArgs("from")
                    .setIsEqual(IsEqualFunc.newBuilder().setToFloat(1.5f)),
                listOf(1.5f)
            ).unwrap(),
            executor.executeLambda(
                Lambda.newBuilder()
                    .setIsEqual(IsEqualFunc.newBuilder().setFromFloat(1.5f).setToFloat(1.5f)),
                listOf()
            ).unwrap(),
            executor.executeLambda(
                Lambda.newBuilder().addArgs("to")
                    .setIsEqual(IsEqualFunc.newBuilder().setFromFloat(1.5f)),
                listOf(1.5f)
            ).unwrap(),
        )
            .let { it as List<List<Boolean>>}
            .let {
                it.forEach { a -> assertTrue(a[0]) }
            }

    @Test
    fun testSwitch() =
        Lambda.newBuilder().addArgs("input").setSwitch(SwitchFunc.newBuilder()
            .addAllConditions(listOf(
                Conditional.newBuilder()
                    .setIf(Lambda.newBuilder().addArgs("input")
                        .setIsEqual(IsEqualFunc.newBuilder()
                            .setToInt32(1).build()
                        )
                    )
                    .setThen(Lambda.newBuilder().setString("1"))
                    .build(),
                Conditional.newBuilder()
                    .setIf(Lambda.newBuilder().addArgs("input")
                        .setIsEqual(IsEqualFunc.newBuilder()
                            .setToInt32(2).build()
                        )
                    )
                    .setThen(Lambda.newBuilder().setString("2"))
                    .build()
            ))
        ).build()
            .let {listOf(
                executor.executeLambda(it, listOf(1)).unwrap(),
                executor.executeLambda(it, listOf(2)).unwrap()
            )}
            .let { it as List<List<String>> }
            .let {
                assertEquals("1", it[0][0])
                assertEquals("2", it[1][0])
            }

    @Test
    fun testGetProp() =
        executor.executeLambda(
            Lambda.newBuilder().addArgs("input")
                .setGetProp(GetPropFunc.newBuilder().setProp("test").build()).build(),
            listOf(hashMapOf("test" to 1))
        ).unwrap()
            .let { it as List<Int>}
            .let { it[0] }
            .let {
                assertEquals(1, it)
            }

    @Test
    fun testInt32() =
        executor.executeLambda(
            Lambda.newBuilder().setInt32(10).build(),
            listOf()
        ).unwrap()
            .let { it as List<Int>}
            .let { it[0] }
            .let {
                assertEquals(10, it)
            }

    @Test
    fun testInt64() =
        executor.executeLambda(
            Lambda.newBuilder().setInt64(10).build(),
            listOf()
        ).unwrap()
            .let { it as List<Int>}
            .let { it[0] }
            .let {
                assertEquals(10, it)
            }


    @Test
    fun testFloat() =
        executor.executeLambda(
            Lambda.newBuilder().setFloat(10.0f).build(),
            listOf()
        ).unwrap()
            .let { it as List<Float>}
            .let { it[0] }
            .let {
                assertEquals(10.0f, it)
            }

    @Test
    fun testGetIndex() =
        executor.executeLambda(
            Lambda.newBuilder().addArgs("input")
                .setGetIndex(GetIndexFunc.newBuilder().setIndex(1).build()).build(),
            listOf(listOf("test", "test1"))
        ).unwrap()
            .let { it as List<String>}
            .let { it[0] }
            .let {
                assertEquals("test1", it)
            }

    @Test
    fun testGetIndexInput() =
        executor.executeLambda(
            Lambda.newBuilder().addAllArgs(listOf("input", "index"))
                .setGetIndex(GetIndexFunc.newBuilder().build()).build(),
            listOf(listOf("test", "test1"), 1)
        ).unwrap()
            .let { it as List<String>}
            .let { it[0] }
            .let {
                assertEquals("test1", it)
            }
}