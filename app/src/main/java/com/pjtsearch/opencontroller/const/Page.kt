package com.pjtsearch.opencontroller.const

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.House
import com.pjtsearch.opencontroller_lib_proto.Widget
import java.io.Serializable

sealed class Page {
    abstract val title: String

    object Home : Page() { override val title = "Home"}
    object Settings : Page() { override val title = "Settings"}
    data class Controller(val controller: com.pjtsearch.opencontroller_lib_proto.Controller) : Page() { override val title: String = controller.displayName }
    fun serialize() =
        when (val page = this) {
            is Home -> listOf("Home")
            is Settings -> listOf("Settings")
            is Controller -> listOf("Controller", page.controller.toByteArray())
        }
    companion object {
        fun deserialize(from: List<Serializable>) =
            when (from[0]) {
                "Home" -> Home
                "Controller" -> Controller(com.pjtsearch.opencontroller_lib_proto.Controller.parseFrom(from[1] as ByteArray))
                "Settings" -> Settings
                else -> Home
            }
    }
}

sealed class BackgroundPage {
    object Homes : BackgroundPage()
    data class Rooms(val house: House, val executor: OpenControllerLibExecutor) : BackgroundPage()
    fun serialize() =
        when (val page = this) {
            is Homes -> listOf("Homes")
            is Rooms -> listOf("Rooms", page.house.toByteArray())
        }
    companion object {
        fun deserialize(from: List<Serializable>) =
            when (from[0]) {
                "Homes" -> Homes
                "Rooms" -> {
                    val house = House.parseFrom(from[1] as ByteArray)
                    Rooms(house, OpenControllerLibExecutor(house))
                }
                else -> Homes
            }
    }
}

sealed class BottomSheetPage {
    data class Widgets(val widgets: List<Widget>) : BottomSheetPage()
    data class EditHouseRef(var houseRef: MutableState<HouseRef>, val index: Int) : BottomSheetPage()
    data class AddHouseRef(var houseRef: MutableState<HouseRef>) : BottomSheetPage()
    fun serialize() =
        when (val page = this) {
            is Widgets -> listOf("Widgets", page.widgets.map { w -> w.toByteArray() })
            is EditHouseRef -> listOf(
                "EditHouseRef",
                page.houseRef.value.toByteArray(),
                page.index
            )
            is AddHouseRef -> listOf("AddHouseRef", page.houseRef.value.toByteArray())
        }
    companion object {
        fun deserialize(from: List<Serializable>) =
            when (from[0]) {
                "Widgets" -> {
                    Widgets((from[1] as List<ByteArray>).map { w -> Widget.parseFrom(w)})
                }
                "EditHouseRef" -> {
                    EditHouseRef(mutableStateOf(HouseRef.parseFrom((from[1] as ByteArray))), from[2] as Int)
                }
                "AddHouseRef" -> AddHouseRef(mutableStateOf(HouseRef.parseFrom((from[1] as ByteArray))))
                else -> Widgets(listOf())
            }
    }
}