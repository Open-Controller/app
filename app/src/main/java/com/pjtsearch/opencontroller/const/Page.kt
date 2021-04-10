package com.pjtsearch.opencontroller.const

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.ui.theme.typography
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.Controller
import com.pjtsearch.opencontroller_lib_proto.House
import com.pjtsearch.opencontroller_lib_proto.Widget
import java.io.Serializable

sealed class Page {
    abstract val title: String
    open val bottomIcon: ImageVector? = null
    open val bottomText: String? = null

    object EmptyGreeter : Page() {
        override val title = "Welcome"
        override val bottomIcon = Icons.Outlined.AutoAwesome
        override val bottomText = "Select a Home"
    }
//    For when house already opened
    data class HomeGreeter(val house: House) : Page() { override val title = "Welcome" }
    object Settings : Page() { override val title = "Settings"}
    data class Controller(val controller: com.pjtsearch.opencontroller_lib_proto.Controller) : Page() { override val title: String = controller.displayName }

    fun serialize() =
        when (val page = this) {
            is EmptyGreeter -> listOf("EmptyGreeter")
            is HomeGreeter -> listOf("HomeGreeter", page.house.toByteArray())
            is Settings -> listOf("Settings")
            is Controller -> listOf("Controller", page.controller.toByteArray())
        }
    companion object {
        fun deserialize(from: List<Serializable>) =
            when (from[0]) {
                "EmptyGreeter" -> EmptyGreeter
                "HomeGreeter" -> HomeGreeter(House.parseFrom(from[1] as ByteArray))
                "Controller" -> Controller(com.pjtsearch.opencontroller_lib_proto.Controller.parseFrom(from[1] as ByteArray))
                "Settings" -> Settings
                else -> EmptyGreeter
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
                "Rooms" -> House.parseFrom(from[1] as ByteArray).let {
                    Rooms(it, OpenControllerLibExecutor(it))
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
            is Widgets -> listOf("Widgets", page.widgets.map { it.toByteArray() } as Serializable)
            is EditHouseRef -> listOf("Rooms", page.houseRef.value.toByteArray(), page.index)
            is AddHouseRef -> listOf("Rooms", page.houseRef.value.toByteArray())
        }
    companion object {
        fun deserialize(from: List<Serializable>) =
            when (from[0]) {
                "Widgets" -> Widgets((from[1] as List<ByteArray>).map { Widget.parseFrom(it) })
                "EditHouseRef" -> EditHouseRef(
                    mutableStateOf(HouseRef.parseFrom(from[1] as ByteArray)),
                    from[2] as Int
                )
                "AddHouseRef" -> AddHouseRef(mutableStateOf(HouseRef.parseFrom(from[1] as ByteArray)))
                else -> Widgets(listOf())
            }
    }
}