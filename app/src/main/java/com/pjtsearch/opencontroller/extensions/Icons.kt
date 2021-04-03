package com.pjtsearch.opencontroller.extensions

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.R
import com.pjtsearch.opencontroller_lib_proto.Icon
import com.pjtsearch.opencontroller_lib_proto.Size

var icons = hashMapOf(
        Icon.STAR to Icons.Outlined.Star,
        Icon.ARROW_UP to Icons.Outlined.KeyboardArrowUp,
        Icon.ARROW_DOWN to Icons.Outlined.KeyboardArrowDown,
        Icon.ARROW_LEFT to Icons.Outlined.KeyboardArrowLeft,
        Icon.ARROW_RIGHT to Icons.Outlined.KeyboardArrowRight,
        Icon.SELECT to Icons.Outlined.GpsFixed,
        Icon.VOLUME_UP to Icons.Outlined.VolumeUp,
        Icon.VOLUME_DOWN to Icons.Outlined.VolumeDown,
        Icon.VOLUME_MUTE to Icons.Outlined.VolumeOff,
        Icon.BACK to Icons.Outlined.Undo,
        Icon.ON to R.drawable.power_on,
        Icon.OFF to R.drawable.power_off,
        Icon.FORWARD to Icons.Outlined.FastForward,
        Icon.REVERSE to Icons.Outlined.FastRewind,
        Icon.INFO to Icons.Outlined.Info,
        Icon.HOME to Icons.Outlined.Home,
        Icon.CHANNEL_UP to Icons.Outlined.ArrowDropUp,
        Icon.CHANNEL_DOWN to Icons.Outlined.ArrowDropDown,
        Icon.CHANNEL to Icons.Outlined.Copyright,
        Icon.REMOVE to Icons.Outlined.Delete,
        Icon.STOP to Icons.Outlined.Stop,
        Icon.SKIP_FORWARD to Icons.Outlined.SkipNext,
        Icon.SKIP_REVERSE to Icons.Outlined.SkipPrevious,
        Icon.RECORD to Icons.Outlined.RadioButtonChecked,
        Icon.PAUSE to Icons.Outlined.Pause,
        Icon.PLAY to Icons.Outlined.PlayArrow,
        Icon.GUIDE to Icons.Outlined.Dashboard,
        Icon.DVR to Icons.Outlined.FiberDvr,
        Icon.MENU to Icons.Outlined.Menu,
        Icon.EXIT to Icons.Outlined.ExitToApp,
        Icon.OPTIONS to Icons.Outlined.Settings,
        Icon.ASTERISK to R.drawable.asterisk,
        Icon.POUND to Icons.Outlined.Tag,
        Icon.CLOSED_CAPTIONS to Icons.Outlined.ClosedCaption,
        Icon.GARAGE to Icons.Outlined.DirectionsCar,
        Icon.LAUNDRY to Icons.Outlined.LocalLaundryService,
        Icon.KITCHEN to Icons.Outlined.Countertops,
        Icon.NOOK to Icons.Outlined.BreakfastDining,
        Icon.FAMILY_ROOM to Icons.Outlined.Weekend,
        Icon.LIVING_ROOM to Icons.Outlined.Chair,
        Icon.MEDIA_ROOM to Icons.Outlined.Theaters,
        Icon.DINING_ROOM to Icons.Outlined.BrunchDining,
        Icon.ENTRY to Icons.Outlined.DoorSliding,
        Icon.MASTER_BEDROOM to Icons.Outlined.KingBed,
        Icon.EXERCISE_ROOM to Icons.Outlined.FitnessCenter,
        Icon.PLAY_ROOM to Icons.Outlined.Games,
        Icon.BEDROOM to Icons.Outlined.SingleBed,
        Icon.ROOM to Icons.Outlined.Room,
        Icon.MENU_ALT to Icons.Outlined.MoreHoriz,
        Icon.PLUS to Icons.Outlined.Add,
        Icon.MINUS to Icons.Outlined.Remove
)

@Composable
fun OpenControllerIcon(icon: Icon, text: String, size: Size? = Size.SMALL) =
        (when (size) {
                Size.SMALL -> 24.dp
                Size.MEDIUM -> 24.dp
                Size.LARGE -> 30.dp
                null -> 24.dp
        }).let { sz ->
                when (val iconValue = icons[icon] ?: throw Error("Could not find icon $icon")) {
                        is Int -> Icon(painterResource(iconValue), text, Modifier.size(sz))
                        is ImageVector -> Icon(iconValue, text, Modifier.size(sz))
                        else -> Text(text)
                }
        }
