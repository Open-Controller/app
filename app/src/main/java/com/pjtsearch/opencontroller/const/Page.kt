package com.pjtsearch.opencontroller.const

import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.pjtsearch.opencontroller.*
import com.pjtsearch.opencontroller.settings.HouseRef
import java.io.Serializable

data class PageState @ExperimentalMaterialApi constructor(
    val frontPage: FrontPage,
    val backgroundPage: BackgroundPage,
    val bottomSheetPage: BottomSheetPage,
    val backdropValue: BackdropValue,
    val bottomSheetValue: ModalBottomSheetValue
)

sealed class FrontPage {
    abstract val title: String
    open val bottomIcon: ImageVector? = null
    open val bottomText: String? = null

    object EmptyGreeter : FrontPage() {
        override val title = "Welcome"
        override val bottomIcon = Icons.Outlined.AutoAwesome
        override val bottomText = "Select a Home"
    }

    //    For when house already opened
    data class HomeGreeter(val house: House) : FrontPage() {
        override val title = "Welcome"
    }

    object Settings : FrontPage() {
        override val title = "Settings"
    }

    data class Controller(val controller: com.pjtsearch.opencontroller.Controller, val houseScope: Map<String, Device>) :
        FrontPage() {
        override val title: String = controller.displayName
    }

//    fun serialize() =
//        when (val page = this) {
//            is EmptyGreeter -> listOf("EmptyGreeter")
//            is HomeGreeter -> listOf("HomeGreeter", page.house.toByteArray())
//            is Settings -> listOf("Settings")
//            is Controller -> listOf("Controller", page.controller.toByteArray())
//        }
//
//    companion object {
//        fun deserialize(from: List<Serializable>) =
//            when (from[0]) {
//                "EmptyGreeter" -> EmptyGreeter
//                "HomeGreeter" -> HomeGreeter(HouseExpr.parseFrom(from[1] as ByteArray))
//                "Controller" -> Controller(
//                    ControllerExpr.parseFrom(
//                        from[1] as ByteArray
//                    )
//                )
//                "Settings" -> Settings
//                else -> EmptyGreeter
//            }
//    }
}

sealed class BackgroundPage {
    object Homes : BackgroundPage()
    data class Rooms(val house: House) :
        BackgroundPage()

//    fun serialize() =
//        when (val page = this) {
//            is Homes -> listOf("Homes")
//            is Rooms -> listOf("Rooms", page.house.toByteArray())
//        }
//
//    companion object {
//        fun deserialize(from: List<Serializable>) =
//            when (from[0]) {
//                "Homes" -> Homes
//                "Rooms" -> HouseExpr.parseFrom(from[1] as ByteArray).let {
//                    Rooms(it, OpenControllerLibExecutor(it))
//                }
//                else -> Homes
//            }
//    }
}

sealed class BottomSheetPage {
    data class Widgets(val widgets: List<Widget>) : BottomSheetPage()
    data class EditHouseRef(var houseRef: MutableState<HouseRef>, val index: Int) :
        BottomSheetPage()

    data class AddHouseRef(var houseRef: MutableState<HouseRef>) : BottomSheetPage()

//    fun serialize() =
//        when (val page = this) {
//            is Widgets -> listOf(
//                "Widgets",
//                page.widgets.map { it.toByteArray() } as Serializable)
//            is EditHouseRef -> listOf("Rooms", page.houseRef.value.toByteArray(), page.index)
//            is AddHouseRef -> listOf("Rooms", page.houseRef.value.toByteArray())
//        }
//
//    companion object {
//        fun deserialize(from: List<Serializable>) =
//            when (from[0]) {
//                "Widgets" -> Widgets((from[1] as List<ByteArray>).map { WidgetExpr.parseFrom(it) })
//                "EditHouseRef" -> EditHouseRef(
//                    mutableStateOf(HouseRef.parseFrom(from[1] as ByteArray)),
//                    from[2] as Int
//                )
//                "AddHouseRef" -> AddHouseRef(mutableStateOf(HouseRef.parseFrom(from[1] as ByteArray)))
//                else -> Widgets(listOf())
//            }
//    }
}