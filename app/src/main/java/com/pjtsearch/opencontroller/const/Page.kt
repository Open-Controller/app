package com.pjtsearch.opencontroller.const

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.House
import com.pjtsearch.opencontroller_lib_proto.Widget
import java.io.Serializable

sealed class Page : Serializable {
    abstract val title: String

    object Home : Page() { override val title = "Home"}
    object Settings : Page() { override val title = "Settings"}
    data class Controller(val controller: com.pjtsearch.opencontroller_lib_proto.Controller) : Page() { override val title: String = controller.displayName }
}

sealed class BackgroundPage : Serializable {
    object Homes : BackgroundPage()
    data class Rooms(val house: House, val executor: OpenControllerLibExecutor) : BackgroundPage()
}

sealed class BottomSheetPage : Serializable {
    data class Widgets(val widgets: List<Widget>) : BottomSheetPage()
    data class EditHouseRef(var houseRef: MutableState<HouseRef>, val index: Int) : BottomSheetPage()
    data class AddHouseRef(var houseRef: MutableState<HouseRef>) : BottomSheetPage()
}