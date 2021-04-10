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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.ui.theme.typography
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.House
import com.pjtsearch.opencontroller_lib_proto.Widget
import java.io.Serializable

sealed class Page : Serializable {
    abstract val title: String
    open val bottomContent: @Composable ColumnScope.() -> Unit = {}

    object EmptyGreeter : Page() {
        override val title = "Home"
        override val bottomContent: @Composable() (ColumnScope.() -> Unit) = {
            Icon(
                Icons.Outlined.AutoAwesome, "OpenController logo",
                Modifier.align(Alignment.CenterHorizontally).size(200.dp),
                MaterialTheme.colors.onSurface.copy(0.3f)
            )
            Text(
                "Select a Home",
                style = typography.h5,
                textAlign = TextAlign.Center
            )
        }
    }
//    For when house already opened
    data class HomeGreeter(val house: House) : Page() { override val title = "Home" }
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