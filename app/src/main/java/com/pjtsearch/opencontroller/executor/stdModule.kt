package com.pjtsearch.opencontroller.executor

import com.github.kittinunf.fuel.*
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.pjtsearch.opencontroller.extensions.toResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.net.Socket
import java.util.*

fun getStdModule(
    getSocket: (String) -> Socket?,
    updateSocket: (String, Socket) -> Unit,
    removeSocket: (String) -> Unit
) = Device(
    mapOf(
        "httpRequest" to { args: List<Any> ->
            fnCtx("httpRequest", args) {
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
            }
        },
        "tcpRequest" to { args: List<Any> ->
            fnCtx("tcpRequest", args) {
                val (host, port) = (args[0] as String).split(":")
                var client: Socket? = null
                try {
                    client =
                        getSocket("$host:$port") ?: Socket(host, port.toInt()).let { s ->
                            updateSocket("$host:$port", s)
                            s
                        }
                } catch (err: Exception) {
                    err.printStackTrace()
                }
                val command = args[1] as String
                client?.outputStream?.write((command + "\r\n").toByteArray())
                Thread.sleep(300)
                client?.close()
                removeSocket("$host:$port")
                Ok(Unit)
            }
        },
        "observeTime" to { args: List<Any> ->
            fnCtx("observeTime", args) {
                val interval = args[0] as Number
                Ok(flow {
                    while (true) {
                        emit(Date().time)
                        delay(interval.toLong())
                    }
                })
            }
        },
    )
)
