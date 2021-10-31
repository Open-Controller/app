package com.pjtsearch.opencontroller.extensions

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.R

var icons = hashMapOf(
    "STAR" to Icons.Outlined.Star,
    "ARROW_UP" to Icons.Outlined.KeyboardArrowUp,
    "ARROW_DOWN" to Icons.Outlined.KeyboardArrowDown,
    "ARROW_LEFT" to Icons.Outlined.KeyboardArrowLeft,
    "ARROW_RIGHT" to Icons.Outlined.KeyboardArrowRight,
    "SELECT" to Icons.Outlined.GpsFixed,
    "VOLUME_UP" to Icons.Outlined.VolumeUp,
    "VOLUME_DOWN" to Icons.Outlined.VolumeDown,
    "VOLUME_MUTE" to Icons.Outlined.VolumeOff,
    "BACK" to Icons.Outlined.Undo,
    "ON" to R.drawable.power_on,
    "OFF" to R.drawable.power_off,
    "FORWARD" to Icons.Outlined.FastForward,
    "REVERSE" to Icons.Outlined.FastRewind,
    "INFO" to Icons.Outlined.Info,
    "HOME" to Icons.Outlined.Home,
    "CHANNEL_UP" to Icons.Outlined.ArrowDropUp,
    "CHANNEL_DOWN" to Icons.Outlined.ArrowDropDown,
    "CHANNEL" to Icons.Outlined.Copyright,
    "REMOVE" to Icons.Outlined.Delete,
    "STOP" to Icons.Outlined.Stop,
    "SKIP_FORWARD" to Icons.Outlined.SkipNext,
    "SKIP_REVERSE" to Icons.Outlined.SkipPrevious,
    "RECORD" to Icons.Outlined.RadioButtonChecked,
    "PAUSE" to Icons.Outlined.Pause,
    "PLAY" to Icons.Outlined.PlayArrow,
    "GUIDE" to Icons.Outlined.Dashboard,
    "DVR" to Icons.Outlined.FiberDvr,
    "MENU" to Icons.Outlined.Menu,
    "EXIT" to Icons.Outlined.ExitToApp,
    "OPTIONS" to Icons.Outlined.Settings,
    "ASTERISK" to R.drawable.asterisk,
    "POUND" to Icons.Outlined.Tag,
    "CLOSED_CAPTIONS" to Icons.Outlined.ClosedCaption,
    "GARAGE" to Icons.Outlined.DirectionsCar,
    "LAUNDRY" to Icons.Outlined.LocalLaundryService,
    "KITCHEN" to Icons.Outlined.Countertops,
    "NOOK" to Icons.Outlined.BreakfastDining,
    "FAMILY_ROOM" to Icons.Outlined.Weekend,
    "LIVING_ROOM" to Icons.Outlined.Chair,
    "MEDIA_ROOM" to Icons.Outlined.Theaters,
    "DINING_ROOM" to Icons.Outlined.BrunchDining,
    "ENTRY" to Icons.Outlined.DoorSliding,
    "MASTER_BEDROOM" to Icons.Outlined.KingBed,
    "EXERCISE_ROOM" to Icons.Outlined.FitnessCenter,
    "PLAY_ROOM" to Icons.Outlined.Games,
    "BEDROOM" to Icons.Outlined.SingleBed,
    "ROOM" to Icons.Outlined.Room,
    "MENU_ALT" to Icons.Outlined.MoreHoriz,
    "PLUS" to Icons.Outlined.Add,
    "MINUS" to Icons.Outlined.Remove
)

@Composable
fun OpenControllerIcon(icon: String, text: String, size: Int? = 0) =
    (when (size) {
        0 -> 24.dp
        1 -> 24.dp
        2 -> 30.dp
        else -> 24.dp
    }).let { sz ->
        when (val iconValue = icons[icon] ?: throw Error("Could not find icon $icon")) {
            is Int -> Icon(painterResource(iconValue), text, Modifier.size(sz))
            is ImageVector -> Icon(iconValue, text, Modifier.size(sz))
            else -> Text(text)
        }
    }
