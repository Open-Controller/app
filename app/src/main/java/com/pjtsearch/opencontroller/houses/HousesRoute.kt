package com.pjtsearch.opencontroller.houses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.extensions.resolveHouseRef
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.HousesMenu
import com.pjtsearch.opencontroller.ui.theme.shapes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HousesRoute(
    isExpandedScreen: Boolean,
    onHouseSelected: (HouseRef) -> Unit
) {
    val ctx = LocalContext.current
    val houseRefs = ctx.settingsDataStore.data.map {
        it.houseRefsList
    }.collectAsState(initial = listOf())
    Column(modifier = Modifier.systemBarsPadding()) {
        houseRefs.value.map {
            ListItem(
                Modifier
                    .padding(5.dp)
                    .padding(start = 20.dp)
                    .clip(shapes.small)
                    .clickable { onHouseSelected(it) }) {
                Text(it.displayName)
            }
        }
    }
}
