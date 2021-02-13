package com.pjtsearch.opencontroller_lib

import com.beust.klaxon.Klaxon
import com.pjtsearch.opencontroller_lib.OpenController
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.Throws
import kotlin.jvm.JvmStatic
data class House(val name: String, val rooms: List<Room>, val devices: List<Device>)
data class Room(val name: String, val controllers: List<Controller>)
data class Controller(val name: String, val widgets: List<Any>)
data class Device(val id: String, val actions: List<Any>, val dynamic_values: List<Any>)

class OpenController(json: String) {
    private external fun from_json(json: String): String
    private external fun to_json(handle: String): String
    private external fun execute_action(handle: String, device: String, action: String)
    private external fun subscribe_dynamic_value(
        handle: String,
        device: String,
        dynamic_value: String
    ): String?

    private external fun unsubscribe_dynamic_value(value_handle: String?)
    private var listeners: MutableList<Listener> = ArrayList()
    fun event_cb(handle: String, device: String, dynamic_value: String, payload: String) {
        listeners.stream()
            .filter { l: Listener -> l == Listener(handle, device, dynamic_value) }
            .findAny()
            .ifPresent { l: Listener ->
                l.listeners.values.forEach(
                    Consumer { i: Consumer<String> -> i.accept(payload) })
            }
    }

    init {
        System.loadLibrary("opencontroller_lib")
    }

    private val handle: String
    fun getHouse(): House? {
        return Klaxon().parse<House>(to_json(handle))
    }

    fun executeAction(device: String, action: String) {
        execute_action(handle, device, action)
    }

    fun subscribeDynamicValue(
        device: String,
        dynamic_value: String,
        cb: Consumer<String>
    ): Runnable {
        val listener = listeners.stream()
            .filter { l: Listener -> l == Listener(handle, device, dynamic_value) }
            .findAny()
            .orElseGet {
                val l = Listener(
                    handle,
                    device,
                    dynamic_value,
                    subscribe_dynamic_value(handle, device, dynamic_value)
                )
                listeners.add(l)
                l
            }
        val id = UUID.randomUUID()
        listener.listeners[id] = cb
        return Runnable {
            if (listener.listeners.size == 1) {
                unsubscribe_dynamic_value(listener.dynamic_value_handle)
                listeners = listeners.stream()
                    .filter { l: Listener -> l != Listener(handle, device, dynamic_value) }
                    .collect(Collectors.toList())
            } else listener.listeners.remove(id)
        }
    }

    private class Listener {
        var handle: String
        var device: String
        var dynamic_value: String
        var dynamic_value_handle: String? = null
        var listeners: MutableMap<UUID, Consumer<String>>

        constructor(handle: String, device: String, dynamic_value: String) {
            this.handle = handle
            this.device = device
            this.dynamic_value = dynamic_value
            this.listeners = HashMap()
        }

        constructor(
            handle: String,
            device: String,
            dynamic_value: String,
            dynamic_value_handle: String?
        ) {
            this.handle = handle
            this.device = device
            this.dynamic_value = dynamic_value
            this.dynamic_value_handle = dynamic_value_handle
            this.listeners = HashMap()
        }

        override fun equals(o: Any?): Boolean {
            if (o === this) return true
            if (o !is Listener) return false
            val c = o
            return c.handle == handle && c.device == device && c.dynamic_value == dynamic_value
        }
    }

    init {
        handle = from_json(json)
    }
}