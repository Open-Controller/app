/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    "HOME" to R.drawable.home,
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

var houseIcons = hashMapOf(
    "GARAGE" to Icons.Outlined.DirectionsCar,
    "NOOK" to Icons.Outlined.BreakfastDining,
    "MEDIA_ROOM" to Icons.Outlined.Theaters,
    "DINING_ROOM" to Icons.Outlined.BrunchDining,
    "ROOM" to Icons.Outlined.Room,
    "HOME" to R.drawable.home,
    "HOUSE" to Icons.Outlined.House,
    "APARTMENT" to Icons.Outlined.Apartment,
    "COTTAGE" to Icons.Outlined.Cottage,
    "FACTORY" to Icons.Outlined.Factory,
    "WAREHOUSE" to Icons.Outlined.Warehouse,
    "VILLA" to Icons.Outlined.Villa,
    "CABIN" to Icons.Outlined.Cabin,
    "CHALET" to Icons.Outlined.Chalet,
    "HOUSEBOAT" to Icons.Outlined.Houseboat,
)

@Composable
fun OpenControllerIcon(
    icon: String, text: String, size: Int? = 0, iconSet: HashMap<String, *> = icons
) = (when (size) {
    0 -> 24.dp
    1 -> 26.dp
    2 -> 40.dp
    else -> 24.dp
}).let { sz ->
    when (val iconValue = iconSet[icon]) {
        is Int -> Icon(painterResource(iconValue), text, Modifier.size(sz))
        is ImageVector -> Icon(iconValue, text, Modifier.size(sz))
        else -> Text(text)
    }
}
